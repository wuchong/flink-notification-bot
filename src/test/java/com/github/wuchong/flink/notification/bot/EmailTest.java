package com.github.wuchong.flink.notification.bot;

import com.github.wuchong.flink.notification.bot.email.EmailGenerator;
import com.github.wuchong.flink.notification.bot.travis.BuildResult;
import org.junit.Test;

import java.util.Map;

import static com.github.wuchong.flink.notification.bot.util.ResourceUtils.readResourceFile;
import static org.junit.Assert.assertEquals;

public class EmailTest {

    @Test
    public void testHTML() throws Exception {
        Map<String, String> result = BuildResult.parse(readResourceFile("failed.payload"));
        assertEquals("Failed: apache/flink#40122 (builds - d5e3853)", EmailGenerator.generateSubject(result));
        assertEquals(readResourceFile("failed.html"), EmailGenerator.generateHTML(result));
    }

}