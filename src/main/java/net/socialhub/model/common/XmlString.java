package net.socialhub.model.common;

import java.util.List;

/**
 * XML String Element
 */
public class XmlString implements XmlElement {

    private String string;

    @Override
    public void setAttribute(List<AttributedElement> elements, StringBuilder builder) {
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
