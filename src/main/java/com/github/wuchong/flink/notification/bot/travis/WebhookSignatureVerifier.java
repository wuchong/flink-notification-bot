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

package com.github.wuchong.flink.notification.bot.travis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;

public class WebhookSignatureVerifier {

    private static final Cache<String, String> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(Duration.ofDays(1))
            .build();

    private static final String TRAVIS_CONFIG_URL = "https://api.travis-ci.com/config";
    private static Signature SIGNATURE;
    private static KeyFactory KEY_FACTORY;

    static {
        try {
            SIGNATURE = Signature.getInstance("SHA1withRSA");
            KEY_FACTORY = KeyFactory.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static final String KEY = "key";

    @SuppressWarnings("unchecked")
    public static String getPublicKey() throws IOException {
        String publicKey = CACHE.getIfPresent(KEY);
        if (publicKey == null) {
            String content = readStringFromURL(TRAVIS_CONFIG_URL);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(content, Map.class);
            Map<String, Object> config = (Map<String, Object>) map.get("config");
            Map<String, Object> notifications = (Map<String, Object>) config.get("notifications");
            Map<String, Object> webhook = (Map<String, Object>) notifications.get("webhook");
            publicKey = (String) webhook.get("public_key");
            CACHE.put(KEY, publicKey);
        }
        return publicKey;
    }

    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public static boolean verifySignature(String data, byte[] signature) throws Exception {
        String keyString = getPublicKey();
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyString.getBytes(StandardCharsets.UTF_8));
        PublicKey publicKey = KEY_FACTORY.generatePublic(publicKeySpec);
        SIGNATURE.initVerify(publicKey);
        SIGNATURE.update(data.getBytes());
        return (SIGNATURE.verify(signature));
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getPublicKey());
    }

}
