package net.socialhub.model.service.paging;

import net.socialhub.model.service.Paging;

/**
 * Paging with page number
 * ベージ番号付きページング
 */
public class IndexPaging extends Paging {

    private Long page;

    //region // Getter&Setter
    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }
    //endregion
}
