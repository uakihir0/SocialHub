package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Paging with cursor
 * カーソル付きページング
 * (Twitter, Slack)
 */
public class CursorPaging<Type> extends Paging {

    /** prev cursor */
    private Type prevCursor;

    /** current cursor */
    private Type currentCursor;

    /** next cursor */
    private Type nextCursor;

    /**
     * From Paging instance
     */
    @SuppressWarnings("unchecked")
    public static <T> CursorPaging<T> fromPaging(Paging paging) {
        if (paging instanceof CursorPaging) {
            return ((CursorPaging<T>) paging).copy();
        }

        // Count の取得
        CursorPaging<T> pg = new CursorPaging<>();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        CursorPaging<Type> newPage = new CursorPaging<>();

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
        CursorPaging<Type> pastPage = new CursorPaging<>();

        if (nextCursor != null) {
            pastPage.setCurrentCursor(nextCursor);
            pastPage.setCount(getCount());
            return pastPage;
        }

        return pastPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMarkPagingEnd(List<?> entities) {
        if (isHasNew() && (getPrevCursor() == null)) {
            setHasNew(false);
        }
        if (isHasPast() && (getNextCursor() == null)) {
            setHasPast(false);
        }
    }

    /**
     * オプジェクトコピー
     */
    public CursorPaging<Type> copy() {
        CursorPaging<Type> pg = new CursorPaging<>();
        pg.setCurrentCursor(getCurrentCursor());
        pg.setNextCursor(getNextCursor());
        pg.setPrevCursor(getPrevCursor());
        copyTo(pg);
        return pg;
    }

    //region // Getter&Setter
    public Type getCurrentCursor() {
        return currentCursor;
    }

    public void setCurrentCursor(Type currentCursor) {
        this.currentCursor = currentCursor;
    }

    public Type getPrevCursor() {
        return prevCursor;
    }

    public void setPrevCursor(Type prevCursor) {
        this.prevCursor = prevCursor;
    }

    public Type getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Type nextCursor) {
        this.nextCursor = nextCursor;
    }
    //endregion
}
