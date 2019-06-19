package net.socialhub.model.service.addition.tumblr;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Service;

/**
 * Tumblr Comment Model
 * Tumblr のコメント情報
 */
public class TumblrComment extends Comment {

    public TumblrComment(Service service) {
        super(service);
    }

    // Reblog Key
    private String reblogKey;

    //region // Getter&Setter
    public String getReblogKey() {
        return reblogKey;
    }

    public void setReblogKey(String reblogKey) {
        this.reblogKey = reblogKey;
    }
    //endregion
}

