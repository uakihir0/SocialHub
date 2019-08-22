package net.socialhub.model.common;

import net.socialhub.utils.StringUtil;

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

        String string = StringUtil.trimLast(text.toString());
        return new AttributedString(string, elements, true);
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
