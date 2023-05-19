package net.socialhub.service.bluesky.model;

import bsky4j.util.ATUriParser;
import net.socialhub.core.model.Service;
import net.socialhub.service.microblog.model.MiniBlogComment;

/**
 * Bluesky Comment Model
 * Bluesky のコメント情報
 */
public class BlueskyComment extends MiniBlogComment {

    private String cid;

    /** Reply count */
    private Long replyCount;

    private String likeRecordUri;

    private String repostRecordUri;

    public BlueskyComment(Service service) {
        super(service);
    }


    @Override
    public String getWebUrl() {
        String rkey = ATUriParser.getRKey((String) getId());
        return "https://bsky.app/profile/" +
                getUser().getScreenName() + "/post/" + rkey;
    }

    // region
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public String getLikeRecordUri() {
        return likeRecordUri;
    }

    public void setLikeRecordUri(String likeRecordUri) {
        this.likeRecordUri = likeRecordUri;
    }

    public String getRepostRecordUri() {
        return repostRecordUri;
    }

    public void setRepostRecordUri(String repostRecordUri) {
        this.repostRecordUri = repostRecordUri;
    }

    // endregion
}
