package net.socialhub.all.utils;

import net.socialhub.core.utils.SnowflakeUtil;
import org.junit.Ignore;
import org.junit.Test;

public class SnowflakeUtilTest {

    @Test
    @Ignore
    public void testTwitterIdToDate() {
        Long id = 0L;

        net.socialhub.core.utils.SnowflakeUtil util = SnowflakeUtil.ofTwitter();
        System.out.println(util.getDateTimeFromID(id));
        System.out.println(util.getDateTimeFromID(util.addHoursToID(id, 1L)));
    }
}
