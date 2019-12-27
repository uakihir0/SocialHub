package net.socialhub.model.service;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * ページング可能レスポンス
 * Pageable Response
 */
public class Pageable<T extends Identify> implements Serializable {

    /** Paging Information */
    private Paging paging;

    /** Entities */
    private List<T> entities;

    /** Displayable predicate */
    private Predicate<T> predicate;

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

    /**
     * Get Displayable Entities
     * 表示条件を満たしたアイテムを取得
     */
    public List<T> getDisplayableEntities() {
        if (predicate == null) {
            return getEntities();
        }
        return getEntities() //
                .stream() //
                .filter(predicate) //
                .collect(Collectors.toList());
    }

    //region // Getter&Setter
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
        if (this.entities != null) {
            this.paging.setMarkPagingEnd(this.entities);
        }
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
        if (this.paging != null) {
            this.paging.setMarkPagingEnd(this.entities);
        }
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }
    //endregion
}
