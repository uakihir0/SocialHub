package net.socialhub.model.service.addition.slack;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;

import java.util.List;

/**
 * Slack Comment Model
 * Slack のコメント情報
 */
public class SlackComment extends Comment {

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

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }
}
