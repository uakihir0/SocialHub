package net.socialhub.service.bluesky.model;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Paging;

import java.util.Date;
import java.util.List;

public class BlueskyPaging extends Paging {

    /**
     * 取得した最初のレコードを記録
     * (取得済みであることを記録するため)
     */
    private Identify firstRecord;

    /**
     * 次を取得するためのカーソルを記録
     */
    private String nextCursor;

    /**
     * From Paging instance
     */
    public static BlueskyPaging fromPaging(Paging paging) {
        if (paging instanceof BlueskyPaging) {
            return ((BlueskyPaging) paging).copy();
        }

        // Count の取得
        BlueskyPaging pg = new BlueskyPaging();
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
        BlueskyPaging pg = copy();

        if (entities.size() > 0) {
            T first = entities.get(0);
            pg.setFirstRecord(first);
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        BlueskyPaging pg = copy();

        if (entities.size() > 0) {
            int count = entities.size();
            T last = entities.get((count - 1));

            if (last instanceof BlueskyComment) {
                BlueskyComment c = (BlueskyComment) last;
                String cursor = c.getCreateAt().getTime() + "::" + c.getCid();
                pg.setNextCursor(cursor);
            }
        }
        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public BlueskyPaging copy() {
        BlueskyPaging pg = new BlueskyPaging();
        pg.setFirstRecord(getFirstRecord());
        pg.setNextCursor(getNextCursor());
        copyTo(pg);
        return pg;
    }

    // region
    public Identify getFirstRecord() {
        return firstRecord;
    }

    public void setFirstRecord(Identify firstRecord) {
        this.firstRecord = firstRecord;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
    // endregion
}
