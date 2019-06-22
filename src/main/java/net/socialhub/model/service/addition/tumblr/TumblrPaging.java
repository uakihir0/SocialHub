package net.socialhub.model.service.addition.tumblr;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Tumblr Paging
 * Tumblr の特殊ページング対応
 */
public class TumblrPaging extends Paging {

    // Since ID
    private Long sinceId;

    // Offset
    private Long offset;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        TumblrPaging pg = copy();

        if (entities.size() > 0) {
            T first = entities.get(0);
            pg.setSinceId((Long) first.getId());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        TumblrPaging pg = copy();

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
    public TumblrPaging copy() {
        TumblrPaging pg = new TumblrPaging();
        pg.setOffset(getOffset());
        pg.setCount(getCount());
        pg.setSinceId(getSinceId());
        pg.setHasMore(getHasMore());
        return pg;
    }

    //region // Getter&Setter
    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
    //endregion
}
