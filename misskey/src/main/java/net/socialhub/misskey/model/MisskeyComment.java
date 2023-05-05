package net.socialhub.misskey.model;

import net.socialhub.misskey.define.MisskeyVisibility;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Reaction;
import net.socialhub.core.model.service.Service;
import net.socialhub.microblog.model.MiniBlogComment;

import java.util.ArrayList;
import java.util.List;

public class MisskeyComment extends MiniBlogComment {

    /** Requester host */
    private String requesterHost;

    /** ID for Paging */
    private String pagingId;

    /** Warning text (Mastodon only) */
    private AttributedString spoilerText;

    /** Open range */
    private MisskeyVisibility visibility;

    /** User replied this comment */
    private Long replyCount;

    /** Reactions */
    private List<Reaction> reactions;

    public MisskeyComment(Service service) {
        super(service);
    }

    @Override
    public String getWebUrl() {
        return "https://"
                + requesterHost
                + "/notes/"
                + getId().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MisskeyComment) {
            return getId().equals(((MisskeyComment) obj).getId());
        }
        return false;
    }

    @Override
    public Comment getDisplayComment() {

        // Misskey の場合はコメントが必ず入る設定になっているので空文字か確認
        return ((getText() == null || getText().getDisplayText().isEmpty())
                && getSharedComment() != null) ? getSharedComment() : this;
    }

    @Override
    public List<Reaction> getReactions() {
        List<Reaction> results = new ArrayList<>();

        Reaction reply = new Reaction();
        reply.setCount(replyCount);
        reply.setName("reply");
        results.add(reply);

        Reaction share = new Reaction();
        share.setReacting(getShared());
        share.setCount(getShareCount());
        share.setName("share");
        results.add(share);

        results.addAll(reactions);
        return results;
    }

    @Override
    public CommentForm getQuoteForm(){
        CommentForm form = new CommentForm();
        form.quoteId(getId());
        form.message(false);
        return form;
    }

    public String getIdForPaging() {
        return (pagingId != null) ? pagingId : (String) getId();
    }

    // region // Getter&Setter
    public String getRequesterHost() {
        return requesterHost;
    }

    public void setRequesterHost(String requesterHost) {
        this.requesterHost = requesterHost;
    }

    public void setPagingId(String pagingId) {
        this.pagingId = pagingId;
    }

    public AttributedString getSpoilerText() {
        return spoilerText;
    }

    public void setSpoilerText(AttributedString spoilerText) {
        this.spoilerText = spoilerText;
    }

    public MisskeyVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(MisskeyVisibility visibility) {
        this.visibility = visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = MisskeyVisibility.of(visibility);
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }
    // endregion
}
