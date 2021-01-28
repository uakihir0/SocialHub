package net.socialhub.apis.addition;

import net.socialhub.service.mastodon.MastodonMapper;
import org.junit.Test;

public class MastodonMapperTest {

    @Test
    public void testMastodonDate(){
        System.out.println(MastodonMapper.parseDate("2020-10-10T01:01:01.200Z"));
        System.out.println(MastodonMapper.parseDate("2020-10-10T01:01:01.200+09:00"));
    }
}
