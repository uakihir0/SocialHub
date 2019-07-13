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


    /**
     * AttributedType のデフォルト実装
     */
    class CommonAttributedType implements AttributedType {

        private AttributedKind kind;

        private String regex;

        private Function<Matcher, String> converter;

        public CommonAttributedType(
                AttributedKind kind,
                String regex,
                Function<Matcher, String> converter) {

            this.converter = converter;
            this.regex = regex;
            this.kind = kind;
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
            if (converter != null) {
                return converter.apply(m);
            }
            // 未定義の場合は全体
            return m.group();
        }
    }
}
