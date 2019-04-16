package net.socialhub.model.service.paging;

import net.socialhub.model.service.Paging;

public class BorderPaging extends Paging {

    /** Max Id (include) */
    private Long maxId;

    /** Since Id (not include) */
    private Long sinceId;

    //region // Getter&Setter
    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }
    //endregion
}
