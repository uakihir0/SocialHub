package net.socialhub.service.bluesky.model;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Paging;

public class BlueskyPaging extends Paging {

    /**
     * 取得した最後のレコードを記録
     * (取得済みであることを記録するため)
     */
    private Identify lastRecord;

    /**
     * 次を取得するためのカーソルを記録
     */
    private String nextCursor;


    // region
    public Identify getLastRecord() {
        return lastRecord;
    }

    public void setLastRecord(Identify lastRecord) {
        this.lastRecord = lastRecord;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
    // endregion
}
