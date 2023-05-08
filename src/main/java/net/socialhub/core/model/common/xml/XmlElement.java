package net.socialhub.core.model.common.xml;

import net.socialhub.core.model.common.AttributedElement;

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
