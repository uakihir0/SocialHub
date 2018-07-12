package net.socialhub.model.service;

import java.util.List;

/**
 * ページング可能レスポンス
 * Pageable Response
 */
public class Pageable<T> {

    /** ページング情報 */
    private Paging paging;

    /** エンティティー */
    private List<T> entities;

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
