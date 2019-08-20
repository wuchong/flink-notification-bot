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

import com.github.wuchong.flink.notification.bot.travis.BuildResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.wuchong.flink.notification.bot.util.ResourceUtils.readResourceFile;
import static org.junit.Assert.assertEquals;

public class BuildResultTest {

    @Test
    public void testPayloadParse() throws Exception {
        Map<String, String> result = BuildResult.parse(readResourceFile("failed.payload"));
        Map<String, String> expected = new HashMap<>();
        expected.put("build_status_capital", "Failed");
        expected.put("short_commit", "d5e3853");
        expected.put("author", "Jark Wu");
        expected.put("commit_message", "remove license to fail fast");
        expected.put("repository", "flink");
        expected.put("changeset", "https://github.com/apache/flink/compare/9955bb0afda2%5E...d5e3853681bf");
        expected.put("duration", "1 min, 36 secs");
        expected.put("repo_username_link", "https://travis-ci.org/apache/flink");
        expected.put("branch_name_link", "https://github.com/apache/flink/tree/builds");
        expected.put("branch_name", "builds");
        expected.put("build_number", "40122");
        expected.put("gravatar_url", "https://secure.gravatar.com/avatar/77efb4eed4c1292d514ac18a6e31b19c.jpg");
        expected.put("build_status", "failed");
        expected.put("build_url", "https://travis-ci.org/apache/flink/builds/574145594");
        expected.put("account", "apache");
        assertEquals(expected, result);
    }

}