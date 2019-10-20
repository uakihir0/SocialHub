package net.socialhub.model.service.support;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Trend;

public class TrendComment {

    private Trend trend;

    private Comment comment;

    //region // Getter&Setter
    public Trend getTrend() {
        return trend;
    }

    public void setTrend(Trend trend) {
        this.trend = trend;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
    //endregion
}
