package net.socialhub.service.bluesky.action;

import bsky4j.model.bsky.actor.ActorDefsProfileView;
import bsky4j.model.bsky.actor.ActorDefsProfileViewBasic;
import bsky4j.model.bsky.actor.ActorDefsProfileViewDetailed;
import bsky4j.model.bsky.actor.ActorDefsViewerState;
import bsky4j.model.bsky.embed.EmbedImages;
import bsky4j.model.bsky.embed.EmbedImagesImage;
import bsky4j.model.bsky.embed.EmbedUnion;
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost;
import bsky4j.model.bsky.feed.FeedDefsPostView;
import bsky4j.model.bsky.feed.FeedDefsViewerState;
import bsky4j.model.bsky.feed.FeedPost;
import bsky4j.model.share.RecordUnion;
import net.socialhub.core.define.MediaType;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.User;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.logger.Logger;
import net.socialhub.service.bluesky.model.BlueskyComment;
import net.socialhub.service.bluesky.model.BlueskyPaging;
import net.socialhub.service.bluesky.model.BlueskyUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
            // TODO: Block
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
            // TODO: Block
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
            // TODO: Block
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

        BlueskyComment model = new BlueskyComment(service);
        FeedDefsPostView post = feed.getPost();

        try {
            model.setId(post.getUri());
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
                model.setText(AttributedString.plain(record.getText()));

                model.setMedias(new ArrayList<>());

                EmbedUnion embed = record.getEmbed();
                if (embed instanceof EmbedImages) {
                    ((EmbedImages) embed).getImages().forEach(img ->
                            model.getMedias().add(media(img)));
                }
            }

            return model;

        } catch (Exception e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * メディアマッピング
     */
    private static Media media(EmbedImagesImage image) {

        Media media = new Media();
        media.setType(MediaType.Image);
        media.setPreviewUrl(image.getImage().getRef().getLink());
        media.setSourceUrl(image.getImage().getRef().getLink());
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
        // TODO: Block
        return relationship;
    }

    // ============================================================== //
    // List Object Mapper
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            List<ActorDefsProfileView> follows,
            String cursor,
            Service service
    ) {
        Pageable<User> model = new Pageable<>();
        model.setEntities(follows.stream()
                .map(a -> user(a, service))
                .collect(toList()));

        List<User> entities = model.getEntities();
        User last = entities.get(entities.size() - 1);

        BlueskyPaging paging = new BlueskyPaging();
        paging.setNextCursor(cursor);
        paging.setLastRecord(last);
        model.setPaging(paging);

        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeline(
            List<FeedDefsFeedViewPost> feed,
            String cursor,
            Service service
    ) {
        Pageable<Comment> model = new Pageable<>();
        model.setEntities(feed.stream()
                .map(a -> comment(a, service))
                .collect(toList()));

        List<Comment> entities = model.getEntities();
        Comment last = entities.get(entities.size() - 1);

        BlueskyPaging paging = new BlueskyPaging();
        paging.setNextCursor(cursor);
        paging.setLastRecord(last);
        model.setPaging(paging);

        return model;
    }
}
