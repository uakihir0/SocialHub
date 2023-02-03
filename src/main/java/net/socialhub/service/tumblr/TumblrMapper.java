package net.socialhub.service.tumblr;

import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.QuotePost;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.Theme;
import com.tumblr.jumblr.types.Trail;
import com.tumblr.jumblr.types.VideoPost;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.tumblr.TumblrIconSize;
import net.socialhub.define.service.tumblr.TumblrReactionType;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.common.xml.XmlConvertRule;
import net.socialhub.model.common.xml.XmlDocument;
import net.socialhub.model.common.xml.XmlTag;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.tumblr.TumblrComment;
import net.socialhub.model.service.addition.tumblr.TumblrPaging;
import net.socialhub.model.service.addition.tumblr.TumblrUser;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.utils.XmlParseUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.socialhub.define.service.tumblr.TumblrIconSize.S512;

public class TumblrMapper {

    /// J2ObjC はダイナミックロードできない為に使用を明示するために使用
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

        // FIXME: 説明文が HTML のユーザーはなぜ？
        if (blog.getDescription() != null) {
            try {
                // Tumblr の自己紹介文はまず HTML で解釈
                model.setDescription(AttributedString.xhtml(blog.getDescription()));
            } catch (Exception ignore) {
                // 解釈に失敗した場合は単純なプレーンテキストとして解釈
                model.setDescription(AttributedString.plain(blog.getDescription()));
            }
        }

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
            List<Theme> themes = trail.getBlog().getTheme();

            if (themes != null && !themes.isEmpty()) {
                model.setCoverImageUrl(themes.get(0).getHeaderImage());
            }
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

        String host = getBlogIdentify(post.getRebloggedRootUrl());
        String name = post.getRebloggedRootName();

        model.setIconImageUrl(getAvatarUrl(host, S512));
        model.setScreenName(host);
        model.setName(name);
        model.setId(host);

        if (trails.containsKey(name)) {
            Trail trail = trails.get(name);
            List<Theme> themes = trail.getBlog().getTheme();

            if (themes != null && !themes.isEmpty()) {
                model.setCoverImageUrl(themes.get(0).getHeaderImage());
            }
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
        model.setNoteCount(post.getNoteCount());
        model.setWebUrl(post.getPostUrl());

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
        model.setNoteCount(post.getNoteCount());

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

        model.setMedias(new ArrayList<>());

        // Trail から優先的に取得
        if (post.getTrail() != null) {
            if (post.getTrail().size() > 0) {
                post.getTrail().stream()
                        .filter(Trail::isCurrentItem)
                        .findFirst().ifPresent((trail) ->
                                textMedia(model, removeSharedBlogLink(trail.getContentRaw())));
            }
        }

        if (post instanceof PhotoPost) {
            PhotoPost photo = (PhotoPost) post;
            model.getMedias().addAll(photos(photo.getPhotos()));

            if (model.getText() == null) {
                String str = removeSharedBlogLink(photo.getCaption());
                textMedia(model, str);
            }
        }
        if (post instanceof TextPost) {
            TextPost text = (TextPost) post;

            if (model.getText() == null) {
                String str = removeSharedBlogLink(text.getBody());
                textMedia(model, str);
            }
        }
        if (post instanceof VideoPost) {
            VideoPost video = (VideoPost) post;
            model.getMedias().add(video(video));

            if (model.getText() == null) {
                String str = removeSharedBlogLink(video.getCaption());
                textMedia(model, str);
            }
        }

        if (post instanceof QuotePost) {
            QuotePost quote = (QuotePost) post;

            if (model.getText() == null) {
                String str = removeSharedBlogLink(
                        quote.getText() + " / " + quote.getSource());
                textMedia(model, str);
            }
        }
    }

    /**
     * テキスト情報を設定
     */
    private static void textMedia(
            TumblrComment model,
            String str) {

        XmlDocument xml = XmlParseUtil.xhtml(str);
        model.setText(xml.toAttributedString(new XmlConvertRule()));

        // 画像一覧を取得
        List<XmlTag> imgTags = xml.findXmlTag("img");
        for (XmlTag imgTag : imgTags) {

            // 画像を追加
            Media media = new Media();
            media.setType(MediaType.Image);
            media.setSourceUrl(imgTag.getAttributes().get("src"));
            media.setPreviewUrl(imgTag.getAttributes().get("src"));
            model.getMedias().add(media);
        }

        // 動画一覧を選択
        List<XmlTag> videoTags = xml.findXmlTag("video");
        for (XmlTag videoTag : videoTags) {

            // さらにそこからソースタグを抽出
            List<XmlTag> sourceTags = videoTag.findXmlTag("source");
            if (sourceTags.size() == 1) {
                XmlTag sourceTag = sourceTags.get(0);

                // 動画を追加
                Media media = new Media();
                media.setType(MediaType.Movie);
                media.setSourceUrl(sourceTag.getAttributes().get("src"));
                media.setPreviewUrl(videoTag.getAttributes().get("poster"));
                model.getMedias().add(media);
            }
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

        // VideoUrl or PermalinkUrl を選択
        media.setSourceUrl(video.getVideoUrl());
        if (media.getSourceUrl() == null) {
            media.setSourceUrl(video.getPermalinkUrl());
        }

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

        model.setPaging(TumblrPaging.fromPaging(paging));
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

        model.setPaging(TumblrPaging.fromPaging(paging));
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

        model.setPaging(TumblrPaging.fromPaging(paging));
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
        return getBlogIdentify(blog.getUrl());
    }

    /**
     * ホスト名を取得
     * Get blog host from url
     */
    public static String getBlogIdentify(String blogUrl) {
        String host = getUrlHost(blogUrl);

        // ドメイン設定していないブログは www.tumblr.com になる (?)
        if (host != null && host.equals("www.tumblr.com")) {
            String[] elements = blogUrl.split("/");
            if (elements.length > 1) {
                String uid = elements[elements.length - 1];
                host = host.replace("www", uid);
            }
        }
        return host;
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

        List<Trail> trails = posts.stream()
                .map(Post::getTrail)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
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

    /**
     * Remove Shared Post Prefix
     * Share されたポストの定型句を削除
     */
    private static String removeSharedBlogLink(String text) {
        String regex = "^(<p><a(.+?)href=\"(.+?)\"(.*?)>(.+?)</a>:</p>)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        if (m.find()) {
            String all = m.group(1);

            // ブログへのリンクであることを確認
            if (m.group(2).contains("class=\"tumblr_blog\"") ||
                    m.group(4).contains("class=\"tumblr_blog\"")) {
                return text.substring(all.length());
            }
        }
        return text;
    }
}
