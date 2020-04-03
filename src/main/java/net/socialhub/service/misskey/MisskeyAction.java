package net.socialhub.service.misskey;

import misskey4j.Misskey;
import misskey4j.api.request.blocks.BlocksCreateRequest;
import misskey4j.api.request.blocks.BlocksDeleteRequest;
import misskey4j.api.request.files.FilesCreateRequest;
import misskey4j.api.request.following.FollowingCreateRequest;
import misskey4j.api.request.following.FollowingDeleteRequest;
import misskey4j.api.request.i.INotificationsRequest;
import misskey4j.api.request.i.IRequest;
import misskey4j.api.request.mutes.MutesCreateRequest;
import misskey4j.api.request.mutes.MutesDeleteRequest;
import misskey4j.api.request.notes.NotesCreateRequest;
import misskey4j.api.request.notes.NotesSearchRequest;
import misskey4j.api.request.notes.NotesTimelineRequest;
import misskey4j.api.request.notes.UsersNotesRequest;
import misskey4j.api.request.protocol.PagingBuilder;
import misskey4j.api.request.users.UsersFollowersRequest;
import misskey4j.api.request.users.UsersFollowingsRequest;
import misskey4j.api.request.users.UsersRelationRequest;
import misskey4j.api.request.users.UsersShowRequest;
import misskey4j.api.response.files.FilesCreateResponse;
import misskey4j.api.response.i.INotificationsResponse;
import misskey4j.api.response.i.IResponse;
import misskey4j.api.response.notes.NotesSearchResponse;
import misskey4j.api.response.notes.NotesTimelineResponse;
import misskey4j.api.response.notes.UsersNotesResponse;
import misskey4j.api.response.users.UsersFollowersResponse;
import misskey4j.api.response.users.UsersFollowingsResponse;
import misskey4j.api.response.users.UsersRelationResponse;
import misskey4j.api.response.users.UsersShowResponse;
import misskey4j.entity.Follow;
import misskey4j.entity.Note;
import misskey4j.entity.Notification;
import misskey4j.entity.contant.NotificationType;
import misskey4j.entity.share.Response;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.misskey.MisskeyPaging;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.utils.HandlingUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class MisskeyAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(MisskeyAction.class);

    private ServiceAuth<Misskey> auth;

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

            Response<UsersShowResponse[]> response =
                    misskey.users().show(UsersShowRequest.builder()
                            .userId((String) id.getId())
                            .build());

            if (response.get().length != 1) {
                throw new SocialHubException("Unexpected response in Misskey's getUser.");
            }
            return MisskeyMapper.user(response.get()[0],
                    misskey.getHost(), service);
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
        throw new NotImplimentedException();
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

            return MisskeyMapper.timeLine(response.get(),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
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

            return MisskeyMapper.timeLine(
                    Stream.of(response.get())
                            .map(Notification::getNote)
                            .filter(Objects::nonNull)
                            .toArray(Note[]::new),
                    misskey.getHost(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
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
        throw new NotSupportedException("Not supported on Misskey.");
    }

    /**
     * {@inheritDoc}
     */
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Misskey misskey = auth.getAccessor();
            Service service = getAccount().getService();

            UsersNotesRequest.UsersNotesRequestBuilder builder =
                    UsersNotesRequest.builder();

            builder.userId((String) id.getId());
            builder.excludeNsfw(true);
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
        proceed(() -> {
            Misskey misskey = auth.getAccessor();
            ExecutorService pool = Executors.newCachedThreadPool();

            NotesCreateRequest.NotesCreateRequestBuilder builder =
                    NotesCreateRequest.builder();

            // 文言の追加
            builder.text(req.getText());

            // 返信の処理
            if (req.getTargetId() != null) {
                builder.replyId((String) req.getTargetId());
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

            misskey.notes().create(builder.build());
        });
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
        throw new SocialHubException(e);
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
