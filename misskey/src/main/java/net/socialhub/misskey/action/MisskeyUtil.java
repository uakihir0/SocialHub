package net.socialhub.misskey.action;

import net.socialhub.core.utils.ServiceUtil;

public class MisskeyUtil implements ServiceUtil {

    /**
     * Default max comment length
     * (each instance changed)
     */
    private float maxCommentLength = 1000.f;

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return (((float) text.length()) / maxCommentLength);
    }

    // region
    public float getMaxCommentLength() {
        return maxCommentLength;
    }

    public void setMaxCommentLength(float maxCommentLength) {
        this.maxCommentLength = maxCommentLength;
    }
    // endregion
}
