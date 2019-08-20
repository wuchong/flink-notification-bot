package com.github.wuchong.flink.notification.bot;

import com.github.wuchong.flink.notification.bot.util.DurationFormatter;
import org.joda.time.Period;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DurationFormatterTest {

    @Test
    public void testDurationFormatter() {
        assertFormat(Period.hours(3).plusMinutes(1).plusSeconds(10), "3 hrs, 1 min, 10 secs");
        assertFormat(Period.hours(1).plusMinutes(33).plusSeconds(0), "1 hour, 33 mins");
        assertFormat(Period.hours(2).plusSeconds(11), "2 hrs, 11 secs");
        assertFormat(Period.hours(1).plusMinutes(1).plusSeconds(1), "1 hour, 1 min, 1 sec");
    }

    private void assertFormat(Period period, String format) {
        assertEquals(format, DurationFormatter.format(period.toStandardDuration().getStandardSeconds()));
    }

}