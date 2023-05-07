package net.socialhub.core.model.support;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Service;

public class TupleIdentify extends Identify {

    public TupleIdentify(Service service) {
        super(service);
    }

    private Object subId;

    /**
     * Identify for Tumble Blog
     * (ブログポストを取得するために必要)
     */
    public void forTumblrBlogIdentify(String blogName, String postId) {
        setId(postId);
        setSubId(blogName);
    }

    //region // Getter&Setter
    public Object getSubId() {
        return subId;
    }

    public void setSubId(Object subId) {
        this.subId = subId;
    }
    //endregion
}
