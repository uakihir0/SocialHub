package net.socialhub.model.common;

/**
 * Attributes Elements
 * 属性情報
 */
public class AttributedElement {

    private AttributedType type;

    /** オリジナルテキスト */
    private String text;

    /** 表示するテキスト */
    private String displayText;

    /** 実際に処理するテキスト */
    private String expandedText;

    private AttributedRange range;

    public AttributedElement copy() {
        AttributedElement model = new AttributedElement();

        model.setType(getType());
        model.setText(getText());
        model.setDisplayText(getDisplayText());
        model.setExpandedText(getExpandedText());
        model.setRange(getRange().copy());
        return model;
    }

    //region // Getter&Setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDisplayText() {
        if (displayText != null) {
            return displayText;
        }
        return text;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getExpandedText() {
        if (expandedText != null) {
            return expandedText;
        }
        return text;
    }

    public void setExpandedText(String expandedText) {
        this.expandedText = expandedText;
    }

    public AttributedType getType() {
        return type;
    }

    public void setType(AttributedType type) {
        this.type = type;
    }

    public AttributedRange getRange() {
        return range;
    }

    public void setRange(AttributedRange range) {
        this.range = range;
    }
    //endregion
}