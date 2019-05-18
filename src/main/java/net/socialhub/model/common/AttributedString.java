package net.socialhub.model.common;

import net.socialhub.define.AttributeType;
import net.socialhub.utils.StringUtil;

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

    private List<AttributeType> kinds;

    private List<AttributedElement> attribute;

    private List<AttributedElement> displayAttribute;

    /**
     * Attributed String
     * (属性文字列に変換)
     */
    public AttributedString(String text) {
        this(text, AttributeType.all());
    }

    /**
     * Attributed String
     * (属性文字列に変換)
     */
    public AttributedString(String text, List<AttributeType> kinds) {
        this.displayAttribute = null;
        this.attribute = null;
        this.kinds = kinds;
        this.text = text;

        // 無指定の場合は全部
        if (kinds == null) {
            this.kinds = AttributeType.all();
        }
    }

    /**
     * Get Attributes (with calc)
     * アトリビュートを取得
     * (この際に計算が実行される)
     */
    public List<AttributedElement> getAttribute() {
        if (attribute != null) {
            return attribute;
        }

        // 初期化
        attribute = new ArrayList<>();

        // リンクを取得 (プロトコル含む)
        scanElements(AttributeType.Link, FULL_URL_REGEX);

        // Mastodon アカウントを取得
        scanElements(AttributeType.MastodonAccount, MASTODON_ACCOUNT_REGEX);

        // Email を取得
        scanElements(AttributeType.Email, SIMPLE_EMAIL_REGEX);

        // リンクを取得 (プロトコル含めず)
        scanElements(AttributeType.Link, SHORT_URL_REGEX);

        // 電話番号を取得
        scanElements(AttributeType.Phone, SIMPLE_PHONE_REGEX);

        // ハッシュタグを取得
        scanElements(AttributeType.HashTag, HASH_TAG_REGEX);

        // Twitter アカウントを取得
        scanElements(AttributeType.TwitterAccount, TWITTER_ACCOUNT_REGEX);

        // 範囲の開始順にソート
        attribute.sort(Comparator.comparingInt(e -> e.getRange().getStart()));

        return attribute;
    }

    /**
     * Get Display Attributes (with calc)
     * 表示向け Attribute の計算
     */
    public List<AttributedElement> getDisplayAttribute() {
        if (displayAttribute != null) {
            return displayAttribute;
        }

        // 初期化
        String tmp = text;
        List<AttributedElement> elements = getAttribute().stream() //
                .map(AttributedElement::copy) //
                .collect(Collectors.toList());

        for (int i = 0; i < elements.size(); i++) {
            AttributedElement elem = elements.get(i);

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
                AttributedElement next = elements.get(j);
                next.getRange().setStart(next.getRange().getStart() - diff);
                next.getRange().setEnd(next.getRange().getEnd() - diff);
            }
        }

        // 最後の空白は除去
        displayText = StringUtil.trimLast(tmp);
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
     * エレメントを走査
     */
    private void scanElements(AttributeType attributeType, String regex) {
        if (kinds.contains(attributeType)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);

            while (m.find()) {

                // 範囲が被っていない事を確認
                if (this.isUnusedRange(m)) {
                    AttributedElement element = new AttributedElement();
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

    /**
     * Call when user changed attributed element
     * AttributedElement を書き換えた場合に使用
     */
    public void markedElementChanged() {
        this.displayAttribute = null;
        this.attribute = null;
        this.displayText = null;
    }

    public void setText(String text) {
        markedElementChanged();
        this.text = text;
    }

    public List<AttributeType> getKinds() {
        return kinds;
    }

    public void setKinds(List<AttributeType> kinds) {
        this.markedElementChanged();
        this.kinds = kinds;
    }
    //endregion
}
