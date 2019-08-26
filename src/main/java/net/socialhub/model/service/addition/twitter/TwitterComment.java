package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.service.Service;
import net.socialhub.model.service.addition.MiniBlogComment;

/**
 * Twitter Comment Model
 * Twitter のコメント情報
 */
public class TwitterComment extends MiniBlogComment {

    public TwitterComment(Service service) {
        super(service);
    }

    @Override
    public String getUrl() {
        return "https://twitter.com/"
                + getUser().getScreenName()
                + "/status/" + getId().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TwitterComment) {
            return getId().equals(((TwitterComment) obj).getId());
        }
        return false;
    }
}
