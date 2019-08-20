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

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BuildResultTest {

    @Test
    public void testPayloadParse() throws Exception {
        File file = new File(getClass().getResource("/failed.payload").getFile());
        List<String> line = Files.readLines(file, StandardCharsets.UTF_8);
        Map<String, String> result = BuildResult.parse(line.get(0));
        String expected = "{" +
                "short_commit=d5e3853, " +
                "author=Jark Wu, " +
                "commit_message=remove license to fail fast, " +
                "repository=flink, " +
                "changeset=https://github.com/apache/flink/compare/9955bb0afda2%5E...d5e3853681bf, " +
                "duration=1 min, 36 secs, " +
                "repo_username_link=https://travis-ci.org/apache/flink, " +
                "branch_name_link=https://github.com/apache/flink/tree/builds, " +
                "branch_name=builds, " +
                "build_number=40122, " +
                "gravatar_url=https://secure.gravatar.com/avatar/77efb4eed4c1292d514ac18a6e31b19c.jpg, " +
                "build_status=failed, " +
                "build_url=https://travis-ci.org/apache/flink/builds/574145594, " +
                "account=apache}";

        assertEquals(expected, result.toString());
    }

}