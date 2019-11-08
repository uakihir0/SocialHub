package net.socialhub.define.service.slack;

import net.socialhub.define.AttributedTypes;
import net.socialhub.model.common.AttributedType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

import static net.socialhub.define.service.slack.SlackAttributedTypes.Regex.SLACK_FULL_URL_REGEX;
import static net.socialhub.define.service.slack.SlackAttributedTypes.Regex.SLACK_MAIL_REGEX;
import static net.socialhub.define.service.slack.SlackAttributedTypes.Regex.SLACK_MENTION_REGEX;
import static net.socialhub.model.common.AttributedKind.Account;
import static net.socialhub.model.common.AttributedKind.EMail;
import static net.socialhub.model.common.AttributedKind.Link;

public class SlackAttributedTypes {

    private static Function<Matcher, String> GET_INNER_STRING = //
            (m) -> (m.groupCount() > 0) ? m.group(1) : m.group();

    public static AttributedType fullLink =
            new AttributedType.CommonAttributedType(Link, SLACK_FULL_URL_REGEX, //
                    SlackAttributedTypes::getLinkDisplayText, GET_INNER_STRING);

    public static AttributedType email =
            new AttributedType.CommonAttributedType(EMail, SLACK_MAIL_REGEX, //
                    GET_INNER_STRING, GET_INNER_STRING);

    public static AttributedType mention =
            new AttributedType.CommonAttributedType(Account, SLACK_MENTION_REGEX, //
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
        public static final String SLACK_FULL_URL_REGEX = "<(" + AttributedTypes.Regex.FULL_URL_REGEX + "\\|?(.*?))>";

        /** Slack での Mention の正規表現 */
        public static final String SLACK_MENTION_REGEX = "<(@[a-zA-Z0-9]+)>";

        /** Slack での Mail の正規表現 */
        public static final String SLACK_MAIL_REGEX =
                "<mailto:(" + AttributedTypes.Regex.SIMPLE_EMAIL_REGEX + ")\\|" + AttributedTypes.Regex.SIMPLE_EMAIL_REGEX + ">";
    }
}
