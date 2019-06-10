package net.socialhub.service.action;

import net.socialhub.define.ActionType;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;

public class RequestActionImpl implements RequestAction {

    private AccountAction action;

    private ActionType type;

    private Object[] args;

    public RequestActionImpl(AccountAction action, ActionType type, Object... args) {
        this.action = action;
        this.type = type;
        this.args = args;
    }

    @Override
    public Pageable<Comment> getTimeLine(Paging paging) {

        switch (type) {
        case HomeTimeLine:
            return action.getHomeTimeLine(paging);

        case MentionTimeLine:
            return action.getMentionTimeLine(paging);

        case UserCommentTimeLine:
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getUserCommentTimeLine((Identify) args[0], paging);
            }

        case UserLikeTimeLine:
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getUserLikeTimeLine((Identify) args[0], paging);
            }
        }

        throw new NotImplimentedException();
    }
}
