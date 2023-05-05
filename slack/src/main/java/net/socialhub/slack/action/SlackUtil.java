package net.socialhub.slack.action;


import net.socialhub.core.utils.ServiceUtil;

public class SlackUtil implements ServiceUtil {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return ((float) text.length() / 4000.f);
    }
}
