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

import com.github.wuchong.flink.notification.bot.email.EmailConfig;
import com.github.wuchong.flink.notification.bot.email.EmailSender;
import com.github.wuchong.flink.notification.bot.travis.TravisPostHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class FlinkHttpServer {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Email setting: from={}, to={}", EmailConfig.INSTANCE.getUsername(), EmailConfig.INSTANCE.getTo());
        System.out.println(Arrays.toString(args));
        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/travis", new TravisPostHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
    }
}
