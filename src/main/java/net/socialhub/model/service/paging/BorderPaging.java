package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;
import java.util.Optional;

/**
 * Paging with max and since
 * Max Since 管理のページング
 * (Twitter, Mastodon)
 */
public class BorderPaging extends Paging {

    /** Max Id (include) */
    private Long maxId;

    /** Since Id (not include) */
    private Long sinceId;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        Optional<Long> id = entities.get(0).getId(Long.class);

        // 数値の ID のみ対象
        if (id.isPresent()) {
            BorderPaging newPage = new BorderPaging();
            newPage.setCount(this.getCount());

            if (this.getMaxId() != null) {
                newPage.setSinceId(this.getMaxId());
            } else {
                newPage.setSinceId(id.get());
            }
            return newPage;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        T last = entities.get(entities.size() - 1);
        Optional<Long> id = last.getId(Long.class);

        // 数値の ID のみ対象
        if (id.isPresent()) {
            BorderPaging pastPage = new BorderPaging();
            pastPage.setCount(this.getCount());

            if (this.getSinceId() != null) {
                pastPage.setMaxId(this.getSinceId());
            } else {
                pastPage.setMaxId(id.get() - 1);
            }
            return pastPage;
        }

        return null;
    }

    /**
     * オブジェクトコピー
     */
    public BorderPaging copy() {
        BorderPaging pg = new BorderPaging();
        pg.setCount(getCount());
        pg.setMaxId(getMaxId());
        pg.setSinceId(getSinceId());
        pg.setHasMore(getHasMore());
        return pg;
    }

    //region // Getter&Setter
    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }
    //endregion
}
