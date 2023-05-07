package net.socialhub.core.model.common.xml;

import net.socialhub.core.model.common.AttributedElement;
import net.socialhub.core.model.common.AttributedItem;
import net.socialhub.core.model.common.AttributedKind;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * XML Document
 */
public class XmlDocument {

    private XmlTag root;

    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * Make XMLDocument to AttributedString
     * XML ドキュメントから属性文字列に変換
     */
    public AttributedString toAttributedString(XmlConvertRule rule) {
        List<AttributedElement> elements = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        root.setAttribute(elements, text, rule);

        // 最後に文字列要素を追加
        if (text.length() > 0) {
            AttributedItem elem = new AttributedItem();
            elem.setDisplayText(StringUtil.trimLast(text.toString()));
            elem.setKind(AttributedKind.PLAIN);
            elements.add(elem);
        }

        return AttributedString.elements(elements);
    }

    /**
     * Find Specific name tags.
     * 特定のタグの要素のみを抽出する
     */
    public List<XmlTag> findXmlTag(String tagName) {
        return root.findXmlTag(tagName);
    }

    //region // Getter&Setter
    public XmlTag getRoot() {
        return root;
    }

    public void setRoot(XmlTag root) {
        this.root = root;
    }
    //endregion
}
