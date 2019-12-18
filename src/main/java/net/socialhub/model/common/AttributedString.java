package net.socialhub.model.common;

import net.socialhub.define.AttributedTypes;
import net.socialhub.model.common.xml.XmlConvertRule;
import net.socialhub.utils.XmlParseUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * String With Attributes
 * 属性付き文字列
 */
public class AttributedString {

    private List<AttributedElement> elements;

    // ============================================================================== //
    // Static functions
    // ============================================================================== //

    /**
     * Make Attributed String from plain text.
     * 装飾無しテキストから属性付き文字列を作成
     */
    public static AttributedString plain(String string) {
        return new AttributedString(string, AttributedTypes.simple());
    }

    /**
     * Make Attributed String from plain text with kinds.
     * 装飾無しテキストから属性付き文字列を作成 (種類を指定)
     */
    public static AttributedString plain(String string, List<AttributedType> kinds) {
        return new AttributedString(string, kinds);
    }

    /**
     * Make Attributed String from XHTML text.
     * XHTML テキストから属性付き文字列を作成
     */
    public static AttributedString xhtml(String string) {
        return AttributedString.xhtml(string, new XmlConvertRule());
    }

    /**
     * Make Attributed String from XHTML text with rule.
     * XHTML テキストから属性付き文字列を作成 (ルールを指定)
     */
    public static AttributedString xhtml(String string, XmlConvertRule rule) {
        return XmlParseUtil.xhtml(string).toAttributedString(rule);
    }

    /**
     * Make Attributes String with AttributedElements list.
     * 属性付き要素から属性付き文字列を生成
     */
    public static AttributedString elements(List<AttributedElement> elements) {
        return new AttributedString(elements);
    }

    // ============================================================================== //
    // Constructor
    // ============================================================================== //

    /**
     * Attributed String with plain text and element types.
     * 文字列から属性文字列を作成 (属性を指定)
     */
    private AttributedString(String text, List<AttributedType> kinds) {
        AttributedItem model = new AttributedItem();
        model.setKind(AttributedKind.PLAIN);
        model.setDisplayText(text);

        Stream<AttributedElement> stream = Stream.of(model);
        for (AttributedType kind : kinds) {
            stream = stream
                    .map(elem -> scanElements(elem, kind))
                    .flatMap(Collection::stream);
        }
        elements = stream.collect(toList());
    }

    /**
     * Make Attributes String with AttributedElements list.
     * 属性付き要素から属性付き文字列を生成
     */
    private AttributedString(List<AttributedElement> elements) {
        this.elements = elements;
    }

    /**
     * Get Elements
     * 要素情報の取得
     */
    public List<AttributedElement> getElements() {
        return this.elements;
    }

    /**
     * Get Display Text
     * 表示文字列を取得
     */
    public String getDisplayText() {
        return this.elements.stream()
                .map(AttributedElement::getDisplayText)
                .collect(Collectors.joining());
    }

    /**
     * Scan elements
     * エレメントを走査
     */
    private List<AttributedElement> scanElements(
            AttributedElement element,
            AttributedType kind) {

        if (element.getKind() == AttributedKind.PLAIN) {

            // プレーン文字列の場合にスキャンして走査
            Pattern p = Pattern.compile(kind.getRegex());
            String text = element.getDisplayText();
            Matcher m = p.matcher(text);

            // 見つかった場合分割
            if (m.find()) {
                String found = m.group();
                int i = text.indexOf(found);

                String before = text.substring(0, i);
                String after = text.substring(i + found.length());
                List<AttributedElement> results = new ArrayList<>();

                {
                    AttributedItem model = new AttributedItem();
                    model.setKind(AttributedKind.PLAIN);
                    model.setDisplayText(before);
                    results.add(model);
                }
                {
                    AttributedItem model = new AttributedItem();
                    model.setDisplayText(kind.getDisplayedText(m));
                    model.setExpandedText(kind.getExpandedText(m));
                    model.setKind(kind.getKind());
                    results.add(model);
                }
                {
                    AttributedItem model = new AttributedItem();
                    model.setKind(AttributedKind.PLAIN);
                    model.setDisplayText(after);

                    // 再帰的に作成したオブジェクトに対して走査
                    results.addAll(scanElements(model, kind));
                }
                return results;
            }
        }
        return Collections.singletonList(element);
    }
}
