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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {

    public static final EmailConfig INSTANCE = load();

    private final String username;
    private final String password;
    private final String to;

    public EmailConfig(String username, String password, String to) {
        this.username = username;
        this.password = password;
        this.to = to;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTo() {
        return to;
    }

    private static EmailConfig load() {
        Properties prop = new Properties();
        try {
            InputStream config = EmailConfig.class.getResourceAsStream("/config.properties");
            if(config == null) {
                throw new RuntimeException("Unable to load /config.properties from the CL. CP: " + System.getProperty("java.class.path"));
            }
            prop.load(config);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load /config.properties from the CL", e);
        }
        return new EmailConfig(
                prop.getProperty("email.username"),
                prop.getProperty("email.password"),
                prop.getProperty("email.to"));
    }
}
