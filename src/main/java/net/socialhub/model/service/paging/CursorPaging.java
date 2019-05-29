package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Paging with cursor
 * カーソル付きページング
 * (Slack)
 */
public class CursorPaging extends Paging {

    /** prev cursor */
    private String prevCursor;

    /** current cursor */
    private String currentCursor;

    /** next cursor */
    private String nextCursor;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        CursorPaging newPage = new CursorPaging();

        if (prevCursor != null) {
            newPage.setCurrentCursor(prevCursor);
            newPage.setCount(getCount());
            return newPage;
        }

        return newPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        CursorPaging pastPage = new CursorPaging();

        if (nextCursor != null) {
            pastPage.setCurrentCursor(nextCursor);
            pastPage.setCount(getCount());
            return pastPage;
        }

        return pastPage;
    }

    //region // Getter&Setter
    public String getCurrentCursor() {
        return currentCursor;
    }

    public void setCurrentCursor(String currentCursor) {
        this.currentCursor = currentCursor;
    }

    public String getPrevCursor() {
        return prevCursor;
    }

    public void setPrevCursor(String prevCursor) {
        this.prevCursor = prevCursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
    //endregion
}
