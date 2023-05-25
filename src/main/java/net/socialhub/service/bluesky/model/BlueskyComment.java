package net.socialhub.service.bluesky.model;

import bsky4j.util.ATUriParser;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Reaction;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.service.microblog.model.MiniBlogComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Bluesky Comment Model
 * Bluesky のコメント情報
 */
public class BlueskyComment extends MiniBlogComment {

    private String cid;

    /** Is Simple Object */
    private Boolean isSimple;

    /** Reply count */
    private Long replyCount;

    private String likeRecordUri;

    private String repostRecordUri;

    private Identify replayRootTo;

    public BlueskyComment(Service service) {
        super(service);
    }

    @Override
    public String getWebUrl() {
        String rkey = ATUriParser.getRKey((String) getId());
        return "https://bsky.app/profile/" +
                getUser().getScreenName() + "/post/" + rkey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlueskyComment) {
            return getId().equals(((BlueskyComment) obj).getId());
        }
        return false;
    }

    @Override
    public List<Reaction> getReactions() {
        List<Reaction> results = new ArrayList<>();

        Reaction reply = new Reaction();
        reply.setCount(replyCount);
        reply.setName("reply");
        results.add(reply);
        return results;
    }

    @Override
    public CommentForm getQuoteForm() {
        CommentForm form = new CommentForm();
        form.quoteId(getId());
        form.message(false);
        return form;
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

    public Identify getReplayRootTo() {
        return replayRootTo;
    }

    public void setReplayRootTo(Identify replayRootTo) {
        this.replayRootTo = replayRootTo;
    }

    public Boolean getSimple() {
        return isSimple;
    }

    public void setSimple(Boolean simple) {
        isSimple = simple;
    }
    // endregion
}
