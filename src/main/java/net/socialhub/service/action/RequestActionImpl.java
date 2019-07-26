package net.socialhub.service.action;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.*;

import static net.socialhub.define.action.TimeLineActionType.*;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;

public class RequestActionImpl implements RequestAction {

    private AccountAction action;

    private ActionType type;

    private Object[] args;

    public RequestActionImpl(AccountAction action, ActionType type, Object... args) {
        this.action = action;
        this.type = type;
        this.args = args;
    }

    /**
     * {@inheritDoc}
     */
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
        if (type == UserMediaTimeLine) {
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getUserMediaTimeLine((Identify) args[0], paging);
            }
        }
        if (type == SearchTimeLine) {
            if ((args.length == 1) && //
                    (args[0] instanceof String)) {
                return action.getSearchTimeLine((String) args[0], paging);
            }

        }

        throw new NotImplimentedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getUsers(Paging paging) {

        if (type == GetFollowingUsers) {
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getFollowingUsers((Identify) args[0], paging);
            }
        }

        if (type == GetFollowerUsers) {
            if ((args.length == 1) && //
                    (args[0] instanceof Identify)) {
                return action.getFollowerUsers((Identify) args[0], paging);
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
