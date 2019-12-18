package net.socialhub.model.common;

public class AttributedItem implements AttributedElement {

    private AttributedKind kind;

    /** 表示するテキスト */
    private String displayText;

    /** 実際に処理するテキスト */
    private String expandedText;

    // region // Getter&Setter
    @Override
    public AttributedKind getKind() {
        return kind;
    }

    public void setKind(AttributedKind kind) {
        this.kind = kind;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getExpandedText() {
        if (expandedText == null) {
            return getDisplayText();
        }
        return expandedText;
    }

    public void setExpandedText(String expandedText) {
        this.expandedText = expandedText;
    }
    // endregion
}
