package net.socialhub.service.mastodon;

import net.socialhub.service.Utils;

public class MastodonUtil implements Utils.ServiceUtils {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return ((float) text.length() / 500.f);
    }
}
