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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

    public static void send(Email email) {
        String from = email.getFrom();
        Properties prop = new Properties();
        if (from.endsWith("hotmail.com") || from.endsWith("outlook.com")) {
            prop.put("mail.smtp.host", "smtp.office365.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.starttls.enable","true");
            prop.put("mail.smtp.auth", "true");
        } else if (from.endsWith("gmail.com")) {
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "465");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.socketFactory.port", "465");
            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else if (from.endsWith("163.com")) {
            prop.put("mail.smtp.host", "smtp.163.com");
            prop.put("mail.smtp.port", "465");
            prop.put("mail.smtp.starttls.enable","true");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.socketFactory.port", "465");
            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else if (from.endsWith("mailgun.org") || from.endsWith("wuchong.me")) {
            prop.put("mail.smtp.host", "smtp.mailgun.org");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.socketFactory.port", "465");
            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email.getUsername(), email.getPassword());
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getFrom(), "Flink CI"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email.getTo())
            );
            message.setSubject(email.getSubject());
            message.setText(email.getContent());
            message.setHeader("Content-Type", "text/html; charset=utf-8");
            message.setHeader("Content-Transfer-Encoding", "quoted-printable");

            LOG.trace("Sending Email: '"+ email.getSubject() + "'");
            Transport.send(message);
            LOG.info("Send Email Successfully: '"+ email.getSubject() + "'");
        } catch (Exception e) {
            LOG.error("Send Email failed: '" + email.getSubject() + "'" , e);
        }
    }
}
