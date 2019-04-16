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

        // リクエストが完了している場合は null を返却
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        // MaxSince でページングが管理されている場合
        if (paging instanceof BorderPaging) {
            BorderPaging border = (BorderPaging) paging;
            Optional<Long> id = entities.get(0).getId(Long.class);

            // 数値の ID のみ対象
            if (id.isPresent()) {
                BorderPaging newPage = new BorderPaging();
                newPage.setCount(border.getCount());

                if (border.getMaxId() != null) {
                    newPage.setSinceId(border.getMaxId());
                } else {
                    newPage.setSinceId(id.get());
                }
                return newPage;
            }
        }

        return null;
    }

    /**
     * Get Past Page
     * 過去のページを取得
     */
    public Paging pastPage() {

        // リクエストが完了している場合は null を返却
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        // MaxSince でページングが管理されている場合
        if (paging instanceof BorderPaging) {
            BorderPaging border = (BorderPaging) paging;
            T last = entities.get(entities.size() - 1);
            Optional<Long> id = last.getId(Long.class);

            // 数値の ID のみ対象
            if (id.isPresent()) {
                BorderPaging newPage = new BorderPaging();
                newPage.setCount(border.getCount());

                if (border.getSinceId() != null) {
                    newPage.setMaxId(border.getSinceId());
                } else {
                    newPage.setMaxId(id.get() - 1);
                }
                return newPage;
            }
        }

        return null;
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
