package net.socialhub.core.model;

import net.socialhub.core.model.paging.BorderPaging;
import net.socialhub.core.model.paging.CursorPaging;
import net.socialhub.core.model.paging.DatePaging;
import net.socialhub.core.model.paging.IndexPaging;
import net.socialhub.core.model.paging.OffsetPaging;

import java.io.Serializable;
import java.util.List;

/**
 * Paging
 * ページング情報
 * Specified Paging
 * @see BorderPaging
 * @see CursorPaging
 * @see DatePaging
 * @see IndexPaging
 * @see OffsetPaging
 */
public class Paging implements Serializable {

    private Long count;
    private boolean hasNew = true;
    private boolean hasPast = true;

    public Paging() {
    }

    public Paging(Long count) {
        this.count = count;
    }

    public Paging copy() {
        Paging pg = new Paging();
        pg.setCount(count);
        pg.setHasNew(hasNew);
        pg.setHasPast(hasPast);
        return pg;
    }

    protected void copyTo(Paging pg) {
        pg.setCount(count);
        pg.setHasNew(hasNew);
        pg.setHasPast(hasPast);
    }

    /**
     * Get page for get newer entities
     * 新しい情報を取得するページを取得
     *
     * @param entities DataList it's ordered by created date time for desc.
     *                 算出するデータリスト、先頭から最新の ID になっている想定
     */
    public <T extends Identify> Paging newPage(List<T> entities) {
        return null;
    }

    /**
     * Get page for get past entities
     * 遡って過去の情報を取得するページを取得
     *
     * @param entities DataList it's ordered by created date time for desc.
     *                 算出するデータリスト、先頭から最新の ID になっている想定
     */
    public <T extends Identify> Paging pastPage(List<T> entities) {
        return null;
    }

    /**
     * Alias
     * New <-> Prev
     */
    public <T extends Identify> Paging prevPage(List<T> entities) {
        return newPage(entities);
    }

    /**
     * Alias
     * Past <-> Next
     */
    public <T extends Identify> Paging nextPage(List<T> entities) {
        return pastPage(entities);
    }

    /**
     * Set mark as paging end
     * ページの終端をマークする
     */
    public void setMarkPagingEnd(List<?> entities) {
        if (isHasPast()
                && entities.isEmpty()
                && (getCount() > 0)) {
            setHasPast(false);
        }
    }

    // region // Getter&Setter
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    /**
     * Alias
     * New <-> Prev
     */
    public boolean isHasPrev() {
        return isHasNew();
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    /**
     * Alias
     * New <-> Prev
     */
    public void setHasPrev(boolean hasPrev) {
        setHasNew(hasPrev);
    }

    public boolean isHasPast() {
        return hasPast;
    }

    /**
     * Alias
     * Past <-> Next
     */
    public boolean isHasNext() {
        return isHasPast();
    }

    public void setHasPast(boolean hasPast) {
        this.hasPast = hasPast;
    }

    /**
     * Alias
     * Past <-> Next
     */
    public void setHasNext(boolean hasNext) {
        setHasPast(hasNext);
    }
    // endregion
}
