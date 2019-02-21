package net.socialhub.model.common;

import net.socialhub.define.AttributeEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String With Attributes
 * 属性付き文字列
 */
public class AttributedString {

    /** URL の正規表現 */
    private static final String FULL_URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    /** URL の正規表現 */
    private static final String SHORT_URL_REGEX = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    /** EMail の簡易的な正規表現 */
    private static final String SIMPLE_EMAIL_REGEX = "[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+";

    /** 電話番号 (国際対応) の正規表現 */
    private static final String SIMPLE_PHONE_REGEX = "[0\\+\\(][\\d\\-\\(\\)]{9,16}";

    /** ハッシュタグ (国際対応) の正規表現 */
    private static final String HASH_TAG_REGEX = "[#＃][A-Za-z0-9_À-ÖØ-öø-ÿĀ-ɏɓ-ɔɖ-ɗəɛɣɨɯɲʉʋʻ̀-ͯḀ-ỿЀ-ӿԀ-ԧⷠ-ⷿꙀ-֑ꚟ-ֿׁ-ׂׄ-ׇׅא-תװ-״\uFB12-ﬨשׁ-זּטּ-לּמּנּ-סּףּ-פּצּ-ﭏؐ-ؚؠ-ٟٮ-ۓە-ۜ۞-۪ۨ-ۯۺ-ۼۿݐ-ݿࢠࢢ-ࢬࣤ-ࣾﭐ-ﮱﯓ-ﴽﵐ-ﶏﶒ-ﷇﷰ-ﷻﹰ-ﹴﹶ-ﻼ\u200Cก-ฺเ-๎ᄀ-ᇿ\u3130-ㆅꥠ-\uA97F가-\uD7AFힰ-\uD7FFﾡ-ￜァ-ヺー-ヾｦ-ﾟｰ０-９Ａ-Ｚａ-ｚぁ-ゖ゙-ゞ㐀-\u4DBF一-\u9FFF꜀-뜿띀-렟\uF800-﨟〃々〻]+";

    /** Mastodon アカウントの正規表現 */
    private static final String MASTODON_ACCOUNT_REGEX = "@[a-zA-Z0-9_]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*";

    /** Twitter アカウントの正規表現 */
    private static final String TWITTER_ACCOUNT_REGEX = "@[a-zA-Z0-9_]{2,}";

    private String text;

    private String displayText;

    private List<AttributeEnum> kinds;

    private List<AttributedElements> attribute;

    private List<AttributedElements> displayAttribute;

    /**
     * Attributed String
     * (属性文字列に変換)
     */
    public AttributedString(String text) {
        this(text, AttributeEnum.all());
    }

    /**
     * Attributed String
     * (属性文字列に変換)
     */
    public AttributedString(String text, List<AttributeEnum> kinds) {
        this.displayAttribute = null;
        this.attribute = null;
        this.kinds = kinds;
        this.text = text;

        // 無指定の場合は全部
        if (kinds == null) {
            this.kinds = AttributeEnum.all();
        }
    }

    /**
     * Get Attributes (with calc)
     * アトリビュートを取得
     * (この際に計算が実行される)
     */
    public List<AttributedElements> getAttribute() {
        if (attribute != null) {
            return attribute;
        }

        // 初期化
        attribute = new ArrayList<>();

        // リンクを取得 (プロトコル含む)
        scanElements(AttributeEnum.Link, FULL_URL_REGEX);

        // Mastodon アカウントを取得
        scanElements(AttributeEnum.MastodonAccount, MASTODON_ACCOUNT_REGEX);

        // Email を取得
        scanElements(AttributeEnum.Email, SIMPLE_EMAIL_REGEX);

        // リンクを取得 (プロトコル含めず)
        scanElements(AttributeEnum.Link, SHORT_URL_REGEX);

        // 電話番号を取得
        scanElements(AttributeEnum.Phone, SIMPLE_PHONE_REGEX);

        // ハッシュタグを取得
        scanElements(AttributeEnum.HashTag, HASH_TAG_REGEX);

        // Twitter アカウントを取得
        scanElements(AttributeEnum.TwitterAccount, TWITTER_ACCOUNT_REGEX);

        // 範囲の開始順にソート
        attribute.sort(Comparator.comparingInt(e -> e.getRange().getStart()));

        return attribute;
    }

