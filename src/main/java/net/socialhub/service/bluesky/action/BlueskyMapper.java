package net.socialhub.service.bluesky.action;

import bsky4j.model.bsky.actor.ActorDefsProfileView;
import bsky4j.model.bsky.actor.ActorDefsProfileViewBasic;
import bsky4j.model.bsky.actor.ActorDefsProfileViewDetailed;
import bsky4j.model.bsky.actor.ActorDefsViewerState;
import bsky4j.model.bsky.embed.EmbedImages;
import bsky4j.model.bsky.embed.EmbedImagesImage;
import bsky4j.model.bsky.embed.EmbedImagesView;
import bsky4j.model.bsky.embed.EmbedImagesViewImage;
import bsky4j.model.bsky.embed.EmbedUnion;
import bsky4j.model.bsky.embed.EmbedViewUnion;
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost;
import bsky4j.model.bsky.feed.FeedDefsPostView;
import bsky4j.model.bsky.feed.FeedDefsViewerState;
import bsky4j.model.bsky.feed.FeedPost;
import bsky4j.model.bsky.notification.NotificationListNotificationsNotification;
import bsky4j.model.bsky.richtext.RichtextFacet;
import bsky4j.model.bsky.richtext.RichtextFacetByteSlice;
import bsky4j.model.bsky.richtext.RichtextFacetFeatureUnion;
import bsky4j.model.bsky.richtext.RichtextFacetLink;
import bsky4j.model.bsky.richtext.RichtextFacetMention;
import bsky4j.model.share.RecordUnion;
import net.socialhub.core.define.MediaType;
import net.socialhub.core.define.emoji.EmojiCategoryType;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.User;
import net.socialhub.core.model.common.AttributedElement;
import net.socialhub.core.model.common.AttributedItem;
import net.socialhub.core.model.common.AttributedKind;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.support.ReactionCandidate;
import net.socialhub.logger.Logger;
import net.socialhub.service.bluesky.define.BlueskyNotificationType;
import net.socialhub.service.bluesky.define.BlueskyReactionType;
import net.socialhub.service.bluesky.model.BlueskyComment;
import net.socialhub.service.bluesky.model.BlueskyPaging;
import net.socialhub.service.bluesky.model.BlueskyUser;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Arrays.copyOfRange;
import static java.util.stream.Collectors.toList;

public class BlueskyMapper {

    private static final Logger logger = Logger.getLogger(BlueskyMapper.class);

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    // ============================================================== //
    // Single Object Mapper
    // ============================================================== //

    /**
     * (詳細) ユーザーマッピング
     */
    public static User user(
            ActorDefsProfileViewDetailed account,
            Service service
    ) {
        BlueskyUser user = new BlueskyUser(service);
        user.setSimple(false);

        user.setId(account.getDid());
        user.setScreenName(account.getHandle());

        user.setName(account.getDisplayName());
        user.setIconImageUrl(account.getAvatar());
        user.setCoverImageUrl(account.getBanner());

        if (account.getPostsCount() != null) {
            user.setStatusesCount(account.getPostsCount().longValue());
        }
        if (account.getFollowersCount() != null) {
            user.setFollowersCount(account.getFollowersCount().longValue());
        }
        if (account.getFollowsCount() != null) {
            user.setFollowingsCount(account.getFollowsCount().longValue());
        }

        if (account.getViewer() != null) {
            ActorDefsViewerState state = account.getViewer();
            user.setFollowRecordUri(state.getFollowing());
            user.setFollowedRecordUri(state.getFollowedBy());
            user.setMuted(state.getMuted());
            user.setBlockedBy(state.getBlockedBy());
            user.setBlockingRecordUri(state.getBlocking());
        }

        user.setDescription(AttributedString.plain(account.getDescription()));
        user.setProtected(false);

        return user;
    }

