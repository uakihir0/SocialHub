package net.socialhub.model.service;

import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.IndexPaging;

import java.io.Serializable;
import java.util.List;

/**
 * Paging
 * ページング情報
 * Specified Paging
 * {@link BorderPaging}
 * {@link CursorPaging}
 * {@link IndexPaging}
 */
public class Paging implements Serializable {

    private Long count;

    private Boolean hasMore;

    public Paging() {
    }

    public Paging(Long count) {
        this.count = count;
    }

    /**
     * Get page for get newer entities
     * 新しい情報を取得するページを取得
     */
    public <T extends Identify> Paging newPage(List<T> entities) {
        return null;
    }

    /**
     * Get page for get past entities
     * 遡って過去の情報を取得するページを取得
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

    //region // Getter&Setter
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
    //endregion
}