    /**
     * Get Display Attributes (with calc)
     * 表示向け Attribute の計算
     */
    public List<AttributedElements> getDisplayAttribute() {
        if (displayAttribute != null) {
            return displayAttribute;
        }

        // 初期化
        String tmp = text;
        List<AttributedElements> elements = getAttribute().stream() //
                .map(AttributedElements::copy) //
                .collect(Collectors.toList());

        for (int i = 0; i < elements.size(); i++) {
            AttributedElements elem = elements.get(i);

            // 表示するテキストを変更
            String before = tmp.substring(0, elem.getRange().getStart());
            String after = tmp.substring(elem.getRange().getEnd());
            tmp = (before + elem.getDisplayText() + after);

            // レンジ幅を変えて属性を修正
            int diff = (elem.getText().length() - elem.getDisplayText().length());
            elem.getRange().setEnd(elem.getRange().getEnd() - diff);
            elem.setText(elem.getDisplayText());

            // 後の属性に対してもレンジ幅を変更
            for (int j = (i + 1); j < elements.size(); j++) {
                AttributedElements next = elements.get(j);
                next.getRange().setStart(next.getRange().getStart() - diff);
                next.getRange().setEnd(next.getRange().getEnd() - diff);
            }
        }

        displayText = tmp;
        displayAttribute = elements;
        return displayAttribute;
    }

    /**
     * 表示向けテキストの計算
     */
    public String getDisplayText() {
        if (displayText != null) {
            return displayText;
        }

        getDisplayAttribute();
        return displayText;
    }

    /**
     * 未使用レンジかとうかの確認
     */
    private void scanElements(AttributeEnum attributeType, String regex) {
        if (kinds.contains(attributeType)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);

            while (m.find()) {

                // 範囲が被っていない事を確認
                if (this.isUnusedRange(m)) {
                    AttributedElements element = new AttributedElements();
                    element.setRange(new AttributedRange(m));
                    element.setType(attributeType);
                    element.setText(m.group());
                    attribute.add(element);
                }
            }
        }
    }

    /**
     * 未使用レンジかとうかの確認
     */
    private boolean isUnusedRange(Matcher m) {
        return attribute.stream().noneMatch(elem -> {

            // 範囲が被っているかを確認
            AttributedRange range = elem.getRange();
            return ((range.getStart() <= m.start()) && (m.start() < range.getEnd())) || //
                    ((m.start() <= range.getStart()) && (range.getStart() < m.end()));
        });
    }

    @Override
    public String toString() {
        return this.text;
    }

    //region // Getter&Setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.displayAttribute = null;
        this.attribute = null;

        this.displayText = null;
        this.text = text;
    }

    public List<AttributeEnum> getKinds() {
        return kinds;
    }

    public void setKinds(List<AttributeEnum> kinds) {
        this.displayAttribute = null;
        this.attribute = null;

        this.displayText = null;
        this.kinds = kinds;
    }
    //endregion

    /**
     * Attributes Elements
     * 属性情報
     */
    public static class AttributedElements {

        private AttributeEnum type;

        /** オリジナルテキスト */
        private String text;

        /** 表示するテキスト */
        private String displayText;

        /** 実際に処理するテキスト */
        private String expandedText;

        private AttributedRange range;

        public AttributedElements copy() {
            AttributedElements model = new AttributedElements();

            model.setType(type);
            model.setText(text);
            model.setDisplayText(displayText);
            model.setExpandedText(expandedText);
            model.setRange(range.copy());
            return model;
        }

        //region // Getter&Setter
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDisplayText() {
            if (displayText != null) {
                return displayText;
            }
            return text;
        }

        public void setDisplayText(String displayText) {
            this.displayText = displayText;
        }

        public String getExpandedText() {
            if (expandedText != null) {
                return expandedText;
            }
            return text;
        }

        public void setExpandedText(String expandedText) {
            this.expandedText = expandedText;
        }

        public AttributeEnum getType() {
            return type;
        }

        public void setType(AttributeEnum type) {
            this.type = type;
        }

        public AttributedRange getRange() {
            return range;
        }

        public void setRange(AttributedRange range) {
            this.range = range;
        }
        //endregion
    }

    /**
     * Attributes Range
     * 文字列レンジ情報
     */
    public static class AttributedRange {

        public AttributedRange(Matcher m) {
            this.start = m.start();
            this.end = m.end();
        }

        public AttributedRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public AttributedRange copy() {
            return new AttributedRange(start, end);
        }

        /** Included Index */
        private int start;

        /** Excluded Index */
        private int end;

        public int getLength() {
            return (end - start);
        }

        //region // Getter&Setter
        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
        //endregion
    }
}
