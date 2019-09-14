package net.socialhub.service.slack;

import net.socialhub.service.Utils;

public class SlackUtil implements Utils.ServiceUtils {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return ((float) text.length() / 4000.f);
    }
}
