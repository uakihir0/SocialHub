package net.socialhub.model.service;

import java.io.Serializable;

public class Paging implements Serializable {

    private Long page;
    private Long count;
    private Long maxId;
    private Long sinceId;

    //region // Getter&Setter
    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

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
