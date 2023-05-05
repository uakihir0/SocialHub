package net.socialhub.mastodon.model;

import net.socialhub.core.model.service.Identify;
import net.socialhub.core.model.service.Paging;

import java.util.List;

public class MastodonPaging extends Paging {

    private String minId;
    private String minIdInLink;
    private String maxId;
    private String maxIdInLink;

    /**
     * From Paging instance
     */
    public static MastodonPaging fromPaging(Paging paging) {
        if (paging instanceof MastodonPaging) {
            return ((MastodonPaging) paging).copy();
        }

        // Count の取得
        MastodonPaging pg = new MastodonPaging();
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
        MastodonPaging pg = copy();
        if (entities == null || entities.isEmpty()) {
            return pg;
        }

        pg.setMaxId(null);
        if (getMinIdInLink() != null) {
            pg.setMinId(getMinIdInLink());
        } else {
            Object entryMinId = entities.get(0).getId();
            pg.setMinId((String) entryMinId);
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        MastodonPaging pg = copy();
        if (entities == null || entities.isEmpty()) {
            return pg;
        }

        pg.setMinId(null);
        if (getMaxIdInLink() != null) {
            pg.setMaxId(getMaxIdInLink());
        } else {
            int lastIndex = (entities.size() - 1);
            Object entryMaxId = entities.get(lastIndex).getId();
            pg.setMaxId((String) entryMaxId);
        }

        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public MastodonPaging copy() {
        MastodonPaging pg = new MastodonPaging();
        pg.setMaxId(getMaxId());
        pg.setMinId(getMinId());
        copyTo(pg);
        return pg;
    }

    // region // Getter & Setter
    public String getMinId() {
        return minId;
    }

    public void setMinId(String minId) {
        this.minId = minId;
    }

    public String getMinIdInLink() {
        return minIdInLink;
    }

    public void setMinIdInLink(String minIdInLink) {
        this.minIdInLink = minIdInLink;
    }

    public String getMaxId() {
        return maxId;
    }

    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    public String getMaxIdInLink() {
        return maxIdInLink;
    }

    public void setMaxIdInLink(String maxIdInLink) {
        this.maxIdInLink = maxIdInLink;
    }
    // endregion
}
