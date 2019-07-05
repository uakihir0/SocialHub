package net.socialhub.utils;

import org.junit.Ignore;
import org.junit.Test;

public class SnowflakeUtilTest {

    @Test
    @Ignore
    public void testTwitterIdToDate() {
        Long id = 0L;

        SnowflakeUtil util = SnowflakeUtil.ofTwitter();
        System.out.println(util.getDateTimeFromID(id));
        System.out.println(util.getDateTimeFromID(util.addHoursToID(id, 1L)));
    }
}
