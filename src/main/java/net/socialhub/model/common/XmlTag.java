package net.socialhub.model.common;

import net.socialhub.define.AttributedTypes;
import net.socialhub.model.error.SocialHubException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlTag implements XmlElement {

    private String name;

    private Map<String, String> attributes = new HashMap<>();

    private List<XmlElement> elements = new ArrayList<>();

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

    @Override
    public void setAttribute(List<AttributedElement> elems, StringBuilder builder) {
        // 主に Mastodon 向けの処理が記載

        // 見えない要素の場合は特に処理として何もしない
        if (attributes.containsKey("class")) {
            if (attributes.get("class").equals("invisible")) {
                return;
            }
        }

        // 改行の場合は素直に改行
        if (name.equalsIgnoreCase("br")) {
            builder.append("\n");
            return;
        }

        // リンクの処理
        if (name.equalsIgnoreCase("a")) {
            AttributedRange range = new AttributedRange();
            range.setStart(builder.length());
            expandAttribute(elems, builder);
            range.setEnd(builder.length());


            if (attributes.containsKey("class")) {

                // ハッシュタグの場合
                if (attributes.get("class").contains("hashtag")) {
                    AttributedElement elem = new AttributedElement();
                    elem.setText(builder.substring(range.getStart(), range.getEnd()));
                    elem.setExpandedText(elem.getText());
                    elem.setType(AttributedTypes.hashTag);
                    elem.setRange(range);
                    elems.add(elem);
                    return;
                }

                // ユーザー向け URL の場合
                if (attributes.get("class").contains("u-url")) {
                    AttributedElement elem = new AttributedElement();
                    elem.setText(builder.substring(range.getStart(), range.getEnd()));
                    elem.setExpandedText(elem.getText() + "@" + getHost(attributes.get("href")));
                    elem.setType(AttributedTypes.mastodonAccount);
                    elem.setRange(range);
                    elems.add(elem);
                    return;
                }
            }

            AttributedElement elem = new AttributedElement();
            elem.setText(builder.substring(range.getStart(), range.getEnd()));
            elem.setExpandedText(attributes.get("href"));
            elem.setType(AttributedTypes.link);
            elem.setRange(range);
            elems.add(elem);
            return;
        }

        // p タグの後は段落
        if (name.equalsIgnoreCase("p")) {
            expandAttribute(elems, builder);
            builder.append("\n\n");
            return;
        }

        // その他の場合は無視して続行
        // (テキスト装飾系は扱わない)
        expandAttribute(elems, builder);
    }

    private void expandAttribute(List<AttributedElement> elems, StringBuilder builder) {
        for (XmlElement element : elements) {
            element.setAttribute(elems, builder);
        }
    }

    private String getHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            throw new SocialHubException(e);
        }
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
