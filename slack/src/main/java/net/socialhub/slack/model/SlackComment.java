package net.socialhub.slack.model;

import net.socialhub.slack.define.SlackFormKey;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Reaction;
import net.socialhub.core.model.service.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Slack Comment Model
 * Slack のコメント情報
 */
public class SlackComment extends Comment {

    private String channel;

    private String threadId;

    private List<Reaction> reactions;

    public SlackComment(Service service) {
        super(service);
    }

    @Override
    public String getWebUrl() {
        return "https://app.slack.com/archives/" +
                getChannel() + "/p" + getId();
    }

    @Override
    public List<Reaction> getReactions() {
        if (reactions == null) {
            return super.getReactions();
        }

        List<Reaction> results = super.getReactions();
        results.addAll(reactions);
        return results;
    }

    @Override
    public void applyReaction(Reaction reaction) {
        if (reactions == null) {
            reactions = new ArrayList<>();
            reactions.add(reaction);
            return;
        }

        for (Reaction exist : reactions) {
            if (exist.getName().equals(reaction.getName())) {
                if (reaction.getReacting() && !exist.getReacting()) {
                    exist.setCount(exist.getCount() + 1);
                    exist.setReacting(true);
                }
                if (!reaction.getReacting() && exist.getReacting()) {
                    exist.setCount(exist.getCount() - 1);
                    exist.setReacting(false);
                }
                return;
            }
        }

        reactions.add(reaction);
    }

    @Override
    public CommentForm getReplyForm() {
        CommentForm form = new CommentForm();
        form.param(SlackFormKey.CHANNEL_KEY, channel);
        form.message(getDirectMessage());
        form.replyId(getId());
        return form;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    // region // Getter&Setter
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
    // endregion
}
