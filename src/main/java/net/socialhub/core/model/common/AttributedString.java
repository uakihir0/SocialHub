package net.socialhub.core.model.common;

import net.socialhub.core.model.Emoji;
import net.socialhub.core.model.common.xml.XmlConvertRule;
import net.socialhub.core.utils.XmlParseUtil;
import net.socialhub.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static net.socialhub.core.define.AttributedType.simple;

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
        return new AttributedString((string != null) ? string : "", simple());
    }

    /**
     * Make Attributed String from plain text with kinds.
     * 装飾無しテキストから属性付き文字列を作成 (種類を指定)
     */
    public static AttributedString plain(String string, List<AttributedType> kinds) {
        return new AttributedString((string != null) ? string : "", kinds);
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
     * Make Attributes String with AttributedElements list.
     * 属性付き要素から属性付き文字列を生成
     */
    private AttributedString(List<AttributedElement> elements) {
        this.elements = elements;
    }

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
     * Add Emoji Element
     * 絵文字要素を追加
     */
    public void addEmojiElement(List<Emoji> emojis) {
        if (emojis != null && !emojis.isEmpty()) {
            Stream<AttributedElement> stream = elements.stream();
            for (Emoji emoji : emojis) {
                stream = stream
                        .map(elem -> scanEmojis(elem, emoji))
                        .flatMap(Collection::stream);
            }
            elements = stream.collect(toList());
        }
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
            String text = element.getDisplayText();
            if (!text.isEmpty()) {

                // プレーン文字列の場合にスキャンして走査
                Pattern p = Pattern.compile(kind.getRegex());
                Matcher m = p.matcher(text);

                // 見つかった場合分割
                if (m.find()) {
                    int i = m.start();
                    String found = m.group();

                    if (i >= 0) {
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

                    } else {

                        // 特殊環境下でエラーになるようなので調査のためログを挟む
                        Logger log = Logger.getLogger(AttributedString.class);
                        log.debug("UnExpected Status");
                        log.debug("Text : " + text);
                        log.debug("Found: " + found);
                        log.debug("Index: " + i);
                    }
                }
            }
        }
        return Collections.singletonList(element);
    }

    /**
     * Scan emojis
     * 絵文字を抽出
     */
    private List<AttributedElement> scanEmojis(
            AttributedElement element,
            Emoji emoji) {

        // 文字列の場合のみが対象
        if (element.getKind() == AttributedKind.PLAIN) {
            String text = element.getDisplayText();

            // プレーン文字列の場合にスキャンして走査
            String regex = ":" + emoji.getShortCode() + ":";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);

            // 見つかった場合分割
            if (m.find()) {
                int i = m.start();
                String found = m.group();

                if (i >= 0) {
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
                        model.setDisplayText(regex);
                        model.setExpandedText(emoji.getImageUrl());
                        model.setKind(AttributedKind.EMOJI);
                        results.add(model);
                    }
                    {
                        AttributedItem model = new AttributedItem();
                        model.setKind(AttributedKind.PLAIN);
                        model.setDisplayText(after);

                        // 再帰的に作成したオブジェクトに対して走査
                        results.addAll(scanEmojis(model, emoji));
                    }
                    return results;

                } else {

                    // 特殊環境下でエラーになるようなので調査のためログを挟む
                    Logger log = Logger.getLogger(AttributedString.class);
                    log.debug("UnExpected Status");
                    log.debug("Text : " + text);
                    log.debug("Found: " + found);
                    log.debug("Index: " + i);
                }
            }
        }

        return Collections.singletonList(element);
    }
}
