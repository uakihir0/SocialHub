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

        if (prevCursor != null) {
            CursorPaging newPage = new CursorPaging();
            newPage.setCurrentCursor(prevCursor);
            newPage.setCount(getCount());
            return newPage;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {

        if (nextCursor != null) {
            CursorPaging pastPage = new CursorPaging();
            pastPage.setCurrentCursor(nextCursor);
            pastPage.setCount(getCount());
            return pastPage;
        }
        return null;
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
