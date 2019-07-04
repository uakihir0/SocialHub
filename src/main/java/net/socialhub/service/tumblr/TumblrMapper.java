package net.socialhub.service.tumblr;

import com.tumblr.jumblr.types.*;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.tumblr.TumblrIconSize;
import net.socialhub.define.service.tumblr.TumblrReactionType;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.User;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.tumblr.TumblrComment;
import net.socialhub.model.service.addition.tumblr.TumblrUser;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.utils.MapperUtil;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static net.socialhub.define.service.tumblr.TumblrIconSize.S512;

public class TumblrMapper {

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            com.tumblr.jumblr.types.TextPost.class, //
            com.tumblr.jumblr.types.Video.class, //
            com.tumblr.jumblr.types.Note.class);

    // ============================================================== //
    // User
    // ============================================================== //

    /**
     * ユーザーマッピング
     * (Primary のブログをユーザーと設定)
     * (User is user's primary blog)
     */
    public static User user(
            com.tumblr.jumblr.types.User user, //
            Service service) {

        // プライマリブログについての処理
        for (Blog blog : user.getBlogs()) {
            if (blog.isPrimary()) {
                return user(blog, service);
            }
        }

        User model = new User(service);
        model.setName(user.getName());
        return model;
    }

    /**
     * ユーザーマッピング
     * (ブログをユーザーと設定)
     * (User is user's blog)
     */
    public static User user(
            com.tumblr.jumblr.types.Blog blog, //
            Service service) {

        TumblrUser model = new TumblrUser(service);

        model.setName(blog.getName());
        model.setBlogTitle(blog.getTitle());
        model.setDescription(new AttributedString(blog.getDescription()));

        String host = getBlogIdentify(blog);
        model.setIconImageUrl(getAvatarUrl(host, S512));
        model.setScreenName(host);
        model.setId(host);

        model.setFollowersCount(blog.getFollowersCount());
        model.setPostsCount(blog.getPostCount());
        model.setLikesCount(blog.getLikeCount());
        model.setBlogUrl(blog.getUrl());

        Relationship relationship = new Relationship();
        relationship.setFollowing((blog.getFollowed() == Boolean.TRUE));
        relationship.setBlocking((blog.getIsBlockedFromPrimary() == Boolean.TRUE));
        model.setRelationship(relationship);

        return model;
    }

    /**
     * ユーザーマッピング
     * (そのブログの投稿に紐づくユーザーと設定)
     */
    public static User user(
            com.tumblr.jumblr.types.Post post, //
            Map<String, Trail> trails, //
            Service service) {

        User model = user(post.getBlog(), service);

        if (trails.containsKey(post.getBlog().getName())) {
            Trail trail = trails.get(post.getBlog().getName());
            model.setCoverImageUrl(trail.getBlog().getTheme().getHeaderImage());
        }

        return model;
    }

    /**
     * ユーザーマッピング
     * (そのブログの投稿に紐づくユーザーと設定)
     */
    public static User reblogUser(
            com.tumblr.jumblr.types.Post post, //
            Map<String, Trail> trails, //
            Service service) {

        TumblrUser model = new TumblrUser(service);

        String host = getUrlHost(post.getRebloggedRootUrl());
        model.setIconImageUrl(getAvatarUrl(host, S512));
        model.setScreenName(host);
        model.setId(host);

        String name = post.getRebloggedRootName();

        if (trails.containsKey(name)) {
            Trail trail = trails.get(name);
            model.setCoverImageUrl(trail.getBlog().getTheme().getHeaderImage());
        }

        return model;
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * コメントマッピング
     */
    public static Comment comment(
            com.tumblr.jumblr.types.Post post, //
            Map<String, Trail> trails, //
            Service service) {

        TumblrComment model = new TumblrComment(service);
        model.setCreateAt(new Date(post.getTimestamp() * 1000));
        model.setReblogKey(post.getReblogKey());

        model.setId(post.getId());
        model.setUser(user(post, trails, service));

        // リブログ情報を設定
        if (post.getRebloggedRootId() != null) {
            model.setSharedComment(reblogComment(post, trails, service));
            model.setMedias(new ArrayList<>());

        } else {

            // コンテンツを格納
            setMedia(model, post);
        }

        return model;
    }

    /**
     * コメントマッピング
     * (Handle as shared post)
     */
    public static Comment reblogComment(
            com.tumblr.jumblr.types.Post post, //
            Map<String, Trail> trails, //
            Service service) {

        TumblrComment model = new TumblrComment(service);
        model.setCreateAt(new Date(post.getTimestamp() * 1000));
        model.setReblogKey(post.getReblogKey());

        model.setId(post.getRebloggedRootId());
        model.setUser(reblogUser(post, trails, service));
        setMedia(model, post);


        return model;
    }

    // ============================================================== //
    // Medias
    // ============================================================== //

    public static void setMedia(
            TumblrComment model, //
            com.tumblr.jumblr.types.Post post) {

        model.setText(new AttributedString(post.getSummary()));
        model.setMedias(new ArrayList<>());

        if (post instanceof PhotoPost) {
            PhotoPost photo = (PhotoPost) post;
            model.getMedias().addAll(photos(photo.getPhotos()));
        }

        if (post instanceof VideoPost) {
            model.getMedias().add(video((VideoPost) post));
        }
    }


    /**
     * 画像マッピング
     */
    private static List<Media> photos(
            List<Photo> photos) {

        List<Media> results = new ArrayList<>();
        for (Photo photo : photos) {

            Media media = new Media();
            media.setType(MediaType.Image);
            media.setSourceUrl(photo.getOriginalSize().getUrl());
            media.setPreviewUrl(photo.getOriginalSize().getUrl());
            results.add(media);
        }

        return results;
    }

    /**
     * 動画マッピング
     */
    private static Media video(
            VideoPost video) {

        Media media = new Media();
        media.setType(MediaType.Movie);
        media.setSourceUrl(video.getPermalinkUrl());
        media.setPreviewUrl(video.getThumbnailUrl());
        return media;
    }

    // ============================================================== //
    // Reactions
    // ============================================================== //

    /**
     * リアクション候補マッピング
     */
    public static List<ReactionCandidate> reactionCandidates() {
        List<ReactionCandidate> candidates = new ArrayList<>();

        ReactionCandidate like = new ReactionCandidate();
        like.setCategory(EmojiCategoryType.Activities.getCode());
        like.setName(TumblrReactionType.Like.getCode().get(0));
        like.addAlias(TumblrReactionType.Like.getCode());
        candidates.add(like);

        ReactionCandidate share = new ReactionCandidate();
        share.setCategory(EmojiCategoryType.Activities.getCode());
        share.setName(TumblrReactionType.Reblog.getCode().get(0));
        share.addAlias(TumblrReactionType.Reblog.getCode());
        candidates.add(share);

        return candidates;
    }


    // ============================================================== //
    // Timelines
    // ============================================================== //

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            List<com.tumblr.jumblr.types.Post> posts, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        Map<String, Trail> trails = getTrailMap(posts);

        model.setEntities(posts.stream() //
                .map(e -> comment(e, trails, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingTumblrPaging(paging));
        return model;
    }

    // ============================================================== //
    // UserList
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            List<com.tumblr.jumblr.types.User> users, //
            Service service, //
            Paging paging) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(users.stream() //
                .map(e -> user(e, service)) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingTumblrPaging(paging));
        return model;
    }


    /**
     * ユーザーマッピング
     */
    public static Pageable<User> usersByBlogs(
            List<com.tumblr.jumblr.types.Blog> blogs, //
            Service service, //
            Paging paging) {


        Pageable<User> model = new Pageable<>();
        model.setEntities(blogs.stream() //
                .map(e -> user(e, service)) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingTumblrPaging(paging));
        return model;
    }

    // ============================================================== //
    // Supports
    // ============================================================== //

    /**
     * ホスト名を取得 (プライマリを取得)
     * Get primary blog host from url
     */
    public static String getUserIdentify(List<Blog> blogs) {
        for (Blog blog : blogs) {
            if (blog.isPrimary()) {
                return getBlogIdentify(blog);
            }
        }
        return null;
    }

    /**
     * ホスト名を取得
     * Get blog host from url
     */
    public static String getBlogIdentify(Blog blog) {
        return getUrlHost(blog.getUrl());
    }

    /**
     * ホスト名を取得
     * Get host from url
     */
    public static String getUrlHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * ユーザーの画面マップを取得
     * Get Trail Map
     */
    public static Map<String, Trail> getTrailMap(
            List<com.tumblr.jumblr.types.Post> posts) {

        List<Trail> trails = posts.stream() //
                .flatMap(e -> e.getTrail().stream()) //
                .collect(Collectors.toList());

        Map<String, Trail> results = new HashMap<>();
        for (Trail trail : trails) {
            results.put(trail.getBlog().getName(), trail);
        }
        return results;
    }

    /**
     * ユーザー情報のマージ処理
     */
    public static void margeUser(User to, User from) {
        if ((to != null) && (from != null)) {
            to.setCoverImageUrl(from.getCoverImageUrl());
        }
    }

    /**
     * アバター画像を取得
     * Get avatar url
     */
    public static String getAvatarUrl(String host, TumblrIconSize size) {
        return "https://api.tumblr.com/v2/blog/" + host + "/avatar/" + size.getSize();
    }

}
