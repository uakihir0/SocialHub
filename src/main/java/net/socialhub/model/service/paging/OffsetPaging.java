package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

public class OffsetPaging extends Paging {

    private Long offset;

    /**
     * From Paging instance
     */
    public static OffsetPaging fromPaging(Paging paging) {
        if (paging instanceof OffsetPaging) {
            return ((OffsetPaging) paging).copy();
        }

        // Count の取得
        OffsetPaging pg = new OffsetPaging();
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
        OffsetPaging pg = copy();
        pg.setOffset(0L);
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        OffsetPaging pg = copy();

        if (entities.size() > 0) {
            long count = (long) entities.size();
            if (pg.getOffset() == null) {
                pg.setOffset(0L);
            }

            // オフセット分を取得した量分変更
            pg.setOffset(pg.getOffset() + count);
        }
        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public OffsetPaging copy() {
        OffsetPaging pg = new OffsetPaging();
        pg.setOffset(getOffset());
        copyTo(pg);
        return pg;
    }

    //region // Getter&Setter
    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
    //endregion
}

