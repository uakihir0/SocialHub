package net.socialhub.service.mastodon.action;

import net.socialhub.core.utils.ServiceUtil;

public class MastodonUtil implements ServiceUtil {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return (((float) text.length()) / 500.f);
    }
}
