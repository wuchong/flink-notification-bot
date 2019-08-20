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

package com.github.wuchong.flink.notification.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;
import de.bripkens.gravatar.Rating;

import java.net.URLDecoder;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BuildResult {

    private static final Cache<String, String> GRAVATAR_CACHE = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofDays(30))
            .build();

    // payload['status_message'] or payload['result_message']
    private final String result;
    // payload['number']
    private final String buildNumber;
    // payload['repository']['owner_name']
    private final String account;
    // payload['repository']['name']
    private final String repository;
    // payload['branch']
    private final String branch;
    // substr(payload['commit'], 0, 7)
    private final String shortCommit;
    // payload['commit']
    private final String commit;
    // payload['message']
    private final String commitMessage;
    private final String authorName;
    private final long duration; // seconds
    private final String buildUrl;
    private final String compareUrl;
    private final String gravatarUrl;


    public BuildResult(String result, String buildNumber, String account, String repository, String branch, String commit, String commitMessage, String authorName, long duration, String buildUrl, String compareUrl, String gravatarUrl) {
        this.result = result;
        this.buildNumber = buildNumber;
        this.account = account;
        this.repository = repository;
        this.branch = branch;
        this.commit = commit;
        this.shortCommit = commit.substring(0, 7);
        this.commitMessage = commitMessage;
        this.authorName = authorName;
        this.duration = duration;
        this.buildUrl = buildUrl;
        this.compareUrl = compareUrl;
        this.gravatarUrl = gravatarUrl;
    }

    public String getRepoURL() {
        return "https://github.com/" + account + "/" + repository;
    }

    public String getRepoBranchURL() {
        return getRepoURL() + "/tree/" + branch;
    }

    public String getCommitURL() {
        return getRepoURL() + "/commit/" + commit;
    }

    public String getPrettyDuration() {
        return DurationFormatter.format(duration);
    }

    @Override
    public String toString() {
        return "BuildResult{" +
                "result='" + result + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                ", account='" + account + '\'' +
                ", repository='" + repository + '\'' +
                ", branch='" + branch + '\'' +
                ", shortCommit='" + shortCommit + '\'' +
                ", commit='" + commit + '\'' +
                ", commitMessage='" + commitMessage + '\'' +
                ", authorName='" + authorName + '\'' +
                ", duration=" + duration +
                ", buildUrl='" + buildUrl + '\'' +
                ", compareUrl='" + compareUrl + '\'' +
                ", gravatarUrl='" + gravatarUrl + '\'' +
                ", repoUrl='" + getRepoURL() + '\'' +
                ", repoBranchUrl='" + getRepoBranchURL() + '\'' +
                ", commitUrl='" + getCommitURL() + '\'' +
                ", prettyDuration='" + getPrettyDuration() + '\'' +
                '}';
    }

    private static final String PAYLOAD_PREFIX = "payload=";


    @SuppressWarnings("unchecked")
    public static Map<String, String> parse(String payload) throws Exception {
        String decoded = URLDecoder.decode(payload);
        String json;
        if (decoded.startsWith(PAYLOAD_PREFIX)) {
            json = decoded.substring(PAYLOAD_PREFIX.length());
        } else {
            throw new PayloadParseException("payload is not start with 'payload='.");
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class);
        return convertToBuildResult(map);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> convertToBuildResult(Map<String, Object> map) {
        String result = (String) map.get("status_message");
        if (result == null) {
            result = (String) map.get("result_message");
        }
        String buildNumber = (String) map.get("number");
        Map<String, String> repoMap = (Map<String, String>) map.get("repository");
        String account = repoMap.get("owner_name"); // apache
        String repository = repoMap.get("name");    // flink
        String branch = (String) map.get("branch");
        String commit = (String) map.get("commit");
        String commitMessage = (String) map.get("message");
        String authorName = (String) map.get("author_name");
        String authorEmail = (String) map.get("author_email");
        int duration = (Integer) map.get("duration");
        String buildUrl = (String) map.get("build_url");
        String compareUrl = (String) map.get("compare_url");
        if (compareUrl != null) {
            compareUrl = compareUrl.replaceAll("\\^", "%5E");
        }
        String gravatarUrl = GRAVATAR_CACHE.getIfPresent(authorEmail);
        if (gravatarUrl == null) {
            String url = new Gravatar()
                    .setSize(50)
                    .setHttps(true)
                    .setRating(Rating.PARENTAL_GUIDANCE_SUGGESTED)
                    .setStandardDefaultImage(DefaultImage.MONSTER)
                    .getUrl(authorEmail);
            if (url == null) {
                gravatarUrl = "https://secure.gravatar.com/avatar/";
            } else {
                gravatarUrl = url.substring(0, url.indexOf("?"));
            }
            GRAVATAR_CACHE.put(authorEmail, gravatarUrl);
        }
        String repoUrl = "https://github.com/" + account + "/" + repository;
        String travisUrl = "https://travis-ci.org/" + account + "/" + repository;
        String branchUrl = repoUrl + "/tree/" + branch;
        String commitUrl = repoUrl + "/commit/" + commit;

        Map<String, String> builds = new HashMap<>();
        builds.put("repo_username_link", travisUrl);
        builds.put("account", account);
        builds.put("repository", repository);
        builds.put("branch_name_link", branchUrl);
        builds.put("branch_name", branch);
        builds.put("build_url", buildUrl);
        builds.put("build_number", buildNumber);
        builds.put("build_status", result.toLowerCase());
        builds.put("duration", DurationFormatter.format(duration));
        builds.put("author", authorName);
        builds.put("gravatar_url", gravatarUrl);
        builds.put("changeset", compareUrl);
        builds.put("short_commit", commit.substring(0, 7));
        builds.put("commit_message", commitMessage);
        return builds;

    }

}
