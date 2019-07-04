package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

public class OffsetPaging extends Paging {

    private Long offset;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        return null;
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
        pg.setCount(getCount());
        pg.setOffset(getOffset());
        pg.setHasMore(getHasMore());
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

