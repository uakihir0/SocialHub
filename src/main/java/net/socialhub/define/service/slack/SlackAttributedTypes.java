package net.socialhub.define.service.slack;

import net.socialhub.model.common.AttributedType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

import static net.socialhub.define.AttributedTypes.Regex.FULL_URL_REGEX;
import static net.socialhub.define.AttributedTypes.Regex.SIMPLE_EMAIL_REGEX;
import static net.socialhub.define.service.slack.SlackAttributedTypes.Regex.*;
import static net.socialhub.model.common.AttributedKind.*;

public final class SlackAttributedTypes {
    private SlackAttributedTypes() {
    }

    private static final Function<Matcher, String> GET_INNER_STRING = //
            m -> (m.groupCount() > 0) ? m.group(1) : m.group();

    public static final AttributedType fullLink =
            new AttributedType.CommonAttributedType(LINK, SLACK_FULL_URL_REGEX, //
                    SlackAttributedTypes::getLinkDisplayText, GET_INNER_STRING);

    public static final AttributedType email =
            new AttributedType.CommonAttributedType(EMAIL, SLACK_MAIL_REGEX, //
                    GET_INNER_STRING, GET_INNER_STRING);

    public static final AttributedType mention =
            new AttributedType.CommonAttributedType(ACCOUNT, SLACK_MENTION_REGEX, //
                    GET_INNER_STRING, GET_INNER_STRING);

    public static List<AttributedType> simple() {
        return Arrays.asList( //
                SlackAttributedTypes.fullLink, //
                SlackAttributedTypes.email, //
                SlackAttributedTypes.mention);
    }

    // 表示用のリンクを生成
    private static String getLinkDisplayText(Matcher m) {
        if (m.groupCount() > 4) {
            String title = m.group(5);
            if (title != null && !title.isEmpty()) {
                return title;
            }
            String first = m.group(1);
            if (first != null && !first.isEmpty()) {
                return first;
            }
        }
        return m.group();
    }

    public static class Regex {

        /** Slack での URL の正規表現 */
        public static final String SLACK_FULL_URL_REGEX = "<(" + FULL_URL_REGEX + "\\|?(.*?))>";

        /** Slack での Mention の正規表現 */
        public static final String SLACK_MENTION_REGEX = "<(@[a-zA-Z0-9]+)>";

        /** Slack での Mail の正規表現 */
        public static final String SLACK_MAIL_REGEX =
                "<mailto:(" + SIMPLE_EMAIL_REGEX + ")\\|" + SIMPLE_EMAIL_REGEX + ">";
    }
}
