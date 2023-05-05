package net.socialhub.core.model.service.paging;

import net.socialhub.core.model.service.Identify;
import net.socialhub.core.model.service.Paging;

import java.util.List;

/**
 * Paging with date range
 * 時間範囲指定
 * (Slack)
 */
public class DatePaging extends Paging {

    private String latest;

    private String oldest;

    private Boolean inclusive;

    /**
     * From Paging instance
     */
    public static DatePaging fromPaging(Paging paging) {
        if (paging instanceof DatePaging) {
            return ((DatePaging) paging).copy();
        }

        // Count の取得
        DatePaging pg = new DatePaging();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends Identify> Paging newPage(List<K> entities) {
        DatePaging newPage = new DatePaging();
        newPage.setCount(getCount());

        if (entities != null && !entities.isEmpty()) {
            String first = (String) entities.get(0).getId();

            newPage.setInclusive(false);
            newPage.setOldest(first);
            return newPage;

        } else {

            // デフォルト動作
            if (latest != null && inclusive != null) {
                newPage.setInclusive(!inclusive);
                newPage.setOldest(latest);
                return newPage;
            }

            // 上記以外は再度リクエスト
            return this.copy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends Identify> Paging pastPage(List<K> entities) {
        DatePaging newPage = new DatePaging();
        newPage.setCount(getCount());

        if (entities != null && !entities.isEmpty()) {
            int index = (entities.size() - 1);
            String last = (String) entities.get(index).getId();

            newPage.setInclusive(false);
            newPage.setLatest(last);
            return newPage;

        } else {

            // デフォルト動作
            if (oldest != null && inclusive != null) {
                newPage.setInclusive(!inclusive);
                newPage.setLatest(oldest);
                return newPage;
            }

            // 上記以外は再度リクエスト
            return this.copy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMarkPagingEnd(List<?> entities) {
        if (isHasPast()
                && entities.isEmpty()
                && (getOldest() == null)
                && (getCount() > 0)) {
            setHasPast(false);
        }
    }

    /**
     * オプジェクトコピー
     */
    public DatePaging copy() {
        DatePaging pg = new DatePaging();
        pg.setLatest(getLatest());
        pg.setOldest(getOldest());
        pg.setInclusive(getInclusive());
        copyTo(pg);
        return pg;
    }

    //region // Getter&Setter
    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getOldest() {
        return oldest;
    }

    public void setOldest(String oldest) {
        this.oldest = oldest;
    }

    public Boolean getInclusive() {
        return inclusive;
    }

    public void setInclusive(Boolean inclusive) {
        this.inclusive = inclusive;
    }
    //endregion
}
