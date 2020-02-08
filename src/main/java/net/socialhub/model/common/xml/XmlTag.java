package net.socialhub.model.common.xml;

import net.socialhub.model.common.AttributedBucket;
import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedItem;
import net.socialhub.model.common.AttributedKind;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.utils.StringUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlTag implements XmlElement {

    private String name;

    private List<XmlElement> elements = new ArrayList<>();

    private Map<String, String> attributes = new HashMap<>();

    @Override
    public void setAttribute(
            List<AttributedElement> elems,
            StringBuilder builder,
            XmlConvertRule rule) {

        // 見えない要素の場合は無視
        if (attributes.containsKey("class")) {
            if (attributes.get("class").equals("invisible")) {
                return;
            }
        }

        // ------------------------------------------------------------------- //
        // <BR>: 改行の場合は改行
        if (name.equalsIgnoreCase("br")) {
            builder.append(rule.getBr());
            return;
        }

        // ------------------------------------------------------------------- //
        // <P>: タグの後は段落
        if (name.equalsIgnoreCase("p")) {
            expandAttribute(elems, builder, rule);
            builder.append(rule.getP());
            return;
        }


        // ------------------------------------------------------------------- //
        // <A>: リンクの処理
        if (name.equalsIgnoreCase("a")) {
            expandStringElement(elems, builder);
            expandAttribute(elems, builder, rule);

            String displayText = builder.toString();
            builder.setLength(0);

            if (attributes.containsKey("class")) {

                // ハッシュタグの場合
                if (attributes.get("class").contains("hashtag")) {
                    AttributedItem elem = new AttributedItem();
                    elem.setKind(AttributedKind.HASH_TAG);
                    elem.setDisplayText(displayText);
                    elems.add(elem);
                    return;
                }

                // ユーザー向け URL の場合 (Mastodon)
                if (attributes.get("class").contains("u-url")) {
                    AttributedItem elem = new AttributedItem();
                    String href = attributes.get("href");
                    elem.setKind(AttributedKind.ACCOUNT);
                    elem.setDisplayText(displayText);
                    elem.setExpandedText(href);
                    elems.add(elem);
                    return;
                }
            }

            AttributedItem elem = new AttributedItem();
            elem.setKind(AttributedKind.LINK);
            elem.setExpandedText(attributes.get("href"));
            elem.setDisplayText(displayText);
            elems.add(elem);
            return;
        }

        // ------------------------------------------------------------------- //
        // <BLOCKQUOTE>: 引用の処理 (Tumblr)
        if (name.equalsIgnoreCase("blockquote")) {
            expandStringElement(elems, builder);

            AttributedBucket elem = new AttributedBucket();
            elem.setChildren(new ArrayList<>());
            elem.setKind(AttributedKind.QUOTE);
            elems.add(elem);

            // 再帰的に中身を走査
            StringBuilder text = new StringBuilder();
            expandAttribute(elem.getChildren(), text, rule);

            // 最後に文字列要素を追加
            if (text.length() > 0) {
                AttributedItem item = new AttributedItem();
                item.setDisplayText(StringUtil.trimLast(text.toString()));
                item.setKind(AttributedKind.PLAIN);
                elem.getChildren().add(item);
            }
            return;
        }

        // TODO: テキスト装飾系


        // その他の場合は無視して続行
        expandAttribute(elems, builder, rule);
    }

    /** 文字を切り出す処理 */
    private void expandStringElement(
            List<AttributedElement> elems,
            StringBuilder builder) {

        // 空文字の場合は処理を行わない
        if (builder.length() > 0) {

            AttributedItem elem = new AttributedItem();
            elem.setDisplayText(builder.toString());
            elem.setKind(AttributedKind.PLAIN);
            elems.add(elem);

            // Clear Buffer
            builder.setLength(0);
        }
    }

    private void expandAttribute(
            List<AttributedElement> elems,
            StringBuilder builder,
            XmlConvertRule rule) {

        for (XmlElement element : elements) {
            element.setAttribute(elems, builder, rule);
        }
    }

    private String getHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            throw new SocialHubException(e);
        }
    }

    /**
     * Find Specific name tags.
     * 特定のタグの要素のみを抽出する
     */
    public List<XmlTag> findXmlTag(String tagName) {
        ArrayList<XmlTag> tags = new ArrayList<>();

        // 対象タグが画像の場合はそのタグを加える
        if (name.equalsIgnoreCase(tagName)) {
            tags.add(this);
        }

        // XmlTag の場合は分岐して処理
        for (XmlElement elem : elements) {
            if (elem instanceof XmlTag) {
                tags.addAll(((XmlTag) elem).findXmlTag(tagName));
            }
        }

        return tags;
    }

    @Override
    public String toString() {

        // 見えない要素の場合は空文字
        if (attributes.containsKey("class")) {
            if (attributes.get("class").equals("invisible")) {
                return "";
            }
        }

        // 改行の場合は素直に改行
        if (name.equalsIgnoreCase("br")) {
            return "\n";
        }

        StringBuilder builder = new StringBuilder();
        for (XmlElement element : elements) {
            builder.append(element.toString());
        }

        return builder.toString();
    }

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<XmlElement> getElements() {
        return elements;
    }

    public void setElements(List<XmlElement> elements) {
        this.elements = elements;
    }
    //endregion
}
