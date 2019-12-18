package net.socialhub.define;

import net.socialhub.model.common.AttributedType;
import net.socialhub.model.common.AttributedType.CommonAttributedType;
import net.socialhub.utils.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static net.socialhub.define.AttributedTypes.Regex.*;
import static net.socialhub.model.common.AttributedKind.*;

public final class AttributedTypes {
    private AttributedTypes() {
    }

    // Commons
    public static final AttributedType link =
            new CommonAttributedType(LINK, FULL_URL_REGEX, //
                    m -> StringUtil.getDisplayUrl(m.group()), Matcher::group);

    public static final AttributedType email =
            new CommonAttributedType(EMAIL, SIMPLE_EMAIL_REGEX);
    public static final AttributedType phone =
            new CommonAttributedType(PHONE, SIMPLE_PHONE_REGEX);
    public static final AttributedType hashTag =
            new CommonAttributedType(HASH_TAG, HASH_TAG_REGEX);

    // Identify
    public static final AttributedType twitter =
            new CommonAttributedType(ACCOUNT, TWITTER_ACCOUNT_REGEX);
    public static final AttributedType mastodon =
            new CommonAttributedType(ACCOUNT, MASTODON_ACCOUNT_REGEX);

    public static List<AttributedType> simple() {
        return Arrays.asList(
                AttributedTypes.link,
                AttributedTypes.mastodon,
                AttributedTypes.email,
                AttributedTypes.phone,
                AttributedTypes.hashTag,
                AttributedTypes.twitter);
    }

    public static class Regex {
        private Regex() {
        }

        /** URL の正規表現 */
        public static final String FULL_URL_REGEX = "(https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*))";

        /** EMail の簡易的な正規表現 */
        public static final String SIMPLE_EMAIL_REGEX = "([a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+)";

        /** 電話番号 (国際対応) の正規表現 */
        public static final String SIMPLE_PHONE_REGEX = "([0\\+\\(][\\d\\-\\(\\)]{9,16})";

        /** ハッシュタグ (国際対応) の正規表現 */
        public static final String HASH_TAG_REGEX = "([#＃][A-Za-z0-9_À-ÖØ-öø-ÿĀ-ɏɓ-ɔɖ-ɗəɛɣɨɯɲʉʋʻ̀-ͯḀ-ỿЀ-ӿԀ-ԧⷠ-ⷿꙀ-֑ꚟ-ֿׁ-ׂׄ-ׇׅא-תװ-״\uFB12-ﬨשׁ-זּטּ-לּמּנּ-סּףּ-פּצּ-ﭏؐ-ؚؠ-ٟٮ-ۓە-ۜ۞-۪ۨ-ۯۺ-ۼۿݐ-ݿࢠࢢ-ࢬࣤ-ࣾﭐ-ﮱﯓ-ﴽﵐ-ﶏﶒ-ﷇﷰ-ﷻﹰ-ﹴﹶ-ﻼ\u200Cก-ฺเ-๎ᄀ-ᇿ\u3130-ㆅꥠ-\uA97F가-\uD7AFힰ-\uD7FFﾡ-ￜァ-ヺー-ヾｦ-ﾟｰ０-９Ａ-Ｚａ-ｚぁ-ゖ゙-ゞ㐀-\u4DBF一-\u9FFF꜀-뜿띀-렟\uF800-﨟〃々〻]+)";

        /** Mastodon アカウントの正規表現 */
        public static final String MASTODON_ACCOUNT_REGEX = "(@[a-zA-Z0-9_]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)";

        /** Twitter アカウントの正規表現 */
        public static final String TWITTER_ACCOUNT_REGEX = "(@[a-zA-Z0-9_]{2,})";
    }
}
