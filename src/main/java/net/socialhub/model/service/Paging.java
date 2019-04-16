package net.socialhub.model.service;

import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.IndexPaging;

import java.io.Serializable;

/**
 * Paging
 * ページング情報
 *
 * Specified Paging
 * {@link BorderPaging}
 * {@link CursorPaging}
 * {@link IndexPaging}
 */
public class Paging implements Serializable {

    private Long count;



    //region // Getter&Setter
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    //endregion
}
