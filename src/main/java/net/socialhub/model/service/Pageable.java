package net.socialhub.model.service;

import net.socialhub.model.service.paging.BorderPaging;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * ページング可能レスポンス
 * Pageable Response
 */
public class Pageable<T extends Identify> implements Serializable {

    /** Paging Information */
    private Paging paging;

    /** Entities */
    private List<T> entities;

    /**
     * Get New Page
     * 最新のページを取得
     */
    public Paging newPage() {
        return paging.newPage(entities);
    }

    /**
     * Get Past Page
     * 過去のページを取得
     */
    public Paging pastPage() {
        return paging.pastPage(entities);
    }

    /**
     * Get Prev Page
     * 前のページを取得
     */
    public Paging prevPage() {
        return paging.prevPage(entities);
    }

    /**
     * Get Next Page
     * 次のページを取得
     */
    public Paging nextPage() {
        return paging.nextPage(entities);
    }

    //region // Getter&Setter
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }
    //endregion
}
