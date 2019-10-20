package net.socialhub.model.service.addition.slack;

import net.socialhub.define.service.slack.SlackFormKey;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;

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
    public List<Reaction> getReactions() {
        if (reactions == null) {
            return super.getReactions();
        }

        List<Reaction> reactions = super.getReactions();
        reactions.addAll(this.reactions);
        return reactions;
    }

    @Override
    public CommentForm getReplyForm() {
        CommentForm form = new CommentForm();
        form.param(SlackFormKey.CHANNEL_KEY, channel);
        form.replyId(getId());
        return form;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    //region // Getter&Setter
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
    //endregion
}
