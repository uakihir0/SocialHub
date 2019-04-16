package net.socialhub.model.service.paging;

import net.socialhub.model.service.Paging;

/**
 * Paging with cursor
 * カーソル付きページング
 */
public class CursorPaging extends Paging {

    /** ongoing cursor */
    private String cursor;

    /** next cursor */
    private String nextCursor;

    //region // Getter&Setter
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
    //endregion
}
