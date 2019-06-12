package net.socialhub.service.action;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;

import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MentionTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserCommentTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserLikeTimeLine;

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
    public Pageable<Comment> getComments(Paging paging) {

        if (type == HomeTimeLine) {
            return action.getHomeTimeLine(paging);
        }
        if (type == MentionTimeLine) {
            return action.getMentionTimeLine(paging);
        }
        if (type == UserCommentTimeLine) {
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getUserCommentTimeLine((Identify) args[0], paging);
            }
        }
        if (type == UserLikeTimeLine) {
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getUserLikeTimeLine((Identify) args[0], paging);
            }
        }

        throw new NotImplimentedException();
    }

    //region // Getter&Setter
    public AccountAction getAction() {
        return action;
    }

    public void setAction(AccountAction action) {
        this.action = action;
    }
    //endregion
}
