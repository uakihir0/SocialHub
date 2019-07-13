package net.socialhub.model.common;

import net.socialhub.define.AttributedTypes;
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

    private String text;

    private String displayText;

    private List<AttributedType> kinds;

    private List<AttributedElement> attribute;

    private List<AttributedElement> displayAttribute;

    /**
     * Attributed String
     * (属性文字列に変換)
     */
    public AttributedString(String text) {
        this(text, AttributedTypes.simple());
    }

    /**
     * Attributed String
     * (属性文字列に変換)
     */
    public AttributedString(String text, List<AttributedType> kinds) {
        this.displayAttribute = null;
        this.attribute = null;
        this.kinds = kinds;
        this.text = text;

        // 無指定の場合は全部
        if (kinds == null) {
            this.kinds = AttributedTypes.simple();
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

        for (AttributedType kind : kinds) {
            scanElements(kind);
        }

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
    private void scanElements(AttributedType kind) {
        if (kinds.contains(kind)) {
            Pattern p = Pattern.compile(kind.getRegex());
            Matcher m = p.matcher(text);

            while (m.find()) {

                // 範囲が被っていない事を確認
                if (this.isUnusedRange(m)) {
                    AttributedElement element = new AttributedElement();
                    element.setDisplayText(kind.getDisplayedText(m));
                    element.setRange(new AttributedRange(m));
                    element.setText(m.group());
                    element.setType(kind);
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

    public List<AttributedType> getKinds() {
        return kinds;
    }

    public void setKinds(List<AttributedType> kinds) {
        this.markedElementChanged();
        this.kinds = kinds;
    }
    //endregion
}