    /**
     * (簡易) ユーザーマッピング
     */
    private static User user(
            ActorDefsProfileView account,
            Service service
    ) {
        BlueskyUser user = new BlueskyUser(service);
        user.setSimple(true);

        user.setId(account.getDid());
        user.setScreenName(account.getHandle());

        user.setName(account.getDisplayName());
        user.setIconImageUrl(account.getAvatar());

        if (account.getViewer() != null) {
            ActorDefsViewerState state = account.getViewer();
            user.setFollowRecordUri(state.getFollowing());
            user.setFollowedRecordUri(state.getFollowedBy());
            user.setMuted(state.getMuted());
            user.setBlockedBy(state.getBlockedBy());
            user.setBlockingRecordUri(state.getBlocking());
        }

        user.setDescription(AttributedString.plain(account.getDescription()));
        user.setProtected(false);
        return user;
    }

    /**
     * (簡易) ユーザーマッピング
     */
    private static User user(
            ActorDefsProfileViewBasic account,
            Service service
    ) {
        BlueskyUser user = new BlueskyUser(service);
        user.setSimple(true);

        user.setId(account.getDid());
        user.setScreenName(account.getHandle());

        user.setName(account.getDisplayName());
        user.setIconImageUrl(account.getAvatar());

        if (account.getViewer() != null) {
            ActorDefsViewerState state = account.getViewer();
            user.setFollowRecordUri(state.getFollowing());
            user.setFollowedRecordUri(state.getFollowedBy());
            user.setMuted(state.getMuted());
            user.setBlockedBy(state.getBlockedBy());
            user.setBlockingRecordUri(state.getBlocking());
        }

        user.setProtected(false);
        return user;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            FeedDefsFeedViewPost feed,
            Service service
    ) {
        FeedDefsPostView post = feed.getPost();
        return comment(post, service);
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            FeedDefsPostView post,
            Service service
    ) {
        BlueskyComment model = new BlueskyComment(service);

        try {
            model.setId(post.getUri());
            model.setCid(post.getCid());
            model.setUser(user(post.getAuthor(), service));
            model.setCreateAt(dateFormat.parse(post.getIndexedAt()));

            // TODO: Labels
            model.setPossiblySensitive(false);

            model.setLikeCount((post.getLikeCount() != null)
                    ? post.getLikeCount().longValue() : 0);
            model.setShareCount((post.getRepostCount() != null)
                    ? post.getRepostCount().longValue() : 0);
            model.setReplyCount((post.getReplyCount() != null)
                    ? post.getReplyCount().longValue() : 0);

            FeedDefsViewerState state = post.getViewer();
            model.setLiked(state.getLike() != null);
            model.setLikeRecordUri(state.getLike());
            model.setShared(state.getRepost() != null);
            model.setRepostRecordUri(state.getRepost());

            RecordUnion union = post.getRecord();
            if (union instanceof FeedPost) {
                FeedPost record = (FeedPost) union;
                model.setText(getAttributedText(record));
            }

            model.setMedias(new ArrayList<>());
            EmbedViewUnion embed = post.getEmbed();
            if (embed instanceof EmbedImagesView) {
                ((EmbedImagesView) embed).getImages().forEach(img ->
                        model.getMedias().add(media(img)));
            }

            return model;

        } catch (Exception e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }



    private static AttributedString getAttributedText(FeedPost post) {
        List<AttributedElement> elements = new ArrayList<>();
        byte[] bytes = post.getText().getBytes(StandardCharsets.UTF_8);

        // 読み進めたバイト数
        int readIndex = 0;

        if (post.getFacets() != null) {
            List<RichtextFacet> facets = post.getFacets().stream()
                    .sorted(Comparator.comparing(i -> i.getIndex().getByteStart()))
                    .collect(toList());

            for (RichtextFacet facet : facets) {
                if (facet.getFeatures() != null && !facet.getFeatures().isEmpty()) {
                    RichtextFacetFeatureUnion union = facet.getFeatures().get(0);
                    RichtextFacetByteSlice index = facet.getIndex();

                    // Facet の前を Text として取得
                    if (readIndex < index.getByteStart()) {
                        int beforeLen = (index.getByteStart() - readIndex);
                        byte[] beforeBytes = copyOfRange(bytes, readIndex, readIndex + beforeLen);

                        readIndex = index.getByteStart();
                        int afterLen = (bytes.length - beforeLen);
                        bytes = copyOfRange(bytes, readIndex, readIndex + afterLen);

                        String str = new String(beforeBytes, StandardCharsets.UTF_8);
                        AttributedItem element = new AttributedItem();
                        element.setKind(AttributedKind.PLAIN);
                        element.setExpandedText(str);
                        element.setDisplayText(str);
                        elements.add(element);
                    }

                    // Facet の部分を切り出して作成
                    int len = (index.getByteEnd() - index.getByteStart());
                    byte[] targetByte = copyOfRange(bytes, 0, len);

                    readIndex = index.getByteEnd();
                    int afterLen = (bytes.length - len);
                    bytes = copyOfRange(bytes, len, len + afterLen);

                    if (union instanceof RichtextFacetMention) {
                        RichtextFacetMention mention = (RichtextFacetMention) union;
                        String str = new String(targetByte, StandardCharsets.UTF_8);
                        AttributedItem element = new AttributedItem();
                        element.setKind(AttributedKind.ACCOUNT);
                        element.setExpandedText(mention.getDid());
                        element.setDisplayText(str);
                        elements.add(element);
                    }

                    if (union instanceof RichtextFacetLink) {
                        RichtextFacetLink link = (RichtextFacetLink) union;
                        String str = new String(targetByte, StandardCharsets.UTF_8);
                        AttributedItem element = new AttributedItem();
                        element.setKind(AttributedKind.LINK);
                        element.setExpandedText(link.getUri());
                        element.setDisplayText(str);
                        elements.add(element);
                    }
                }
            }
        }

        if (bytes.length > 0) {
            String str = new String(bytes, StandardCharsets.UTF_8);
            AttributedItem element = new AttributedItem();
            element.setKind(AttributedKind.PLAIN);
            element.setExpandedText(str);
            element.setDisplayText(str);
            elements.add(element);
        }

        return AttributedString.elements(elements);
    }

    /**
     * メディアマッピング
     */
    private static Media media(EmbedImagesViewImage img) {

        Media media = new Media();
        media.setType(MediaType.Image);
        media.setPreviewUrl(img.getThumb());
        media.setSourceUrl(img.getFullsize());
        return media;
    }

    /**
     * ユーザー関係マッピング
     */
    public static Relationship relationship(
            BlueskyUser user
    ) {
        Relationship relationship = new Relationship();
        relationship.setFollowing(user.getFollowRecordUri() != null);
        relationship.setFollowed(user.getFollowedRecordUri() != null);
        relationship.setMuting((user.getMuted() != null) ? user.getMuted() : false);
        relationship.setBlocking((user.getBlockingRecordUri() != null));
        return relationship;
    }

    /**
     * 通知マッピング
     */
    public static Notification notification(
            NotificationListNotificationsNotification notification,
            Map<String, FeedDefsPostView> posts,
            Service service
    ) {

        try {
            Notification model = new Notification(service);
            model.setId(notification.getUri());
            model.setCreateAt(dateFormat.parse(notification.getIndexedAt()));

            BlueskyNotificationType type =
                    BlueskyNotificationType
                            .of(notification.getReason());

            if (type != null) {
                model.setType(type.getCode());
                if (type.getAction() != null) {
                    model.setAction(type.getAction().getCode());
                }
            }

            if (notification.getAuthor() != null) {
                model.setUsers(new ArrayList<>());
                model.getUsers().add(user(notification.getAuthor(), service));
            }


            if (notification.getRecord() != null) {
                RecordUnion union = notification.getRecord();

                if (union instanceof FeedPost) {
                    String subject = notification.getReasonSubject();
                    FeedDefsPostView post = posts.get(subject);

                    if (post != null) {
                        model.setComments(new ArrayList<>());
                        model.getComments().add(comment(post, service));
                    }
                }
            }

            return model;

        } catch (Exception e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * リアクション候補マッピング
     */
    public static List<ReactionCandidate> reactionCandidates() {
        List<ReactionCandidate> candidates = new ArrayList<>();

        ReactionCandidate like = new ReactionCandidate();
        like.setCategory(EmojiCategoryType.Activities.getCode());
        like.setName(BlueskyReactionType.Like.getCode().get(0));
        like.addAlias(BlueskyReactionType.Like.getCode());
        candidates.add(like);

        ReactionCandidate share = new ReactionCandidate();
        share.setCategory(EmojiCategoryType.Activities.getCode());
        share.setName(BlueskyReactionType.Repost.getCode().get(0));
        share.addAlias(BlueskyReactionType.Repost.getCode());
        candidates.add(share);

        return candidates;
    }

    // ============================================================== //
    // List Object Mapper
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            List<ActorDefsProfileView> users,
            Paging paging,
            Service service
    ) {
        if (paging instanceof BlueskyPaging) {
            BlueskyPaging p = (BlueskyPaging) paging;
            users = takeUntil(users, f -> {
                Identify id = p.getFirstRecord();
                return id != null && f.getDid().equals(id.getId());
            });
        }

        // 空の場合
        if (users.isEmpty()) {
            Pageable<User> model = new Pageable<>();
            model.setEntities(new ArrayList<>());
            model.setPaging(paging);
            return model;
        }

        Pageable<User> model = new Pageable<>();
        model.setEntities(users.stream()
                .map(a -> user(a, service))
                .collect(toList()));

        model.setPaging(BlueskyPaging.fromPaging(paging));
        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timelineByFeeds(
            List<FeedDefsFeedViewPost> feed,
            Paging paging,
            Service service
    ) {
        if (paging instanceof BlueskyPaging) {
            BlueskyPaging p = (BlueskyPaging) paging;
            feed = takeUntil(feed, f -> {
                Identify id = p.getFirstRecord();
                return id != null && f.getPost()
                        .getUri().equals(id.getId());
            });
        }

        // 空の場合
        if (feed.isEmpty()) {
            Pageable<Comment> model = new Pageable<>();
            model.setEntities(new ArrayList<>());
            model.setPaging(paging);
            return model;
        }

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(feed.stream()
                .map(a -> comment(a, service))
                .collect(toList()));

        model.setPaging(BlueskyPaging.fromPaging(paging));
        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timelineByPosts(
            List<FeedDefsPostView> posts,
            Paging paging,
            Service service
    ) {
        if (paging instanceof BlueskyPaging) {
            BlueskyPaging p = (BlueskyPaging) paging;
            posts = takeUntil(posts, f -> {
                Identify id = p.getFirstRecord();
                return id != null && f.getUri().equals(id.getId());
            });
        }

        // 空の場合
        if (posts.isEmpty()) {
            Pageable<Comment> model = new Pageable<>();
            model.setEntities(new ArrayList<>());
            model.setPaging(paging);
            return model;
        }

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(posts.stream()
                .map(a -> comment(a, service))
                .collect(toList()));

        model.setPaging(BlueskyPaging.fromPaging(paging));
        return model;
    }

    /**
     * 通知マッピング
     */
    public static Pageable<Notification> notifications(
            List<NotificationListNotificationsNotification> notifications,
            List<FeedDefsPostView> posts,
            Paging paging,
            Service service
    ) {

        if (paging instanceof BlueskyPaging) {
            BlueskyPaging p = (BlueskyPaging) paging;
            notifications = takeUntil(notifications, f -> {
                Identify id = p.getFirstRecord();
                return id != null && f.getUri().equals(id.getId());
            });
        }

        // 空の場合
        if (notifications.isEmpty()) {
            Pageable<Notification> model = new Pageable<>();
            model.setEntities(new ArrayList<>());
            model.setPaging(paging);
            return model;
        }

        Map<String, FeedDefsPostView> postMap = new HashMap<>();
        posts.forEach(a -> postMap.put(a.getUri(), a));

        Pageable<Notification> model = new Pageable<>();
        model.setEntities(notifications.stream()
                .map(n -> notification(n, postMap, service))
                .collect(toList()));

        model.setPaging(BlueskyPaging.fromPaging(paging));
        return model;
    }

    static <T> List<T> takeUntil(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                break;
            }
            result.add(item);
        }
        return result;
    }
}
