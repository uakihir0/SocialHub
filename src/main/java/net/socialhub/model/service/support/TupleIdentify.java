package net.socialhub.model.service.support;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Service;

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
