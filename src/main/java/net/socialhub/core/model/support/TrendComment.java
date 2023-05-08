package net.socialhub.core.model.support;

import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Trend;

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
