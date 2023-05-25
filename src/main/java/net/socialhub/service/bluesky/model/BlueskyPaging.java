package net.socialhub.service.bluesky.model;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Paging;

import java.util.List;

public class BlueskyPaging extends Paging {

    /**
     * 最新のレコードの記録
     */
    private Identify latestRecord;
    private Identify latestRecordHint;

    /**
     * ページングカーソル
     */
    private String cursor;
    private String cursorHint;

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

            // ヒントが設定されている場合はそれを使用
            if (getLatestRecordHint() != null) {
                pg.setLatestRecord(getLatestRecordHint());
                pg.setCursor(null);
                return pg.clearHint();
            }

            T first = entities.get(0);
            pg.setLatestRecord(first);
            pg.setCursor(null);
            return pg.clearHint();
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

            // ヒントが設定されている場合はそれを使用
            if (getCursorHint() != null) {
                pg.setCursor(getCursorHint());
                pg.setLatestRecord(null);
                return pg.clearHint();
            }

            int count = entities.size();
            T last = entities.get((count - 1));

            if (last instanceof BlueskyComment) {
                BlueskyComment c = (BlueskyComment) last;
                String cursor = c.getCreateAt().getTime() + "::" + c.getCid();
                pg.setCursor(cursor);
                pg.setLatestRecord(null);
                return pg.clearHint();
            }
        }

        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public BlueskyPaging copy() {
        BlueskyPaging pg = new BlueskyPaging();

        pg.setLatestRecord(getLatestRecord());
        pg.setLatestRecordHint(getLatestRecordHint());

        pg.setCursor(getCursor());
        pg.setCursorHint(getCursorHint());

        copyTo(pg);
        return pg;
    }

    public BlueskyPaging clearHint() {
        setLatestRecordHint(null);
        setCursorHint(null);
        return this;
    }

    // region
    public Identify getLatestRecord() {
        return latestRecord;
    }

    public void setLatestRecord(Identify latestRecord) {
        this.latestRecord = latestRecord;
    }

    public Identify getLatestRecordHint() {
        return latestRecordHint;
    }

    public void setLatestRecordHint(Identify latestRecordHint) {
        this.latestRecordHint = latestRecordHint;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getCursorHint() {
        return cursorHint;
    }

    public void setCursorHint(String cursorHint) {
        this.cursorHint = cursorHint;
    }
    // endregion
}
