package net.socialhub.service.bluesky.action;

import bsky4j.BlueskyException;
import bsky4j.BlueskyTypes;
import bsky4j.api.entity.atproto.identity.IdentityResolveHandleRequest;
import bsky4j.api.entity.atproto.identity.IdentityResolveHandleResponse;
import bsky4j.api.entity.atproto.repo.RepoListRecordsRequest;
import bsky4j.api.entity.atproto.repo.RepoListRecordsResponse;
import bsky4j.api.entity.atproto.repo.RepoUploadBlobRequest;
import bsky4j.api.entity.atproto.repo.RepoUploadBlobResponse;
import bsky4j.api.entity.atproto.server.ServerCreateSessionRequest;
import bsky4j.api.entity.atproto.server.ServerCreateSessionResponse;
import bsky4j.api.entity.bsky.actor.ActorGetProfileRequest;
import bsky4j.api.entity.bsky.actor.ActorGetProfileResponse;
import bsky4j.api.entity.bsky.actor.ActorSearchActorsRequest;
import bsky4j.api.entity.bsky.actor.ActorSearchActorsResponse;
import bsky4j.api.entity.bsky.feed.FeedDeleteLikeRequest;
import bsky4j.api.entity.bsky.feed.FeedDeletePostRequest;
import bsky4j.api.entity.bsky.feed.FeedDeleteRepostRequest;
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedRequest;
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedResponse;
import bsky4j.api.entity.bsky.feed.FeedGetPostThreadRequest;
import bsky4j.api.entity.bsky.feed.FeedGetPostThreadResponse;
import bsky4j.api.entity.bsky.feed.FeedGetPostsRequest;
import bsky4j.api.entity.bsky.feed.FeedGetPostsResponse;
import bsky4j.api.entity.bsky.feed.FeedGetTimelineRequest;
import bsky4j.api.entity.bsky.feed.FeedGetTimelineResponse;
import bsky4j.api.entity.bsky.feed.FeedLikeRequest;
import bsky4j.api.entity.bsky.feed.FeedPostRequest;
import bsky4j.api.entity.bsky.feed.FeedRepostRequest;
import bsky4j.api.entity.bsky.graph.GraphBlockRequest;
import bsky4j.api.entity.bsky.graph.GraphDeleteBlockRequest;
import bsky4j.api.entity.bsky.graph.GraphDeleteFollowRequest;
import bsky4j.api.entity.bsky.graph.GraphFollowRequest;
import bsky4j.api.entity.bsky.graph.GraphGetFollowersRequest;
import bsky4j.api.entity.bsky.graph.GraphGetFollowersResponse;
import bsky4j.api.entity.bsky.graph.GraphGetFollowsRequest;
import bsky4j.api.entity.bsky.graph.GraphGetFollowsResponse;
import bsky4j.api.entity.bsky.graph.GraphMuteActorRequest;
import bsky4j.api.entity.bsky.graph.GraphUnmuteActorRequest;
import bsky4j.api.entity.bsky.notification.NotificationListNotificationsRequest;
import bsky4j.api.entity.bsky.notification.NotificationListNotificationsResponse;
import bsky4j.api.entity.bsky.undoc.UndocSearchFeedsRequest;
import bsky4j.api.entity.bsky.undoc.UndocSearchFeedsResponse;
import bsky4j.api.entity.share.Response;
import bsky4j.model.atproto.repo.RepoListRecordsRecord;
import bsky4j.model.atproto.repo.RepoStrongRef;
import bsky4j.model.bsky.actor.ActorDefsViewerState;
import bsky4j.model.bsky.embed.EmbedImages;
import bsky4j.model.bsky.embed.EmbedImagesImage;
import bsky4j.model.bsky.embed.EmbedUnion;
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost;
import bsky4j.model.bsky.feed.FeedDefsThreadUnion;
import bsky4j.model.bsky.feed.FeedDefsThreadViewPost;
import bsky4j.model.bsky.feed.FeedPost;
import bsky4j.model.bsky.notification.NotificationListNotificationsNotification;
import bsky4j.model.bsky.richtext.RichtextFacet;
import bsky4j.model.share.RecordUnion;
import bsky4j.util.facet.FacetList;
import bsky4j.util.facet.FacetRecord;
import bsky4j.util.facet.FacetType;
import bsky4j.util.facet.FacetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.socialhub.core.action.AccountActionImpl;
import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Context;
import net.socialhub.core.model.Error;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.Stream;
import net.socialhub.core.model.Trend;
import net.socialhub.core.model.User;
import net.socialhub.core.model.error.NotImplimentedException;
import net.socialhub.core.model.error.NotSupportedException;
import net.socialhub.core.model.error.SocialHubException;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.support.ReactionCandidate;
import net.socialhub.core.utils.MapperUtil;
import net.socialhub.logger.Logger;
import net.socialhub.service.bluesky.define.BlueskyReactionType;
import net.socialhub.service.bluesky.model.BlueskyComment;
import net.socialhub.service.bluesky.model.BlueskyPaging;
import net.socialhub.service.bluesky.model.BlueskyUser;
import net.socialhub.service.microblog.action.MicroBlogAccountAction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class BlueskyAction extends AccountActionImpl implements MicroBlogAccountAction {

    private static final Logger logger = Logger.getLogger(BlueskyAction.class);

    private BlueskyAuth auth;
    private String accessJwt;
    private Long expireAt;
    private String did;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor(getDid())
                                    .build());

            me = BlueskyMapper.user(response.get(), service);
            return me;
        });
    }

    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor((String) id.getId())
                                    .build());

            return BlueskyMapper.user(response.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     * ハンドルも DID も同じ位置で解釈される
     * https://staging.bsky.app/profile/uakihir0.com
     * https://staging.bsky.app/profile/did:plc:bwdof2anluuf5wmfy2upgulw
     */
    @Override
    public User getUser(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            String[] elements = url.split("/");
            String id = elements[elements.length - 1];

            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor(id)
                                    .build());

            return BlueskyMapper.user(response.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            auth.getAccessor().graph().follow(
                    GraphFollowRequest.builder()
                            .accessJwt(getAccessJwt())
                            .subject((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            String uri = getUserUri(id);

            if (uri != null) {
                auth.getAccessor().graph().deleteFollow(
                        GraphDeleteFollowRequest.builder()
                                .accessJwt(getAccessJwt())
                                .uri(uri)
                                .build());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            auth.getAccessor().graph().muteActor(
                    GraphMuteActorRequest.builder()
                            .accessJwt(getAccessJwt())
                            .actor((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            auth.getAccessor().graph().unmuteActor(
                    GraphUnmuteActorRequest.builder()
                            .accessJwt(getAccessJwt())
                            .actor((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            auth.getAccessor().graph().block(
                    GraphBlockRequest.builder()
                            .accessJwt(getAccessJwt())
                            .subject((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            String uri = getUserUri(id);

            if (uri != null) {
                auth.getAccessor().graph().deleteBlock(
                        GraphDeleteBlockRequest.builder()
                                .accessJwt(getAccessJwt())
                                .uri(uri)
                                .build());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {
            if (id instanceof BlueskyUser) {
                BlueskyUser user = (BlueskyUser) id;
                return BlueskyMapper.relationship(user);
            }

            BlueskyUser user = (BlueskyUser) getUser(id);
            return BlueskyMapper.relationship(user);
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
            Service service = getAccount().getService();
            Response<GraphGetFollowsResponse> follows =
                    auth.getAccessor().graph().getFollows(
                            GraphGetFollowsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor((String) id.getId())
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());

            return BlueskyMapper.users(
                    follows.get().getFollows(),
                    paging,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();
            Response<GraphGetFollowersResponse> follows =
                    auth.getAccessor().graph().getFollowers(
                            GraphGetFollowersRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor((String) id.getId())
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());

            return BlueskyMapper.users(
                    follows.get().getFollowers(),
                    paging,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> searchUsers(String query, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();
            Response<ActorSearchActorsResponse> response =
                    auth.getAccessor().actor().searchActors(
                            ActorSearchActorsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .term(query)
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());

            return BlueskyMapper.users(
                    response.get().getActors(),
                    paging,
                    service
            );
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
            Service service = getAccount().getService();
            Response<FeedGetTimelineResponse> response =
                    auth.getAccessor().feed().getTimeline(
                            FeedGetTimelineRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());

            return BlueskyMapper.timelineByFeeds(
                    response.get().getFeed(),
                    paging,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();
            Response<NotificationListNotificationsResponse> notifications =
                    auth.getAccessor().notification().listNotifications(
                            NotificationListNotificationsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());

            // TODO: ページング

            List<String> subjects = notifications
                    .get().getNotifications().stream()
                    .filter(n -> n.getRecord() != null)
                    .filter(n -> n.getRecord().getType().equals("replay"))
                    .map(NotificationListNotificationsNotification::getReasonSubject)
                    .collect(toList());

            Response<FeedGetPostsResponse> posts =
                    auth.getAccessor().feed().getPosts(
                            FeedGetPostsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .uris(subjects)
                                    .build());

            return BlueskyMapper.timelineByPosts(
                    posts.get().getPosts(),
                    null,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();
            Response<FeedGetAuthorFeedResponse> response =
                    auth.getAccessor().feed().getAuthorFeed(
                            FeedGetAuthorFeedRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor((String) id.getId())
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());

            return BlueskyMapper.timelineByFeeds(
                    response.get().getFeed(),
                    paging,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();

            // TODO: ページング対応

            Response<RepoListRecordsResponse> response =
                    auth.getAccessor().repo().listRecords(
                            RepoListRecordsRequest.builder()
                                    .repo((String) id.getId())
                                    .collection(BlueskyTypes.FeedLike)
                                    .rkeyStart("")
                                    .rkeyEnd("")
                                    .limit(limit(paging))
                                    .build());

            List<String> subjects = response.get().getRecords().stream()
                    .map(RepoListRecordsRecord::getUri)
                    .collect(toList());

            Response<FeedGetPostsResponse> posts =
                    auth.getAccessor().feed().getPosts(
                            FeedGetPostsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .uris(subjects)
                                    .build());

            return BlueskyMapper.timelineByPosts(
                    posts.get().getPosts(),
                    null,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {

        return proceed(() -> {
            String cursor = nextCursor(paging);
            Service service = getAccount().getService();
            List<FeedDefsFeedViewPost> feeds = new ArrayList<>();

            // 十分な数の投稿が取得できるまでリクエストを実行
            for (int i = 0; i < 10; i++) {
                Response<FeedGetAuthorFeedResponse> response =
                        auth.getAccessor().feed().getAuthorFeed(
                                FeedGetAuthorFeedRequest.builder()
                                        .accessJwt(getAccessJwt())
                                        .actor((String) id.getId())
                                        .limit(limit(paging))
                                        .cursor(cursor)
                                        .build());

                // 画像の投稿が含まれているものだけを抽出
                List<FeedDefsFeedViewPost> imagePosts =
                        response.get().getFeed().stream().filter(n -> {
                            RecordUnion union = n.getPost().getRecord();
                            if (union instanceof FeedPost) {

                                FeedPost post = (FeedPost) union;
                                if (post.getEmbed() != null) {

                                    EmbedUnion embed = post.getEmbed();
                                    if (embed instanceof EmbedImages) {
                                        EmbedImages images = (EmbedImages) embed;
                                        return images.getImages().size() > 0;
                                    }
                                }
                            }
                            return false;
                        }).collect(toList());

                feeds.addAll(imagePosts);
                cursor = response.get().getCursor();

                // 十分な数の画像を取得できた場合は終了
                if (feeds.size() >= limit(paging)) {
                    break;
                }
            }

            return BlueskyMapper.timelineByFeeds(
                    feeds,
                    paging,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();

            Response<List<UndocSearchFeedsResponse>> response =
                    auth.getAccessor().undoc().searchFeeds(
                            UndocSearchFeedsRequest.builder()
                                    .q(query)
                                    .build());

            List<String> uris = response.get().stream()
                    // tid と User did から at-uri を復元して投稿を取得
                    .map(i -> "at://" + i.getUser().getDid() + "/" + i.getTid())
                    .collect(toList());

            Response<FeedGetPostsResponse> posts =
                    auth.getAccessor().feed().getPosts(
                            FeedGetPostsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .uris(uris)
                                    .build());

            return BlueskyMapper.timelineByPosts(
                    posts.get().getPosts(),
                    null,
                    service
            );
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
            String accessJwt = getAccessJwt();
            List<Future<EmbedImagesImage>> imageFutures = new ArrayList<>();

            if (req.getImages() != null && !req.getImages().isEmpty()) {
                ExecutorService pool = Executors.newCachedThreadPool();

                // 画像を並列でアップロード実行
                req.getImages().forEach(img -> {
                    imageFutures.add(pool.submit(() -> {
                        InputStream input = new ByteArrayInputStream(img.getData());
                        Response<RepoUploadBlobResponse> response =
                                auth.getAccessor().repo().uploadBlob(
                                        RepoUploadBlobRequest.fromStreamBuilder()
                                                .accessJwt(accessJwt)
                                                .stream(input)
                                                .name(img.getName())
                                                .build()
                                );

                        EmbedImagesImage image = new EmbedImagesImage();
                        image.setImage(response.get().getBlob());
                        image.setAlt("");
                        return image;
                    }));
                });
            }

            try {
                // Facet を切り出して設定
                FacetList list = FacetUtil.extractFacets(req.getText());
                List<RichtextFacet> facets = new ArrayList<>();

                if (!list.getRecords().isEmpty()) {
                    List<String> handles = list.getRecords().stream()
                            .filter(i -> i.getType() == FacetType.Mention)
                            .map(FacetRecord::getContentText)
                            .collect(Collectors.toList());

                    // Handle と Did の紐付けを作成
                    Map<String, String> handleDidMap = new HashMap<>();
                    for (String handle : handles) {
                        Response<IdentityResolveHandleResponse> response =
                                auth.getAccessor().identity().resolveHandle(
                                        IdentityResolveHandleRequest.builder()
                                                .handle(handle.substring(1))
                                                .build());

                        handleDidMap.put(handle, response.get().getDid());
                    }

                    // Facets のリストをここで生成
                    facets = list.getRichTextFacets(handleDidMap);
                }

                // 投稿リクエスト
                FeedPostRequest.FeedPostRequestBuilder builder =
                        FeedPostRequest.builder()
                                .accessJwt(getAccessJwt())
                                .text(list.getDisplayText())
                                .facets(facets);

                if (!imageFutures.isEmpty()) {
                    List<EmbedImagesImage> images = new ArrayList<>();
                    for (Future<EmbedImagesImage> imageFuture : imageFutures) {
                        images.add(imageFuture.get());
                    }

                    EmbedImages imagesMain = new EmbedImages();
                    imagesMain.setImages(images);
                    builder.embed(imagesMain);
                }

                auth.getAccessor().feed()
                        .post(builder.build());

            } catch (Exception e) {
                handleException(e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment getComment(Identify id) {

        return proceed(() -> {
            Service service = getAccount().getService();

            Response<FeedGetPostsResponse> posts =
                    auth.getAccessor().feed().getPosts(
                            FeedGetPostsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .uris(singletonList((String) id.getId()))
                                    .build());

            return BlueskyMapper.comment(
                    posts.get().getPosts().get(0),
                    service
            );
        });
    }

    private Comment getCommentWithCheck(Identify id) {
        if (id instanceof BlueskyComment) {
            return (BlueskyComment) id;
        } else {
            return getComment(id);
        }
    }

    /**
     * {@inheritDoc}
     * https://bsky.app/profile/uakihir0.com/post/3jw2ydtuktc2j
     */
    @Override
    public Comment getComment(String url) {

        return proceed(() -> {
            try {
                Service service = getAccount().getService();

                String[] elements = url
                        .split("//")[1]
                        .split("/");
                String handle = elements[2];
                String rkey = elements[4];

                Response<IdentityResolveHandleResponse> response =
                        auth.getAccessor().identity().resolveHandle(
                                IdentityResolveHandleRequest.builder()
                                        .handle(handle)
                                        .build());

                String did = response.get().getDid();
                String uri = "at://" + did + "/app.bsky.feed.post/" + rkey;

                Identify identify = new Identify(service, uri);
                return getComment(identify);

            } catch (Exception e) {
                handleException(e);
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeComment(Identify id) {

        proceed(() -> {
            BlueskyComment comment = (BlueskyComment) getCommentWithCheck(id);
            RepoStrongRef ref = new RepoStrongRef(
                    (String) comment.getId(),
                    comment.getCid()
            );

            auth.getAccessor().feed().like(
                    FeedLikeRequest.builder()
                            .accessJwt(getAccessJwt())
                            .subject(ref)
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlikeComment(Identify id) {

        proceed(() -> {
            BlueskyComment comment = (BlueskyComment) getCommentWithCheck(id);

            auth.getAccessor().feed().deleteLike(
                    FeedDeleteLikeRequest.builder()
                            .accessJwt(getAccessJwt())
                            .uri(comment.getLikeRecordUri())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shareComment(Identify id) {

        proceed(() -> {
            BlueskyComment comment = (BlueskyComment) getCommentWithCheck(id);
            RepoStrongRef ref = new RepoStrongRef(
                    (String) comment.getId(),
                    comment.getCid());

            auth.getAccessor().feed().repost(
                    FeedRepostRequest.builder()
                            .accessJwt(getAccessJwt())
                            .subject(ref)
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshareComment(Identify id) {

        proceed(() -> {
            BlueskyComment comment = (BlueskyComment) getCommentWithCheck(id);

            auth.getAccessor().feed().deleteRepost(
                    FeedDeleteRepostRequest.builder()
                            .accessJwt(getAccessJwt())
                            .uri(comment.getRepostRecordUri())
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

            if (BlueskyReactionType.Like.getCode().contains(type)) {
                likeComment(id);
                return;
            }
            if (BlueskyReactionType.Repost.getCode().contains(type)) {
                retweetComment(id);
                return;
            }
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

            if (BlueskyReactionType.Like.getCode().contains(type)) {
                unlikeComment(id);
                return;
            }
            if (BlueskyReactionType.Repost.getCode().contains(type)) {
                unshareComment(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComment(Identify id) {

        proceed(() -> {
            auth.getAccessor().feed().deletePost(
                    FeedDeletePostRequest.builder()
                            .accessJwt(getAccessJwt())
                            .uri((String) id.getId())
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return BlueskyMapper.reactionCandidates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext(Identify id) {

        return proceed(() -> {
            Service service = getAccount().getService();
            List<Comment> ancestors = new ArrayList<>();
            List<Comment> descendants = new ArrayList<>();

            Response<FeedGetPostThreadResponse> response =
                    auth.getAccessor().feed().getPostThread(
                            FeedGetPostThreadRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .uri((String) id.getId())
                                    .build());

            // 再帰的に確認を行い投稿リストを構築
            FeedDefsThreadUnion union = response.get().getThread();
            if (union instanceof FeedDefsThreadViewPost) {
                FeedDefsThreadViewPost post = (FeedDefsThreadViewPost) union;
                subGetCommentContext(post, ancestors, descendants, service);
            }

            Context context = new Context();
            context.setAncestors(ancestors);
            context.setDescendants(descendants);
            MapperUtil.sortContext(context);
            return context;
        });
    }

    private void subGetCommentContext(
            FeedDefsThreadViewPost post,
            List<Comment> ancestors,
            List<Comment> descendants,
            Service service
    ) {
        if (post.getParent() instanceof FeedDefsThreadViewPost) {
            FeedDefsThreadViewPost parent = (FeedDefsThreadViewPost) post.getParent();
            ancestors.add(BlueskyMapper.comment(parent.getPost(), service));
            subGetCommentContext(parent, ancestors, descendants, service);
        }

        if (post.getReplies() != null && !post.getReplies().isEmpty()) {
            for (FeedDefsThreadUnion reply : post.getReplies()) {
                if (reply instanceof FeedDefsThreadViewPost) {
                    FeedDefsThreadViewPost child = (FeedDefsThreadViewPost) reply;
                    descendants.add(BlueskyMapper.comment(child.getPost(), service));
                    subGetCommentContext(child, ancestors, descendants, service);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trend> getTrends(Integer limit) {
        throw new NotImplimentedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Notification> getNotification(Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();

            Response<NotificationListNotificationsResponse> response =
                    auth.getAccessor().notification().listNotifications(
                            NotificationListNotificationsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .cursor(nextCursor(paging))
                                    .limit(limit(paging))
                                    .build());


            List<String> subjects = response.get().getNotifications()
                    .stream().filter(n -> n.getRecord() instanceof FeedPost)
                    .map(NotificationListNotificationsNotification::getReasonSubject)
                    .collect(toList());


            Response<FeedGetPostsResponse> posts =
                    auth.getAccessor().feed().getPosts(
                            FeedGetPostsRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .uris(subjects)
                                    .build());

            return BlueskyMapper.notifications(
                    response.get().getNotifications(),
                    posts.get().getPosts(),
                    paging,
                    service
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void votePoll(Identify id, List<Integer> choices) {
        throw new NotImplimentedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getLocalTimeLine(Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getFederationTimeLine(Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream setLocalLineStream(EventCallback callback) {
        throw new NotImplimentedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream setFederationLineStream(EventCallback callback) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    /**
     * Get User Uri from Identify
     * ID からユーザー URI を取得
     */
    private String getUserUri(Identify id) {
        String uri = null;

        // ユーザーオブジェクトから取得
        if (id instanceof BlueskyUser) {
            BlueskyUser user = (BlueskyUser) id;
            uri = user.getFollowRecordUri();
        }

        // DID から取得
        if (uri == null) {
            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor((String) id.getId())
                                    .build());

            // ユーザー情報にフォローしているかどうかが確認できる
            ActorDefsViewerState state = response.get().getViewer();
            if (state != null && state.getFollowing() != null) {
                uri = state.getFollowing();
            }
        }

        return uri;
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    private String nextCursor(Paging paging) {
        if (paging instanceof BlueskyPaging) {
            return ((BlueskyPaging) paging).getNextCursor();
        }
        return null;
    }

    private Integer limit(Paging paging) {
        if (paging != null) {
            return paging.getCount().intValue();
        }
        return null;
    }

    // ============================================================== //
    // Session
    // ============================================================== //

    private void createSession() {
        Response<ServerCreateSessionResponse> response =
                auth.getAccessor().server().createSession(
                        ServerCreateSessionRequest.builder()
                                .identifier(auth.getIdentifier())
                                .password(auth.getPassword())
                                .build());

        this.did = response.get().getDid();
        this.accessJwt = response.get().getAccessJwt();
        String encoded = this.accessJwt.split("\\.")[1];
        String decoded = new String(Base64.getDecoder().decode(encoded));

        Map<String, Object> json = new Gson().fromJson(decoded,
                new TypeToken<Map<String, Object>>() {
                }.getType());

        Object expire = json.get("exp");
        if (expire instanceof Double) {
            this.expireAt = ((Double) expire).longValue();
        }
    }

    private String getAccessJwt() {

        // 初回アクセスの場合
        if (accessJwt == null) {
            createSession();
            return accessJwt;
        }

        // 有効期限が切れている場合
        long now = System.currentTimeMillis() / 1000;
        if (now > (expireAt + 30)) {
            createSession();
            return accessJwt;
        }

        return accessJwt;
    }

    private String getDid() {
        if (did == null) {
            createSession();
        }
        return did;
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

        if (e instanceof BlueskyException) {
            BlueskyException be = (BlueskyException) e;
            if (be.getErrorMessage() != null) {
                se.setError(new Error(be.getErrorMessage()));
            }
        }

        throw se;
    }

    // region
    BlueskyAction(Account account, BlueskyAuth auth) {
        this.account(account);
        this.auth(auth);
    }

    BlueskyAction auth(BlueskyAuth auth) {
        this.auth = auth;
        return this;
    }
    // endregion
}
