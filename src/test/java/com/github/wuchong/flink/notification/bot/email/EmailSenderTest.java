package com.github.wuchong.flink.notification.bot.email;

import com.github.wuchong.flink.notification.bot.travis.BuildResult;
import org.junit.Test;

import java.util.Map;

import static com.github.wuchong.flink.notification.bot.util.ResourceUtils.readResourceFile;

public class EmailSenderTest {

    @Test
    public void testSend() throws Exception {
        Map<String, String> result = BuildResult.parse(readResourceFile("failed.payload"));
        Email email = EmailGenerator.createEmail(result);
        EmailSender.send(email);
    }

}