package net.socialhub.model.common.xml;

import net.socialhub.model.common.AttributedElement;

import java.util.List;

/**
 * XML Element
 * {@link XmlElement}
 * {@link XmlString}
 */
public interface XmlElement {

    void setAttribute(
            List<AttributedElement> elements,
            StringBuilder builder,
            XmlConvertRule rule);
}
