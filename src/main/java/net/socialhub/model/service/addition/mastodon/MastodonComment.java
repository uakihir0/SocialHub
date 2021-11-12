package net.socialhub.model.service.addition.mastodon;

import net.socialhub.define.service.mastodon.MastodonVisibility;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Emoji;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.addition.MiniBlogComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Mastodon Comment Model
 * Mastodon のコメント情報
 */
public class MastodonComment extends MiniBlogComment {

    /** Requester host */
    private String requesterHost;

    /** Warning text (Mastodon only) */
    private AttributedString spoilerText;

    /** Open range */
    private MastodonVisibility visibility;

    /** Reply count */
    private Long replyCount;

    /** emojis which contains */
    private List<Emoji> emojis;

    public MastodonComment(Service service) {
        super(service);
    }

    @Override
    public String getWebUrl() {
        if (getService().isPixelFed()) {
            return "https://"
                    + requesterHost
                    + "/p/" + getUser().getScreenName()
                    + "/" + getId().toString();
        }
        if (getService().isPleroma()) {
            return "https://"
                    + requesterHost
                    + "/notice/"
                    + getId().toString();
        }
        return "https://"
                + requesterHost
                + "/web/statuses/"
                + getId().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MastodonComment) {
            return getId().equals(((MastodonComment) obj).getId());
        }
        return false;
    }

    @Override
    public List<Reaction> getReactions() {
        List<Reaction> reactions = new ArrayList<>();

        if (replyCount != null && replyCount > 0) {
            Reaction reply = new Reaction();
            reply.setCount(replyCount);
            reply.setName("reply");
            reactions.add(reply);
        }

        reactions.addAll(super.getReactions());
        return reactions;
    }

    // region // Getter&Setter
    public String getRequesterHost() {
        return requesterHost;
    }

    public void setRequesterHost(String requesterHost) {
        this.requesterHost = requesterHost;
    }

    public AttributedString getSpoilerText() {
        return spoilerText;
    }

    public void setSpoilerText(AttributedString spoilerText) {
        this.spoilerText = spoilerText;
    }

    public MastodonVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(MastodonVisibility visibility) {
        this.visibility = visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = MastodonVisibility.of(visibility);
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public List<Emoji> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<Emoji> emojis) {
        this.emojis = emojis;
    }
    // endregion
}
