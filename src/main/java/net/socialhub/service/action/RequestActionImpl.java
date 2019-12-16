package net.socialhub.service.action;

import com.google.gson.Gson;
import net.socialhub.define.action.ActionType;
import net.socialhub.define.action.TimeLineActionType;
import net.socialhub.define.action.UsersActionType;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Request;
import net.socialhub.model.service.User;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;
import net.socialhub.service.action.request.UsersRequest;
import net.socialhub.service.action.request.UsersRequestImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static net.socialhub.define.action.TimeLineActionType.ChannelTimeLine;
import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MentionTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MessageTimeLine;
import static net.socialhub.define.action.TimeLineActionType.SearchTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserCommentTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserLikeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserMediaTimeLine;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;
import static net.socialhub.define.action.UsersActionType.SearchUsers;

public class RequestActionImpl implements RequestAction {

    private Logger log = Logger.getLogger(RequestActionImpl.class);

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
        return getUsersRequest(GetFollowingUsers,
                (paging) -> account.action().getFollowingUsers(id, paging),
                new SerializeBuilder(GetFollowingUsers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest getFollowerUsers(Identify id) {
        return getUsersRequest(GetFollowerUsers,
                (paging) -> account.action().getFollowerUsers(id, paging),
                new SerializeBuilder(GetFollowerUsers)
                        .add("id", id.getSerializedIdString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest searchUsers(String query) {
        return getUsersRequest(SearchUsers,
                (paging) -> account.action().searchUsers(query, paging),
                new SerializeBuilder(SearchUsers)
                        .add("query", query));
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        return getCommentsRequest(HomeTimeLine,
                (paging) -> account.action().getHomeTimeLine(paging),
                new SerializeBuilder(HomeTimeLine));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getMentionTimeLine() {
        return getCommentsRequest(MentionTimeLine,
                (paging) -> account.action().getMentionTimeLine(paging),
                new SerializeBuilder(MentionTimeLine));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserCommentTimeLine(Identify id) {
        return getCommentsRequest(UserCommentTimeLine,
                (paging) -> account.action().getUserCommentTimeLine(id, paging),
                new SerializeBuilder(UserCommentTimeLine)
                        .add("id", id.getSerializedIdString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserLikeTimeLine(Identify id) {
        return getCommentsRequest(UserLikeTimeLine,
                (paging) -> account.action().getUserLikeTimeLine(id, paging),
                new SerializeBuilder(UserLikeTimeLine)
                        .add("id", id.getSerializedIdString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserMediaTimeLine(Identify id) {
        return getCommentsRequest(UserMediaTimeLine,
                (paging) -> account.action().getUserMediaTimeLine(id, paging),
                new SerializeBuilder(UserMediaTimeLine)
                        .add("id", id.getSerializedIdString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getSearchTimeLine(String query) {
        return getCommentsRequest(SearchTimeLine,
                (paging) -> account.action().getSearchTimeLine(query, paging),
                new SerializeBuilder(SearchTimeLine)
                        .add("query", query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getChannelTimeLine(Identify id) {
        return getCommentsRequest(ChannelTimeLine,
                (paging) -> account.action().getChannelTimeLine(id, paging),
                new SerializeBuilder(ChannelTimeLine)
                        .add("id", id.getSerializedIdString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getMessageTimeLine(Identify id) {
        return getCommentsRequest(MessageTimeLine,
                (paging) -> account.action().getMessageTimeLine(id, paging),
                new SerializeBuilder(MessageTimeLine)
                        .add("id", id.getSerializedIdString()));
    }

    // ============================================================== //
    // From Serialized
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Request fromSerializedString(String serialize) {

        try {
            SerializeParams params = new Gson().fromJson(serialize, SerializeParams.class);
            String action = params.get("action");

            // Identify
            Identify id = null;
            if (params.contains("id")) {
                id = new Identify(account.getService());
                id.setSerializedIdString(params.get("id"));
            }

            // Query
            String query = null;
            if (params.contains("query")) {
                query = params.get("query");
            }

            // ------------------------------------------------------------- //
            // User Actions
            // ------------------------------------------------------------- //

            if (isTypeIncluded(UsersActionType.values(), action)) {
                switch (UsersActionType.valueOf(action)) {

                case GetFollowingUsers:
                    return getFollowingUsers(id);
                case GetFollowerUsers:
                    return getFollowerUsers(id);
                case SearchUsers:
                    return getSearchTimeLine(query);
                case ChannelUsers:
                    return getChannelTimeLine(id);

                default:
                    log.debug("invalid user action type: " + action);
                    return null;
                }
            }

            // ------------------------------------------------------------- //
            // Comment Actions
            // ------------------------------------------------------------- //

            if (isTypeIncluded(TimeLineActionType.values(), action)) {
                switch (TimeLineActionType.valueOf(action)) {

                case HomeTimeLine:
                    return getHomeTimeLine();
                case MentionTimeLine:
                    return getMentionTimeLine();
                case SearchTimeLine:
                    return getSearchTimeLine(query);
                case ChannelTimeLine:
                    return getChannelTimeLine(id);
                case MessageTimeLine:
                    return getMessageTimeLine(id);
                case UserLikeTimeLine:
                    return getUserLikeTimeLine(id);
                case UserMediaTimeLine:
                    return getUserMediaTimeLine(id);
                case UserCommentTimeLine:
                    return getUserCommentTimeLine(id);

                default:
                    log.debug("invalid comment action type: " + action);
                    return null;
                }
            }

            log.debug("invalid action type: " + action);
            return null;

        } catch (Exception e) {
            log.debug("json parse error.", e);
            return null;
        }
    }

    protected boolean isTypeIncluded(Enum<?>[] members, String action) {
        List<String> names = Stream.of(members).map(Enum::name).collect(toList());
        return names.contains(action);
    }

    // ============================================================== //
    // Inner Class
    // ============================================================== //

    /**
     * Serialize Params
     */
    public static class SerializeParams {
        private Map<String, String> params = new HashMap<>();

        public String get(String key) {
            return params.get(key);
        }

        public boolean contains(String key) {
            return params.containsKey(key);
        }

        public void add(String key, String value) {
            params.put(key, value);
        }
    }

    /**
     * Serialize Builder
     */
    public static class SerializeBuilder {
        private SerializeParams params = new SerializeParams();

        public <T extends Enum<T>> SerializeBuilder(Enum<T> action) {
            add("action", action.name());
        }

        public SerializeBuilder add(String key, String value) {
            params.add(key, value);
            return this;
        }

        public String toJson() {
            return new Gson().toJson(params);
        }
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    // User
    protected UsersRequestImpl getUsersRequest(
            ActionType type,
            Function<Paging, Pageable<User>> usersFunction,
            SerializeBuilder serializeBuilder) {

        UsersRequestImpl request = new UsersRequestImpl();
        request.setSerializeBuilder(serializeBuilder);
        request.setUsersFunction(usersFunction);
        request.setActionType(type);
        request.setAccount(account);
        return request;
    }

    // Comments
    protected CommentsRequestImpl getCommentsRequest(
            ActionType type,
            Function<Paging, Pageable<Comment>> commentsFunction,
            SerializeBuilder serializeBuilder) {

        CommentsRequestImpl request = new CommentsRequestImpl();
        request.setSerializeBuilder(serializeBuilder);
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
