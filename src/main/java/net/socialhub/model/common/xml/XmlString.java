package net.socialhub.model.common.xml;

import net.socialhub.model.common.AttributedElement;

import java.util.List;

/**
 * XML String Element
 */
public class XmlString implements XmlElement {

    private String string;

    @Override
    public void setAttribute(
            List<AttributedElement> elements,
            StringBuilder builder,
            XmlConvertRule rule) {
        builder.append(string);
    }

    //region // Getter&Setter
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
    //endregion
}
