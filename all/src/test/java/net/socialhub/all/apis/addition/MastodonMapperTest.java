package net.socialhub.all.apis.addition;

import net.socialhub.mastodon.action.MastodonMapper;
import org.junit.Test;

public class MastodonMapperTest {

    @Test
    public void testMastodonDate() {

        System.out.println(MastodonMapper.parseDate("2020-10-10T01:01:01.200Z"));
        System.out.println(MastodonMapper.parseDate("2020-10-10T01:01:01.200+09:00"));
        // PixelFed
        System.out.println(MastodonMapper.parseDate("2020-10-10T01:01:01+00:00"));
        System.out.println(MastodonMapper.parseDate("2020-10-10T01:01:01.000000+00:00"));
    }
}
