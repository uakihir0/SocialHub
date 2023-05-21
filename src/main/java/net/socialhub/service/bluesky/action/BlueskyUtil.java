package net.socialhub.service.bluesky.action;

import net.socialhub.core.utils.ServiceUtil;

public class BlueskyUtil implements ServiceUtil {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return (((float) text.length()) / 300.f);
    }
}
