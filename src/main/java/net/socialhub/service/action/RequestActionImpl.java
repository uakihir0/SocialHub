package net.socialhub.service.action;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;
import net.socialhub.service.action.request.UsersRequest;
import net.socialhub.service.action.request.UsersRequestImpl;

import java.util.function.Function;

import static net.socialhub.define.action.TimeLineActionType.*;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;
import static net.socialhub.define.action.UsersActionType.SearchUsers;

public class RequestActionImpl implements RequestAction {

    protected Account account;

    public RequestActionImpl(Account account) {
        this.account = account;
    }

    // ============================================================== //
    // User API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest getFollowingUsers(Identify id) {
        return getUsersRequest(GetFollowingUsers, (paging) ->
                account.action().getFollowingUsers(id, paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest getFollowerUsers(Identify id) {
        return getUsersRequest(GetFollowerUsers, (paging) ->
                account.action().getFollowerUsers(id, paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest searchUsers(String query) {
        return getUsersRequest(SearchUsers, (paging) ->
                account.action().searchUsers(query, paging));
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        return getCommentsRequest(HomeTimeLine, (paging) ->
                account.action().getHomeTimeLine(paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getMentionTimeLine() {
        return getCommentsRequest(MentionTimeLine, (paging) ->
                account.action().getMentionTimeLine(paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserCommentTimeLine(Identify id) {
        return getCommentsRequest(UserCommentTimeLine, (paging) ->
                account.action().getUserCommentTimeLine(id, paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserLikeTimeLine(Identify id) {
        return getCommentsRequest(UserLikeTimeLine, (paging) ->
                account.action().getUserLikeTimeLine(id, paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserMediaTimeLine(Identify id) {
        return getCommentsRequest(UserMediaTimeLine, (paging) ->
                account.action().getUserMediaTimeLine(id, paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getSearchTimeLine(String query) {
        return getCommentsRequest(SearchTimeLine, (paging) ->
                account.action().getSearchTimeLine(query, paging));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getChannelTimeLine(Identify id) {
        return getCommentsRequest(ChannelTimeLine, (paging) ->
                account.action().getChannelTimeLine(id, paging));
    }


    // ============================================================== //
    // Support
    // ============================================================== //

    // User
    protected UsersRequestImpl getUsersRequest(
            ActionType type, Function<Paging, Pageable<User>> usersFunction) {

        UsersRequestImpl request = new UsersRequestImpl();
        request.setUsersFunction(usersFunction);
        request.setActionType(type);
        request.setAccount(account);
        return request;
    }

    // Comments
    protected CommentsRequestImpl getCommentsRequest(
            ActionType type, Function<Paging, Pageable<Comment>> commentsFunction) {

        CommentsRequestImpl request = new CommentsRequestImpl();
        request.setCommentsFunction(commentsFunction);
        request.setActionType(type);
        request.setAccount(account);
        return request;
    }

    //region // Getter&Setter
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    //endregion
}
