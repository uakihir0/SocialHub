package net.socialhub.core.model.paging;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Paging;

import java.util.List;

/**
 * Paging with page number
 * ベージ番号付きページング
 */
public class IndexPaging extends Paging {

    private Long page;

    /**
     * From Paging instance
     */
    public static IndexPaging fromPaging(Paging paging) {
        if (paging instanceof IndexPaging) {
            return ((IndexPaging) paging).copy();
        }

        // Count の取得
        IndexPaging pg = new IndexPaging();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {

        if (page > 1) {
            IndexPaging newPage = new IndexPaging();
            newPage.setCount(getCount());
            newPage.setPage(page - 1);
            return newPage;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {

        Long number = (((page == null) ? 0L : page) + 1L);
        IndexPaging pastPage = new IndexPaging();
        pastPage.setCount(getCount());
        pastPage.setPage(number);
        return pastPage;
    }

    /**
     * オブジェクトコピー
     */
    public IndexPaging copy() {
        IndexPaging pg = new IndexPaging();
        pg.setPage(getPage());
        copyTo(pg);
        return pg;
    }

    //region // Getter&Setter
    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }
    //endregion
}
