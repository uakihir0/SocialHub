package net.socialhub.core.model.paging;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Paging;

import java.util.List;

/**
 * ID の開始と終了を指定するページング
 * (start,end の片方のみを指定する)
 */
public class RangePaging<T> extends Paging {

    /** 開始 ID */
    private T start;
    private T startHint;

    /** 終了 ID */
    private T end;
    private T endHint;

    /**
     * From Paging instance
     */
    @SuppressWarnings("unchecked")
    public static <T> RangePaging<T> fromPaging(Paging paging) {
        if (paging instanceof RangePaging) {
            return ((RangePaging<T>) paging).copy();
        }

        // Count の取得
        RangePaging<T> pg = new RangePaging<>();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <J extends Identify> Paging newPage(List<J> entities) {
        RangePaging<T> pg = copy();

        if (entities.size() > 0) {
            if (getEndHint() != null) {
                pg.setEnd(getEndHint());
                pg.setStart(null);
                return pg.clearHint();
            }

            pg.setEnd((T) entities.get(0).getId());
            pg.setStart(null);
            return pg.clearHint();
        }

        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <J extends Identify> Paging pastPage(List<J> entities) {
        RangePaging<T> pg = copy();

        if (entities.size() > 0) {
            if (getStartHint() != null) {
                pg.setStart(getStartHint());
                pg.setEnd(null);
                return pg.clearHint();
            }

            int len = entities.size();
            pg.setStart((T) entities.get(len - 1).getId());
            pg.setEnd(null);
            return pg.clearHint();
        }

        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public RangePaging<T> copy() {
        RangePaging<T> pg = new RangePaging<>();
        pg.setStart(getStart());
        pg.setStartHint(getStartHint());
        pg.setEnd(getEnd());
        pg.setEndHint(getEndHint());
        copyTo(pg);
        return pg;
    }

    public RangePaging<T> clearHint() {
        setStartHint(null);
        setEndHint(null);
        return this;
    }

    // region

    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public T getStartHint() {
        return startHint;
    }

    public void setStartHint(T startHint) {
        this.startHint = startHint;
    }

    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    public T getEndHint() {
        return endHint;
    }

    public void setEndHint(T endHint) {
        this.endHint = endHint;
    }

    // endregion
}
