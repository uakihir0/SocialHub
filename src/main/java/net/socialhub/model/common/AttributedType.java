package net.socialhub.model.common;

import java.util.function.Function;
import java.util.regex.Matcher;

public interface AttributedType {

    /** 属性の種類を取得 */
    AttributedKind getKind();

    /** 正規表現を取得 */
    String getRegex();

    /** 表示文字列を取得 */
    String getDisplayedText(Matcher m);

    /** 文字列を取得 */
    String getExpandedText(Matcher m);

    /**
     * AttributedType のデフォルト実装
     */
    class CommonAttributedType implements AttributedType {

        private AttributedKind kind;

        private String regex;

        private Function<Matcher, String> display;

        private Function<Matcher, String> expand;

        public CommonAttributedType(
                AttributedKind kind,
                String regex,
                Function<Matcher, String> display,
                Function<Matcher, String> expand) {

            this.display = display;
            this.expand = expand;
            this.regex = regex;
            this.kind = kind;
        }

        public CommonAttributedType(
                AttributedKind kind,
                String regex) {

            this(kind, regex,
                    Matcher::group,
                    Matcher::group);
        }

        @Override
        public AttributedKind getKind() {
            return kind;
        }

        @Override
        public String getRegex() {
            return regex;
        }

        @Override
        public String getDisplayedText(Matcher m) {
            if (display != null) {
                return display.apply(m);
            }
            // 未定義の場合は全体
            return m.group();
        }

        @Override
        public String getExpandedText(Matcher m) {
            if (expand != null) {
                return expand.apply(m);
            }
            // 未定義
            return null;
        }
    }
}
