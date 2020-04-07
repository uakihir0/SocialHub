package net.socialhub.service.misskey;

import net.socialhub.service.Utils;

public class MisskeyUtil implements Utils.ServiceUtils {

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
