package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Misskey Paging
 * Misskey の特殊ページングに対応
 */
public class MisskeyPaging extends Paging {

    private String untilId;
    private String sinceId;

    /**
     * From Paging instance
     */
    public static MisskeyPaging fromPaging(Paging paging) {
        if (paging instanceof MisskeyPaging) {
            return ((MisskeyPaging) paging).copy();
        }

        // Count の取得
        MisskeyPaging pg = new MisskeyPaging();
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
        MisskeyPaging pg = copy();

        if (!entities.isEmpty()) {
            T first = entities.get(0);
            pg.setUntilId(null);

            // Comment の場合はページング用 ID を使用
            if (first instanceof MisskeyComment) {
                MisskeyComment mc = (MisskeyComment) first;
                pg.setSinceId(mc.getIdForPaging());

            } else {
                // 他のオブジェクトはそのままのを使用
                pg.setSinceId((String) first.getId());
            }
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        MisskeyPaging pg = copy();

        if (!entities.isEmpty()) {
            T last = entities.get(entities.size() - 1);
            pg.setSinceId(null);

            // Comment の場合はページング用 ID を使用
            if (last instanceof MisskeyComment) {
                MisskeyComment mc = (MisskeyComment) last;
                pg.setUntilId(mc.getIdForPaging());

            } else {
                // 他のオブジェクトはそのままのを使用
                pg.setUntilId((String) last.getId());
            }
        }
        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public MisskeyPaging copy() {
        MisskeyPaging pg = new MisskeyPaging();
        pg.setSinceId(getSinceId());
        pg.setUntilId(getUntilId());
        copyTo(pg);
        return pg;
    }

    // region
    public String getUntilId() {
        return untilId;
    }

    public void setUntilId(String untilId) {
        this.untilId = untilId;
    }

    public String getSinceId() {
        return sinceId;
    }

    public void setSinceId(String sinceId) {
        this.sinceId = sinceId;
    }
    // endregion
}
