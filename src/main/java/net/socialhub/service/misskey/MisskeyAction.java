package net.socialhub.service.misskey;

import misskey4j.Misskey;
import misskey4j.MisskeyException;
import misskey4j.api.model.PollRequest;
import misskey4j.api.request.UsersListsListRequest;
import misskey4j.api.request.UsersListsShowRequest;
import misskey4j.api.request.blocks.BlocksCreateRequest;
import misskey4j.api.request.blocks.BlocksDeleteRequest;
import misskey4j.api.request.favorites.FavoritesCreateRequest;
import misskey4j.api.request.favorites.FavoritesDeleteRequest;
import misskey4j.api.request.files.FilesCreateRequest;
import misskey4j.api.request.following.FollowingCreateRequest;
import misskey4j.api.request.following.FollowingDeleteRequest;
import misskey4j.api.request.hashtags.HashtagsTrendRequest;
import misskey4j.api.request.i.IFavoritesRequest;
import misskey4j.api.request.i.INotificationsRequest;
import misskey4j.api.request.i.IRequest;
import misskey4j.api.request.messages.MessagingHistoryRequest;
import misskey4j.api.request.messages.MessagingMessagesCreateRequest;
import misskey4j.api.request.messages.MessagingMessagesRequest;
import misskey4j.api.request.meta.MetaRequest;
import misskey4j.api.request.mutes.MutesCreateRequest;
import misskey4j.api.request.mutes.MutesDeleteRequest;
import misskey4j.api.request.notes.NoteUnrenoteRequest;
import misskey4j.api.request.notes.NotesChildrenRequest;
import misskey4j.api.request.notes.NotesConversationRequest;
import misskey4j.api.request.notes.NotesCreateRequest;
import misskey4j.api.request.notes.NotesDeleteRequest;
import misskey4j.api.request.notes.NotesFeaturedRequest;
import misskey4j.api.request.notes.NotesGlobalTimelineRequest;
import misskey4j.api.request.notes.NotesLocalTimelineRequest;
import misskey4j.api.request.notes.NotesSearchRequest;
import misskey4j.api.request.notes.NotesShowRequest;
import misskey4j.api.request.notes.NotesTimelineRequest;
import misskey4j.api.request.notes.NotesUserListTimelineRequest;
import misskey4j.api.request.notes.UsersNotesRequest;
import misskey4j.api.request.other.ServiceWorkerRegisterRequest;
import misskey4j.api.request.polls.PollsVoteRequest;
import misskey4j.api.request.protocol.PagingBuilder;
import misskey4j.api.request.reactions.ReactionsCreateRequest;
import misskey4j.api.request.reactions.ReactionsDeleteRequest;
import misskey4j.api.request.users.UsersFollowersRequest;
import misskey4j.api.request.users.UsersFollowingsRequest;
import misskey4j.api.request.users.UsersRelationRequest;
import misskey4j.api.request.users.UsersSearchRequest;
import misskey4j.api.request.users.UsersShowMultipleRequest;
import misskey4j.api.request.users.UsersShowSingleRequest;
import misskey4j.api.response.UsersListsListResponse;
import misskey4j.api.response.UsersListsShowResponse;
import misskey4j.api.response.files.FilesCreateResponse;
import misskey4j.api.response.hashtags.HashtagsTrendResponse;
import misskey4j.api.response.i.IFavoritesResponse;
import misskey4j.api.response.i.INotificationsResponse;
import misskey4j.api.response.i.IResponse;
import misskey4j.api.response.messages.MessagingHistoryResponse;
import misskey4j.api.response.messages.MessagingMessagesResponse;
import misskey4j.api.response.meta.MetaResponse;
import misskey4j.api.response.notes.NotesChildrenResponse;
import misskey4j.api.response.notes.NotesConversationResponse;
import misskey4j.api.response.notes.NotesFeaturedResponse;
import misskey4j.api.response.notes.NotesGlobalTimelineResponse;
import misskey4j.api.response.notes.NotesLocalTimelineResponse;
import misskey4j.api.response.notes.NotesSearchResponse;
import misskey4j.api.response.notes.NotesShowResponse;
import misskey4j.api.response.notes.NotesTimelineResponse;
import misskey4j.api.response.notes.NotesUserListTimelineResponse;
import misskey4j.api.response.notes.UsersNotesResponse;
import misskey4j.api.response.users.UsersFollowersResponse;
import misskey4j.api.response.users.UsersFollowingsResponse;
import misskey4j.api.response.users.UsersRelationResponse;
import misskey4j.api.response.users.UsersSearchResponse;
import misskey4j.api.response.users.UsersShowResponse;
import misskey4j.entity.Error.ErrorDetail;
import misskey4j.entity.Follow;
import misskey4j.entity.Message;
import misskey4j.entity.Note;
import misskey4j.entity.Notification;
import misskey4j.entity.contant.NotificationType;
import misskey4j.entity.share.Response;
import misskey4j.stream.MisskeyStream;
import misskey4j.stream.callback.ClosedCallback;
import misskey4j.stream.callback.ErrorCallback;
import misskey4j.stream.callback.FollowedCallback;
import misskey4j.stream.callback.MentionCallback;
import misskey4j.stream.callback.NoteCallback;
import misskey4j.stream.callback.NotificationCallback;
import misskey4j.stream.callback.OpenedCallback;
import misskey4j.stream.callback.RenoteCallback;
import misskey4j.stream.callback.ReplayCallback;
import net.socialhub.define.service.mastodon.MastodonReactionType;
import net.socialhub.define.service.misskey.MisskeyFormKey;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.request.MediaForm;
import net.socialhub.model.request.PollForm;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;
import net.socialhub.model.service.Error;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.misskey.MisskeyPaging;
import net.socialhub.model.service.addition.misskey.MisskeyPoll;
import net.socialhub.model.service.addition.misskey.MisskeyThread;
import net.socialhub.model.service.event.CommentEvent;
import net.socialhub.model.service.event.NotificationEvent;
import net.socialhub.model.service.event.UserEvent;
import net.socialhub.model.service.paging.OffsetPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.callback.EventCallback;
import net.socialhub.service.action.callback.comment.MentionCommentCallback;
import net.socialhub.service.action.callback.comment.NotificationCommentCallback;
import net.socialhub.service.action.callback.comment.UpdateCommentCallback;
import net.socialhub.service.action.callback.lifecycle.ConnectCallback;
import net.socialhub.service.action.callback.lifecycle.DisconnectCallback;
import net.socialhub.service.action.callback.user.FollowUserCallback;
import net.socialhub.service.action.specific.MicroBlogAccountAction;
import net.socialhub.utils.CollectionUtil;
import net.socialhub.utils.HandlingUtil;
import net.socialhub.utils.MapperUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class MisskeyAction extends AccountActionImpl implements MicroBlogAccountAction {

    private static final Logger logger = Logger.getLogger(MisskeyAction.class);

    private ServiceAuth<Misskey> auth;

    /** Reaction Candidate Cache */
    private List<ReactionCandidate> reactionCandidate;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            Response<IResponse> response = misskey.accounts()
                    .i(IRequest.builder().build());

            me = MisskeyMapper.user(response.get(),
                    misskey.getHost(), service);
            return me;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            UsersShowResponse users;

            // User のアカウント名で取得する場合
            if (((String) id.getId()).startsWith("@")) {
                String[] elem = ((String) id.getId()).split("@");
                String host = (elem.length > 2) ? elem[2] : misskey.getHost();

                users = misskey.users().show(UsersShowSingleRequest.builder()
                        .username(elem[1]).host(host)
                        .build()).get();
            } else {
                users = misskey.users().show(UsersShowSingleRequest.builder()
                        .userId((String) id.getId())
                        .build()).get();
            }

            return MisskeyMapper.user(users,
                    misskey.getHost(), service);
        });
    }

    /**
     * {@inheritDoc}
     * https://misskey.io/@syuilo
     * https://misskey.io/@syuilo@misskey.io
     */
    @Override
    public User getUser(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Pattern regex = Pattern.compile("https://(.+?)/@(.+)");
            Matcher matcher = regex.matcher(url);

            if (matcher.matches()) {
                String host = matcher.group(1);
                String identify = matcher.group(2);

                if (identify.contains("@")) {
                    String format = ("@" + identify);
                    return getUser(new Identify(service, format));
                } else {
                    String format = ("@" + identify + "@" + host);
                    return getUser(new Identify(service, format));
                }
            }
            throw new SocialHubException("this url is not supported format.");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();

            misskey.following().create(
                    FollowingCreateRequest.builder()
                            .userId((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();

            misskey.following().delete(
                    FollowingDeleteRequest.builder()
                            .userId((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.mutes().create(
                    MutesCreateRequest.builder()
                            .userId((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.mutes().delete(
                    MutesDeleteRequest.builder()
                            .userId((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.blocks().create(
                    BlocksCreateRequest.builder()
                            .userId((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.blocks().delete(
                    BlocksDeleteRequest.builder()
                            .userId((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Response<UsersRelationResponse[]> response =
                    misskey.users().relation(UsersRelationRequest.builder()
                            .userId(singletonList((String) id.getId()))
                            .build());

            return MisskeyMapper.relationship(response.get()[0]);
        });
    }

    // ============================================================== //
    // User API
    // ユーザー関連 API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            UsersFollowingsRequest.UsersFollowingsRequestBuilder builder =
                    UsersFollowingsRequest.builder();

            setPaging(builder, paging);
            Response<UsersFollowingsResponse[]> response =
                    misskey.users().followings(builder
                            .userId((String) id.getId())
                            .build());

            return MisskeyMapper.users(
                    Stream.of(response.get())
                            .map(Follow::getFollowee)
                            .collect(toList()),
                    misskey.getHost(),
                    service,
                    paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            UsersFollowersRequest.UsersFollowersRequestBuilder builder =
                    UsersFollowersRequest.builder();

            setPaging(builder, paging);
            Response<UsersFollowersResponse[]> response =
                    misskey.users().followers(builder
                            .userId((String) id.getId())
                            .build());

            return MisskeyMapper.users(
                    Stream.of(response.get())
                            .map(Follow::getFollower)
                            .collect(toList()),
                    misskey.getHost(),
                    service,
                    paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> searchUsers(String query, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            UsersSearchRequest.UsersSearchRequestBuilder builder =
                    UsersSearchRequest.builder();

            if (paging != null) {
                if (paging.getCount() != null) {
                    builder.limit(paging.getCount());
                    if (paging.getCount() > 100) {
                        builder.limit(100L);
                    }
                }
                if (paging instanceof OffsetPaging) {
                    OffsetPaging of = ((OffsetPaging) paging);
                    if (of.getOffset() != null) {
                        builder.offset(of.getOffset());
                    }
                }
            }

            builder.query(query);
            Response<UsersSearchResponse[]> response =
                    misskey.users().search(builder.build());

            Pageable<User> results = MisskeyMapper.users(
                    Stream.of(response.get()).collect(toList()),
                    misskey.getHost(), service, paging);

            results.setPaging(OffsetPaging.fromPaging(paging));
            return results;
        });
    }

    // ============================================================== //
    // Timeline
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            NotesTimelineRequest.NotesTimelineRequestBuilder builder =
                    NotesTimelineRequest.builder();

            setPaging(builder, paging);
            Response<NotesTimelineResponse[]> response =
                    misskey.notes().timeline(builder.build());

            return MisskeyMapper.timeLine(
                    Stream.of(response.get())
                            // Remove featured notes.
                            .filter(e -> e.getFeaturedId() == null)
                            .toArray(Note[]::new),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            INotificationsRequest.INotificationsRequestBuilder builder =
                    INotificationsRequest.builder();
            setPaging(builder, paging);

            builder.markAsRead(true);
            builder.includeTypes(singletonList(NotificationType.REPLY.code()));

            Response<INotificationsResponse[]> response =
                    misskey.accounts().iNotifications(builder.build());

            return MisskeyMapper.mentions(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            UsersNotesRequest.UsersNotesRequestBuilder builder =
                    UsersNotesRequest.builder();

            builder.userId((String) id.getId());
            setPaging(builder, paging);

            Response<UsersNotesResponse[]> response =
                    misskey.notes().users(builder.build());

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        if (id != null) {

            // 自分の分しか取得できないので id が自分でない場合は例外
            if (id.getId().equals(getUserMeWithCache().getId())) {

                Misskey misskey = auth.getAccessor();
                Service service = getAccount().getService();


                IFavoritesRequest.IFavoritesRequestBuilder builder =
                        IFavoritesRequest.builder();

                setPaging(builder, paging);
                Response<IFavoritesResponse[]> response =
                        misskey.accounts().iFavorites(builder.build());

                return MisskeyMapper.timeLine(
                        Stream.of(response.get())
                                .map(IFavoritesResponse::getNote)
                                .filter(Objects::nonNull)
                                .toArray(Note[]::new),
                        misskey.getHost(), service, paging);
            }
        }

        throw new NotSupportedException( //
                "Sorry, user favorites timeline is only support only verified account on Misskey.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            UsersNotesRequest.UsersNotesRequestBuilder builder =
                    UsersNotesRequest.builder();

            builder.userId((String) id.getId());
            builder.withFiles(true);
            setPaging(builder, paging);

            Response<UsersNotesResponse[]> response =
                    misskey.notes().users(builder.build());

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            NotesSearchRequest.NotesSearchRequestBuilder builder =
                    NotesSearchRequest.builder();

            builder.query(query);
            setPaging(builder, paging);

            Response<NotesSearchResponse[]> response =
                    misskey.notes().search(builder.build());

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public void postComment(CommentForm req) {
        if (req.isMessage()) {
            postMessage(req);
            return;
        }

        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            ExecutorService pool = Executors.newCachedThreadPool();

            NotesCreateRequest.NotesCreateRequestBuilder builder =
                    NotesCreateRequest.builder();

            // 文言の追加
            builder.text(req.getText());

            // 返信の処理
            if (req.getReplyId() != null) {
                builder.replyId((String) req.getReplyId());
            }

            // 引用 RT の場合はその ID を設定
            if (req.getQuoteId() != null) {
                builder.renoteId((String) req.getQuoteId());
            }

            // 画像の処理
            if (req.getImages() != null && !req.getImages().isEmpty()) {
                List<String> fileIds = new ArrayList<>();
                builder.fileIds(fileIds);

                // 画像を並列でアップロードする
                List<Future<String>> medias = req.getImages() //
                        .stream().map(image -> pool.submit(() -> {
                            InputStream input = new ByteArrayInputStream(image.getData());
                            Response<FilesCreateResponse> response = misskey.files()
                                    .create(FilesCreateRequest.builder()
                                            .isSensitive(req.isSensitive())
                                            .name(image.getName())
                                            .stream(input)
                                            .force(true)
                                            .build());

                            return response.get().getId();
                        })).collect(toList());

                fileIds.addAll(medias.stream().map( //
                        (e) -> HandlingUtil.runtime(e::get)) //
                        .collect(toList()));
            }

            // 投票
            if (req.getPoll() != null) {
                PollForm poll = req.getPoll();

                builder.poll(PollRequest
                        .builder()
                        .choices(poll.getOptions())
                        .multiple(poll.getMultiple())
                        .expiredAfter(poll.getExpiresIn() * 1000 * 60)
                        .build());
            }

            // 公開範囲
            if (req.getVisibility() != null) {
                builder.visibility(req.getVisibility());
            }
            
            misskey.notes().create(builder.build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment getComment(Identify id) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            Response<NotesShowResponse> response =
                    misskey.notes().show(NotesShowRequest.builder()
                            .noteId((String) id.getId())
                            .build());

            return MisskeyMapper.comment(response.get(),
                    misskey.getHost(), service);
        });
    }

    /**
     * {@inheritDoc}
     * Parse Misskey Post's url, like:
     * https://misskey.io/notes/8axwbcxiff
     */
    @Override
    public Comment getComment(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Pattern regex = Pattern.compile("https://(.+?)/notes/(.+)");
            Matcher matcher = regex.matcher(url);

            if (matcher.matches()) {
                String id = matcher.group(2);
                Identify identify = new Identify(service, id);
                return getComment(identify);
            }

            throw new SocialHubException("this url is not supported format.");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeComment(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.favorites().create(FavoritesCreateRequest.builder()
                    .noteId((String) id.getId())
                    .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlikeComment(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.favorites().delete(FavoritesDeleteRequest.builder()
                    .noteId((String) id.getId())
                    .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shareComment(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.notes().create(NotesCreateRequest.builder()
                    .renoteId((String) id.getId())
                    .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshareComment(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.notes().unrenote(NoteUnrenoteRequest.builder()
                    .noteId((String) id.getId())
                    .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (MastodonReactionType.Favorite.getCode().contains(type)) {
                likeComment(id);
                return;
            }
            if (MastodonReactionType.Reblog.getCode().contains(type)) {
                retweetComment(id);
                return;
            }

            Misskey misskey = auth.getAccessor();
            misskey.reactions().create(ReactionsCreateRequest.builder()
                    .noteId((String) id.getId())
                    .reaction(reaction)
                    .build());
            return;
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (MastodonReactionType.Favorite.getCode().contains(type)) {
                unlikeComment(id);
                return;
            }
            if (MastodonReactionType.Reblog.getCode().contains(type)) {
                unretweetComment(id);
                return;
            }

            // ユーザーごとにリアクションは一つのみ
            Misskey misskey = auth.getAccessor();
            misskey.reactions().delete(ReactionsDeleteRequest.builder()
                    .noteId((String) id.getId())
                    .build());
            return;
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComment(Identify id) {
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.notes().delete(NotesDeleteRequest.builder()
                    .noteId((String) id.getId())
                    .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        if (this.reactionCandidate != null) {
            return this.reactionCandidate;
        }

        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Response<MetaResponse> response =
                    misskey.meta().meta(MetaRequest.builder()
                            .detail(true)
                            .build());

            this.reactionCandidate = MisskeyMapper.reactionCandidates(
                    response.get().getEmojis());
            return this.reactionCandidate;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext(Identify id) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            ExecutorService pool = Executors.newCachedThreadPool();

            String displayId = (String) ((id instanceof Comment) ? //
                    ((Comment) id).getDisplayComment().getId() : id.getId());

            Future<Response<NotesConversationResponse[]>> conversationFuture =
                    pool.submit(() -> misskey.notes().conversation(
                            NotesConversationRequest.builder()
                                    .noteId(displayId)
                                    .limit(100L)
                                    .build()));

            Future<Response<NotesChildrenResponse[]>> childrenFuture =
                    pool.submit(() -> misskey.notes().children(
                            NotesChildrenRequest.builder()
                                    .noteId(displayId)
                                    .limit(100L)
                                    .build()));

            Response<NotesConversationResponse[]> conversation = conversationFuture.get();
            Response<NotesChildrenResponse[]> children = childrenFuture.get();

            // Children の後に続くコメント類を取得
            ArrayList<Note> descendants = Stream.of(children.get())
                    .collect(toCollection(ArrayList::new));

            getMoreReplies(misskey, pool, descendants, descendants.stream()
                    .filter(this::hasMoreReactionPossibility)
                    .map(Note::getId).collect(toList()));

            // コンテキストの組み立て
            Context context = new Context();
            context.setAncestors(Stream.of(conversation.get()) //
                    .map(e -> MisskeyMapper.comment(e, misskey.getHost(), service)) //
                    .collect(toList()));

            context.setDescendants(descendants.stream() //
                    .map(e -> MisskeyMapper.comment(e, misskey.getHost(), service)) //
                    .collect(toList()));

            MapperUtil.sortContext(context);
            return context;
        });
    }

    // さらなる返信を探して notes に追加
    private void getMoreReplies(
            Misskey misskey,
            ExecutorService pool,
            ArrayList<Note> notes,
            List<String> nextIds) throws Exception {

        // No more replies to get (or limit)
        if ((notes.size() >= 100) || (nextIds.size() == 0)) {
            return;
        }

        List<Note> results = new ArrayList<>();
        List<Future<Response<NotesChildrenResponse[]>>> futures = new ArrayList<>();

        // 各 NextID 毎に検索
        for (String nextId : nextIds) {
            futures.add(pool.submit(() ->
                    misskey.notes().children(
                            NotesChildrenRequest.builder()
                                    .noteId(nextId)
                                    .limit(100L)
                                    .build())));
        }

        // 各リクエストの結果を統合
        for (Future<Response<NotesChildrenResponse[]>> future : futures) {

            results.addAll(Stream.of(future.get())
                    .map(Response::get).flatMap(Stream::of)
                    .filter(e -> notes.stream().noneMatch(n -> n.getId().equals(e.getId())))
                    .collect(toList()));
        }

        List<String> ids = results.stream()
                .filter(this::hasMoreReactionPossibility)
                .map(Note::getId).collect(toList());

        notes.addAll(results);
        getMoreReplies(misskey, pool, notes, ids);
    }

    // このノートには更にリアクションが付く可能性があるかどうか？
    private boolean hasMoreReactionPossibility(Note note) {
        return (note.getRepliesCount() > 0) || (note.getRenoteCount() > 0);
    }

    // ============================================================== //
    // Channel (List) API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Channel> getChannels(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            if (id != null) {
                User me = getUserMeWithCache();
                if (!me.getId().equals(id.getId())) {
                    throw new NotSupportedException(
                            "Sorry, authenticated user only.");
                }
            }

            // リスト一覧はページングには非対応
            Response<UsersListsListResponse[]> response =
                    misskey.lists().list(UsersListsListRequest.builder().build());

            return MisskeyMapper.channels(response.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getChannelTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            NotesUserListTimelineRequest.NotesUserListTimelineRequestBuilder builder =
                    NotesUserListTimelineRequest.builder();

            setPaging(builder, paging);
            builder.listId((String) id.getId());

            Response<NotesUserListTimelineResponse[]> response =
                    misskey.notes().userListTimeline(builder.build());

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getChannelUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            Response<UsersListsShowResponse> list =
                    misskey.lists().show(
                            UsersListsShowRequest.builder()
                                    .listId((String) id.getId())
                                    .build());

            Response<UsersShowResponse[]> users =
                    misskey.users().show(
                            UsersShowMultipleRequest.builder()
                                    .userIds(list.get().getUserIds())
                                    .build());

            return MisskeyMapper.users(
                    Stream.of(users.get()).collect(toList()),
                    misskey.getHost(), service, null);
        });
    }

    // ============================================================== //
    // Message API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Thread> getMessageThread(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            ExecutorService pool = Executors.newCachedThreadPool();

            Future<Response<MessagingHistoryResponse[]>> groupsFuture =
                    pool.submit(() -> misskey.messages().history(
                            MessagingHistoryRequest.builder()
                                    .limit(100L)
                                    .group(true)
                                    .build()));

            Future<Response<MessagingHistoryResponse[]>> messagesFuture =
                    pool.submit(() -> misskey.messages().history(
                            MessagingHistoryRequest.builder()
                                    .limit(100L)
                                    .group(false)
                                    .build()));

            Response<MessagingHistoryResponse[]> groups = groupsFuture.get();
            Response<MessagingHistoryResponse[]> messages = messagesFuture.get();


            // ユーザーの一覧を取得
            Map<String, User> userMap = new HashMap<>();
            List<String> userIds = Stream.of(groups.get())
                    .flatMap(e -> e.getGroup().getUserIds().stream())
                    .distinct().collect(toList());

            CollectionUtil.partitionList(userIds, 100).forEach((ids) -> {
                Response<UsersShowResponse[]> users =
                        misskey.users().show(
                                UsersShowMultipleRequest.builder()
                                        .userIds(ids)
                                        .build());

                for (misskey4j.entity.User user : users.get()) {
                    User model = MisskeyMapper.user(user, misskey.getHost(), service);
                    userMap.put(user.getId(), model);
                }
            });


            List<Thread> threads = new ArrayList<>();
            User me = getUserMeWithCache();

            for (Message group : groups.get()) {
                threads.add(MisskeyMapper.thread(group,
                        misskey.getHost(), me, userMap, service));
            }
            for (Message message : messages.get()) {
                threads.add(MisskeyMapper.thread(message,
                        misskey.getHost(), me, userMap, service));
            }

            paging.setHasPast(false);
            paging.setHasNext(false);

            Pageable<Thread> results = new Pageable<>();
            results.setEntities(threads);
            results.setPaging(paging);
            return results;

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMessageTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            boolean isGroup = (id instanceof MisskeyThread)
                    && ((MisskeyThread) id).isGroup();

            MessagingMessagesRequest.MessagingMessagesRequestBuilder builder =
                    MessagingMessagesRequest.builder();
            setPaging(builder, paging);

            builder.markAsRead(true);
            builder.userId(isGroup ? null : (String) id.getId());
            builder.groupId(isGroup ? (String) id.getId() : null);

            Response<MessagingMessagesResponse[]> response =
                    misskey.messages().messages(builder.build());

            return MisskeyMapper.messages(
                    response.get(), misskey.getHost(),
                    service, getReactionCandidates(), paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postMessage(CommentForm req) {
        if (!req.isMessage()) {
            postComment(req);
            return;
        }

        proceed(() -> {
            Misskey misskey = auth.getAccessor();

            Boolean isGroup = null;
            String targetId = null;

            if (req.getReplyId() instanceof String) {
                targetId = (String) req.getReplyId();

                // パラメータからグループか取得
                isGroup = req.getParams()
                        .get(MisskeyFormKey.MESSAGE_TYPE)
                        .equals(MisskeyFormKey.MESSAGE_TYPE_GROUP);
            }

            if (req.getReplyId() instanceof MisskeyThread) {
                targetId = (String) ((Thread) req.getReplyId()).getId();
                isGroup = ((MisskeyThread) req.getReplyId()).isGroup();
            }

            // 必須パラメータが発見できなかった場合
            if (isGroup == null || targetId == null) {
                throw new SocialHubException("Cannot found [targetId] or [isGroup] params.");
            }

            MessagingMessagesCreateRequest.MessagingMessagesCreateRequestBuilder builder =
                    MessagingMessagesCreateRequest.builder();

            // 画像の処理
            if ((req.getImages() != null) && !req.getImages().isEmpty()) {

                // ファイルは一つまで添付可能
                if (req.getImages().size() > 1) {
                    throw new SocialHubException("Only support one file to send message in Misskey.");
                }

                // ファイルのアップロードを実行
                MediaForm media = req.getImages().get(0);
                InputStream input = new ByteArrayInputStream(media.getData());
                Response<FilesCreateResponse> response = misskey.files()
                        .create(FilesCreateRequest.builder()
                                .isSensitive(req.isSensitive())
                                .name(media.getName())
                                .stream(input)
                                .force(true)
                                .build());

                builder.fileId(response.get().getId());
            }

            builder.userId(isGroup ? null : targetId);
            builder.groupId(isGroup ? targetId : null);

            builder.text(req.getText());
            misskey.messages().messagesCreate(builder.build());
        });
    }

    // ============================================================== //
    // Poll
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public void votePoll(Identify id, List<Integer> choices) {

        // MisskeyPoll 以外のオブジェクトは例外
        if (!(id instanceof MisskeyPoll)) {
            throw new SocialHubException("Not support default identify object in Misskey.");
        }

        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            for (Integer choice : choices) {
                misskey.polls().pollsVote(
                        PollsVoteRequest.builder()
                                .noteId(((MisskeyPoll) id).getNoteId())
                                .choice(choice)
                                .build());
            }
        });
    }

    // ============================================================== //
    // Other
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trend> getTrends(Integer limit) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();

            Response<HashtagsTrendResponse[]> response = misskey
                    .hashtags().trend(HashtagsTrendRequest.builder().build());

            return Stream.of(response.get())
                    .map(MisskeyMapper::trend)
                    .collect(toList());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<net.socialhub.model.service.Notification> getNotification(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            ExecutorService pool = Executors.newCachedThreadPool();

            Future<List<ReactionCandidate>> reactionFuture =
                    pool.submit(this::getReactionCandidates);

            INotificationsRequest.INotificationsRequestBuilder builder =
                    INotificationsRequest.builder();
            setPaging(builder, paging);

            builder.markAsRead(true);
            builder.includeTypes(Arrays.asList(
                    NotificationType.FOLLOW.code(),
                    NotificationType.REACTION.code(),
                    NotificationType.RENOTE.code()));

            Future<Response<INotificationsResponse[]>> responseFuture =
                    pool.submit(() -> misskey.accounts()
                            .iNotifications(builder.build()));

            List<ReactionCandidate> reaction = reactionFuture.get();
            Response<INotificationsResponse[]> response = responseFuture.get();

            return MisskeyMapper.notifications(response.get(),
                    reaction, misskey.getHost(), service, paging);
        });
    }

    // ============================================================== //
    // Stream
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setHomeTimeLineStream(EventCallback callback) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            MisskeyStream stream = misskey.stream();

            MisskeyCommentsListener commentsListener =
                    new MisskeyCommentsListener(callback,
                            service, misskey.getHost());
            MisskeyConnectionListener connectionListener =
                    new MisskeyConnectionListener(callback, () ->
                            stream.homeTimeLine(commentsListener));

            stream.setOpenedCallback(connectionListener);
            stream.setClosedCallback(connectionListener);

            return new net.socialhub.model.service.addition.misskey.MisskeyStream(stream);

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setNotificationStream(EventCallback callback) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            MisskeyStream stream = misskey.stream();

            MisskeyNotificationListener notificationListener =
                    new MisskeyNotificationListener(
                            callback, getReactionCandidates(),
                            service, misskey.getHost(),
                            getUserMeWithCache());

            MisskeyConnectionListener connectionListener =
                    new MisskeyConnectionListener(callback, () ->
                            stream.main(notificationListener));

            stream.setOpenedCallback(connectionListener);
            stream.setClosedCallback(connectionListener);
            stream.setErrorCallback(connectionListener);

            return new net.socialhub.model.service.addition.misskey.MisskeyStream(stream);
        });
    }

    // ============================================================== //
    // Another TimeLines
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getLocalTimeLine(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            NotesLocalTimelineRequest.NotesLocalTimelineRequestBuilder builder =
                    NotesLocalTimelineRequest.builder();

            setPaging(builder, paging);
            Response<NotesLocalTimelineResponse[]> response =
                    misskey.notes().localTimeline(builder.build());

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getFederationTimeLine(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            NotesGlobalTimelineRequest.NotesGlobalTimelineRequestBuilder builder =
                    NotesGlobalTimelineRequest.builder();

            setPaging(builder, paging);
            Response<NotesGlobalTimelineResponse[]> response =
                    misskey.notes().globalTimeline(builder.build());

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * Get Featured Timeline
     */
    public Pageable<Comment> getFeaturedTimeLine(Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            NotesFeaturedRequest.NotesFeaturedRequestBuilder builder =
                    NotesFeaturedRequest.builder();

            if (paging != null) {
                if (paging.getCount() != null) {
                    builder.limit(paging.getCount());
                    if (paging.getCount() > 100) {
                        builder.limit(100L);
                    }
                }
                if (paging instanceof OffsetPaging) {
                    OffsetPaging pg = (OffsetPaging) paging;
                    if (pg.getOffset() != null) {
                        builder.offset(pg.getOffset());
                    }
                }
            }

            Response<NotesFeaturedResponse[]> response =
                    misskey.notes().featured(builder.build());

            Pageable<Comment> results = MisskeyMapper.timeLine(
                    response.get(), misskey.getHost(), service, paging);

            results.setPaging(OffsetPaging.fromPaging(paging));
            results.getPaging().setHasNew(false);
            return results;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setLocalLineStream(EventCallback callback) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            MisskeyStream stream = misskey.stream();

            MisskeyCommentsListener commentsListener =
                    new MisskeyCommentsListener(callback,
                            service, misskey.getHost());
            MisskeyConnectionListener connectionListener =
                    new MisskeyConnectionListener(callback, () ->
                            stream.localTimeline(commentsListener));

            stream.setOpenedCallback(connectionListener);
            stream.setClosedCallback(connectionListener);
            stream.setErrorCallback(connectionListener);

            return new net.socialhub.model.service.addition.misskey.MisskeyStream(stream);

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setFederationLineStream(EventCallback callback) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();
            MisskeyStream stream = misskey.stream();

            MisskeyCommentsListener commentsListener =
                    new MisskeyCommentsListener(callback,
                            service, misskey.getHost());
            MisskeyConnectionListener connectionListener =
                    new MisskeyConnectionListener(callback, () ->
                            stream.globalTimeline(commentsListener));

            stream.setOpenedCallback(connectionListener);
            stream.setClosedCallback(connectionListener);
            stream.setErrorCallback(connectionListener);

            return new net.socialhub.model.service.addition.misskey.MisskeyStream(stream);
        });
    }

    /**
     * Register ServiceWorker endpoint.
     * サービスワーカーのエンドポイントを設定
     */
    public void registerSubscription(
            String endpoint, String publicKey, String authSecret) {

        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            misskey.other().serviceWorkerRegister(
                    ServiceWorkerRegisterRequest.builder()
                            .endpoint(endpoint)
                            .publickey(publicKey)
                            .auth(authSecret)
                            .build());
        });
    }

    // ============================================================== //
    // Request
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestAction request() {
        return new MisskeyRequest(getAccount());
    }

    // ============================================================== //
    // paging
    // ============================================================== //

    private void setPaging(PagingBuilder<?> builder, Paging paging) {

        if (paging != null) {
            if (paging.getCount() != null) {
                builder.limit(paging.getCount());
                if (paging.getCount() > 100) {
                    builder.limit(100L);
                }
            }
            if (paging instanceof MisskeyPaging) {
                MisskeyPaging mp = (MisskeyPaging) paging;
                if (mp.getUntilId() != null) {
                    builder.untilId(mp.getUntilId());
                }
                if (mp.getSinceId() != null) {
                    builder.sinceId(mp.getSinceId());
                }
            }
        }
    }

    // ============================================================== //
    // Classes
    // ============================================================== //

    // コメントに対してのコールバック設定
    static class MisskeyCommentsListener implements
            NoteCallback {

        private EventCallback listener;
        private Service service;
        private String host;

        MisskeyCommentsListener(
                EventCallback listener,
                Service service,
                String host) {
            this.listener = listener;
            this.service = service;
            this.host = host;
        }

        @Override
        public void onNoteUpdate(Note note) {
            if (listener instanceof UpdateCommentCallback) {
                Comment comment = MisskeyMapper.comment(note, host, service);
                ((UpdateCommentCallback) listener).onUpdate(new CommentEvent(comment));
            }
        }
    }

    // 通信に対してのコールバック設定
    static class MisskeyConnectionListener implements
            OpenedCallback,
            ClosedCallback,
            ErrorCallback {

        private final EventCallback listener;
        private final Runnable runnable;

        MisskeyConnectionListener(
                EventCallback listener,
                Runnable runnable) {
            this.listener = listener;
            this.runnable = runnable;
        }

        @Override
        public void onOpened() {
            if (listener instanceof ConnectCallback) {
                ((ConnectCallback) listener).onConnect();
            }
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        public void onClosed(boolean remote) {
            if (listener instanceof DisconnectCallback) {
                ((DisconnectCallback) listener).onDisconnect();
            }
        }

        @Override
        public void onError(Exception e) {
            logger.debug("WebSocket Error: ", e);
        }
    }

    // コメントに対してのコールバック設定
    static class MisskeyNotificationListener implements
            FollowedCallback,
            RenoteCallback,
            ReplayCallback,
            MentionCallback,
            NotificationCallback {

        private final EventCallback listener;
        private final List<ReactionCandidate> reactions;
        private final Service service;
        private final String host;
        private final User me;

        MisskeyNotificationListener(
                EventCallback listener,
                List<ReactionCandidate> reactions,
                Service service,
                String host,
                User me) {
            this.listener = listener;
            this.reactions = reactions;
            this.service = service;
            this.host = host;
            this.me = me;
        }

        @Override
        public void onFollowed(misskey4j.entity.User user) {
            if (listener instanceof FollowUserCallback) {
                User model = MisskeyMapper.user(user, host, service);
                ((FollowUserCallback) listener).onFollow(new UserEvent(model));
            }
        }

        @Override
        public void onMention(Note note) {
            // Mention と Reply が重複することを防止
            // -> 自分に対する Reply の場合は Reply に移譲
            if (note.getReply().getUser().getId().equals(me.getId())) {
                return;
            }

            if (listener instanceof MentionCommentCallback) {
                Comment model = MisskeyMapper.comment(note, host, service);
                ((MentionCommentCallback) listener).onMention(new CommentEvent(model));
            }
        }

        @Override
        public void onReply(Note note) {
            if (listener instanceof MentionCommentCallback) {
                Comment model = MisskeyMapper.comment(note, host, service);
                ((MentionCommentCallback) listener).onMention(new CommentEvent(model));
            }
        }

        @Override
        public void onNotification(Notification notification) {

            // Reaction or Renote の場合のみ反応
            // (それ以外の場合は他でカバー済)
            if (notification.getType().equals("reaction") ||
                    notification.getType().equals("renote")) {

                if (listener instanceof NotificationCommentCallback) {
                    net.socialhub.model.service.Notification model =
                            MisskeyMapper.notification(notification, reactions, host, service);
                    ((NotificationCommentCallback) listener).onNotification(new NotificationEvent(model));
                }
            }
        }

        @Override
        public void onRenote(Note note) {
            // Renote は Notification にて反応
        }
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, Exception> runner) {
        try {
            return runner.proceed();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<Exception> runner) {
        try {
            runner.proceed();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static void handleException(Exception e) {
        SocialHubException se = new SocialHubException(e);

        if (e instanceof MisskeyException) {
            MisskeyException me = (MisskeyException) e;

            // エラーメッセージが設定されているエラーである場合
            if (me.getError() != null && me.getError().getError() != null) {
                ErrorDetail detail = me.getError().getError();
                se.setError(new Error(detail.getMessage()));
            }
        }

        throw se;
    }

    //region // Getter&Setter
    MisskeyAction(Account account, ServiceAuth<Misskey> auth) {
        this.account(account);
        this.auth(auth);
    }

    MisskeyAction auth(ServiceAuth<Misskey> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
