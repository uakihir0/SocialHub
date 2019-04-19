package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.Date;
import java.util.List;

/**
 * Paging with date range
 * 時間範囲指定
 * (Slack)
 */
public class DatePaging extends Paging {

    private Date latest;

    private Date oldest;

    private Boolean inclusive;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {

        if (latest != null && inclusive != null) {
            DatePaging newPage = new DatePaging();
            newPage.setInclusive(!inclusive);
            newPage.setOldest(latest);
            newPage.setCount(getCount());
            return newPage;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {

        if (oldest != null && inclusive != null) {
            DatePaging newPage = new DatePaging();
            newPage.setInclusive(!inclusive);
            newPage.setLatest(oldest);
            newPage.setCount(getCount());
            return newPage;
        }
        return null;
    }

    //region // Getter&Setter
    public Date getLatest() {
        return latest;
    }

    public void setLatest(Date latest) {
        this.latest = latest;
    }

    public Date getOldest() {
        return oldest;
    }

    public void setOldest(Date oldest) {
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
