package net.socialhub.core.model.common;

/**
 * Attributes Elements
 * 属性情報
 */
public interface AttributedElement {

    /** Get type of element. */
    AttributedKind getKind();

    /** Get text that user see. */
    String getDisplayText();

    /** Get text that user action. */
    String getExpandedText();

    boolean getVisible();

    void setVisible(boolean visible);
}