/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.wuchong.flink.notification.bot.email;

import com.github.wuchong.flink.notification.bot.util.ResourceUtils;

import java.util.HashMap;
import java.util.Map;

public class EmailGenerator {

    private static final String PASSED = "passed";
    private static final String FIXED = "fixed";
    private static final String BROKEN = "broken";
    private static final String FAILED = "failed";
    private static final String STILL_FAIL = "still failing";
    private static final String ERRORED = "errored";
    private static final String CANCELED = "canceled";
    private static final String PENDING = "pending";

    private static final String COLOR_PASS = "#32D282";
    private static final String COLOR_FAIL = "#DB4545";
    private static final String COLOR_CANCEL = "#666766";

    private static final String BACKGROUND_PASS = "rgba(50, 210, 130, 0.1)";
    private static final String BACKGROUND_FAIL = "rgba(219, 69, 69, 0.1)";
    private static final String BACKGROUND_CANCEL = "rgba(102, 103, 102, 0.1)";

    private static final String ICON_PASS = "status-passed.png";
    private static final String ICON_FAIL = "status-failed.png";
    private static final String ICON_CANCEL = "status-errored.png";

    private static final String ARROW_PASS = "success-arrow.png";
    private static final String ARROW_FAIL = "failure-arrow.png";
    private static final String ARROW_CANCEL = "error-arrow.png";

    public static Map<String, String> getStyles(String status) {
        String color;
        String background;
        String icon;
        String arrow;
        switch (status.toLowerCase()) {
            case PASSED:
            case FIXED:
                color = COLOR_PASS;
                background = BACKGROUND_PASS;
                icon = ICON_PASS;
                arrow = ARROW_PASS;
                break;
            case BROKEN:
            case FAILED:
            case STILL_FAIL:
            case ERRORED:
                color = COLOR_FAIL;
                background = BACKGROUND_FAIL;
                icon = ICON_FAIL;
                arrow = ARROW_FAIL;
                break;
            case CANCELED:
            case PENDING:
                color = COLOR_CANCEL;
                background = BACKGROUND_CANCEL;
                icon = ICON_CANCEL;
                arrow = ARROW_CANCEL;
                break;
            default:
                throw new IllegalArgumentException("Unsupported status: " + status);
        }
        Map<String, String> style = new HashMap<>();
        style.put("color", color);
        style.put("background", background);
        style.put("icon", icon);
        style.put("arrow", arrow);
        return style;
    }

    // --------------------------------------------------------------------------------------------

    public static boolean isCanceled(Map<String, String> data) {
        String status = data.get("build_status");
        return status.equalsIgnoreCase(CANCELED) || status.equalsIgnoreCase(PENDING);
    }

    public static boolean isPassed(Map<String, String> data) {
        String status = data.get("build_status");
        return status.equalsIgnoreCase(PASSED);
    }

    public static boolean isApacheFlink(Map<String, String> data) {
        String account = data.get("account");
        String repository = data.get("repository");
        return "apache".equals(account) && "flink".equals(repository);
    }

    public static boolean isMasterOrReleaseBranch(Map<String, String> data) {
        String branch = data.get("branch_name");
        if (branch != null) {
            return branch.equals("master") || branch.startsWith("release-");
        } else {
            return false;
        }
    }

    public static String generateSubject(Map<String, String> data) {
        String status = data.get("build_status_capital");
        String apache = data.get("account");
        String flink = data.get("repository");
        String number = data.get("build_number");
        String branch = data.get("branch_name");
        String commit = data.get("short_commit");
        String title = status + ": " +
                apache + "/" + flink +
                "#" + number +
                " (" + branch + " - " + commit + ")";
        if ("cron".equalsIgnoreCase(data.get("type"))) {
            return "[CRON] " + title;
        } else {
            return title;
        }
    }

    public static String generateHTML(Map<String, String> data) {
        String status = data.get("build_status");
        Map<String, String> styles = getStyles(status);
        Map<String, String> fullData = new HashMap<>();
        fullData.putAll(data);
        fullData.putAll(styles);

        // fill data
        String html = ResourceUtils.readResourceFile("template.html");
        for (Map.Entry<String, String> entry : fullData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            html = html.replaceAll("\\$\\{" + key + "}", value);
        }
        return html;
    }

    // --------------------------------------------------------------------------------------------

    public static Email createEmail(Map<String, String> data) {
        EmailConfig config = EmailConfig.INSTANCE;
        String subject = generateSubject(data);
        String html = generateHTML(data);
        return new Email(
                config.getUsername(),
                config.getPassword(),
                config.getUsername(),
                config.getTo(),
                subject,
                html);

    }

}
