package net.socialhub.model.common;


import java.util.regex.Matcher;

/**
 * Attributes Range
 * 文字列レンジ情報
 */
public class AttributedRange {

    public AttributedRange(Matcher m) {
        this.start = m.start();
        this.end = m.end();
    }

    public AttributedRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public AttributedRange copy() {
        return new AttributedRange(start, end);
    }

    /** Included Index */
    private int start;

    /** Excluded Index */
    private int end;

    public int getLength() {
        return (end - start);
    }

    //region // Getter&Setter
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
    //endregion
}