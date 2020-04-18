package net.socialhub.service.twitter;

import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.twitter.TwitterIconSize;
import net.socialhub.define.service.twitter.TwitterMediaType;
import net.socialhub.define.service.twitter.TwitterReactionType;
import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedItem;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Application;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.twitter.TwitterChannel;
import net.socialhub.model.service.addition.twitter.TwitterComment;
import net.socialhub.model.service.addition.twitter.TwitterMedia;
import net.socialhub.model.service.addition.twitter.TwitterThread;
import net.socialhub.model.service.addition.twitter.TwitterUser;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.IndexPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.HttpRequest;
import twitter4j.MediaEntity;
import twitter4j.PagableResponseList;
import twitter4j.QueryResult;
import twitter4j.RequestMethod;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.URLEntity;
import twitter4j.UserList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

public class TwitterMapper {

    private static final String HOST = "https://twitter.com/";

    public static TwitterIconSize DEFAULT_ICON_SIZE = TwitterIconSize.W200H200;

    /**
     * ユーザーマッピング
     */
    public static User user(
            twitter4j.User user,
            Service service) {

        TwitterUser model = new TwitterUser(service);

        model.setId(user.getId());
        model.setName(user.getName());
        model.setScreenName(user.getScreenName());
        model.setIconImageUrl(getDefaultIconSize(user));
        model.setCoverImageUrl(user.getProfileBannerMobileRetinaURL());

        model.setStatusesCount((long) user.getStatusesCount());
        model.setFavoritesCount((long) user.getFavouritesCount());
        model.setFollowingsCount((long) user.getFriendsCount());
        model.setFollowersCount((long) user.getFollowersCount());

        model.setVerified(user.isVerified());
        model.setProtected(user.isProtected());
        model.setLocation(user.getLocation());

        // URL の情報を設定
        if (user.getURL() != null && !user.getURL().isEmpty()) {
            AttributedString url = AttributedString.plain(user.getURL());
            model.setUrl(url);

            URLEntity entity = user.getURLEntity();
            for (AttributedElement elem : url.getElements()) {
                if (elem.getExpandedText().equals(entity.getText())) {

                    if (elem instanceof AttributedItem) {
                        AttributedItem item = (AttributedItem) elem;
                        item.setExpandedText(entity.getExpandedURL());
                        item.setDisplayText(entity.getDisplayURL());
                    }
                }
            }
        }

        // プロフィールページの設定
        String profile = HOST + user.getScreenName();
        model.setProfileUrl(AttributedString.plain(profile));

        // 説明文の情報を設定
        AttributedString desc = AttributedString.plain(user.getDescription());
        model.setDescription(desc);

        // URL の DisplayURL ExpandedURL を設定
        for (URLEntity entity : user.getDescriptionURLEntities()) {
            for (AttributedElement elem : desc.getElements()) {
                if (elem.getExpandedText().equals(entity.getText())) {

                    if (elem instanceof AttributedItem) {
                        AttributedItem item = (AttributedItem) elem;
                        item.setExpandedText(entity.getExpandedURL());
                        item.setDisplayText(entity.getDisplayURL());
                    }
                }
            }
        }

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            Status status,
            Service service) {

        TwitterComment model = new TwitterComment(service);

        model.setId(status.getId());
        model.setCreateAt(status.getCreatedAt());
        model.setUser(user(status.getUser(), service));
        model.setPossiblySensitive(status.isPossiblySensitive());
        model.setApplication(application(status.getSource()));

        model.setLikeCount((long) status.getFavoriteCount());
        model.setShareCount((long) status.getRetweetCount());

        model.setLiked(status.isFavorited());
        model.setShared(status.isRetweeted());

        if (status.getInReplyToStatusId() > 0) {
            Identify replyTo = new Identify(service);
            replyTo.setId(status.getInReplyToStatusId());
            model.setReplyTo(replyTo);
        }

        // リツイートの場合内部を展開
        if (status.isRetweet()) {

            model.setSharedComment(comment(status.getRetweetedStatus(), service));
            model.setMedias(new ArrayList<>());

        } else {

            // 引用リツイートの場合はここで展開
            if (status.getQuotedStatus() != null) {
                model.setSharedComment(comment(status.getQuotedStatus(), service));
            }

            AttributedString text = AttributedString.plain(displayText(status));
            model.setText(text);

            // URL の DisplayURL ExpandedURL を設定
            for (URLEntity entity : status.getURLEntities()) {
                for (AttributedElement elem : text.getElements()) {
                    if (elem.getExpandedText().equals(entity.getText())) {

                        if (elem instanceof AttributedItem) {
                            AttributedItem item = (AttributedItem) elem;
                            item.setExpandedText(entity.getExpandedURL());
                            item.setDisplayText(entity.getDisplayURL());
                        }
                    }
                }
            }

            // メディア情報を取得時に展開
            model.setMedias(medias(status.getMediaEntities()));
        }

        return model;
    }

    /**
     * DM マッピング
     */
    public static Comment comment(
            DirectMessage message,
            Map<Long, User> users,
            Twitter twitter,
            Service service) {

        TwitterComment model = new TwitterComment(service);

        model.setId(message.getId());
        model.setCreateAt(message.getCreatedAt());
        model.setUser(users.get(message.getSenderId()));
        model.setDirectMessage(true);

        AttributedString text = AttributedString.plain(displayText(message));
        model.setText(text);

        // URL の DisplayURL ExpandedURL を設定
        for (URLEntity entity : message.getURLEntities()) {
            for (AttributedElement elem : text.getElements()) {
                if (elem.getExpandedText().equals(entity.getText())) {

                    if (elem instanceof AttributedItem) {
                        AttributedItem item = (AttributedItem) elem;
                        item.setExpandedText(entity.getExpandedURL());
                        item.setDisplayText(entity.getDisplayURL());
                    }
                }
            }
        }

        // メディア情報を取得時に展開
        model.setMedias(medias(message.getMediaEntities()));

        // メディアに認証ヘッダーを設定
        for (Media media : model.getMedias()) {
            if (media instanceof TwitterMedia) {
                TwitterMedia tm = (TwitterMedia) media;
                HttpRequest request = new HttpRequest(RequestMethod.GET,
                        tm.getSourceUrl(), null, twitter.getAuthorization(), emptyMap());
                tm.setAuthorizationHeader(request.getAuthorization().getAuthorizationHeader(request));
            }
        }

        return model;
    }

    /**
     * メディアマッピング
     */
    public static List<Media> medias(
            MediaEntity[] entities) {

        List<Media> medias = new ArrayList<>();
        for (MediaEntity entity : entities) {
            medias.add(media(entity));
        }
        return medias;
    }

    /**
     * メディアマッピング
     */
    public static Media media(
            MediaEntity entity) {

        switch (TwitterMediaType.of(entity.getType())) {

            case Photo: {
                TwitterMedia media = new TwitterMedia();
                media.setType(MediaType.Image);

                media.setSourceUrl(entity.getMediaURLHttps());
                media.setPreviewUrl(entity.getMediaURLHttps());
                return media;
            }

            case Video: {
                TwitterMedia media = new TwitterMedia();
                media.setType(MediaType.Movie);

                media.setPreviewUrl(entity.getMediaURLHttps());
                for (MediaEntity.Variant variant : entity.getVideoVariants()) {

                    // ストリーム向けの動画タイプが存在する場合はそれを利用
                    if (variant.getContentType().equals("application/x-mpegURL")) {
                        media.setStreamVideoUrl(variant.getUrl());
                        media.setSourceUrl(variant.getUrl());
                    }
                }

                // それ意外の場合一番高画質のものを選択
                Stream.of(entity.getVideoVariants()) //
                        .filter((e) -> e.getContentType().startsWith("video")) //
                        .max(Comparator.comparingInt(MediaEntity.Variant::getBitrate)) //
                        .ifPresent((variant) -> {
                            String url = variant.getUrl();
                            media.setMp4VideoUrl(url);

                            if (media.getSourceUrl() == null) {
                                media.setSourceUrl(url);
                            }
                        });

                return media;
            }
        }
        return null;
    }

    /**
     * ユーザー関係
     */
    public static Relationship relationship(
            Friendship friendship) {

        Relationship model = new Relationship();
        model.setFollowed(friendship.isFollowedBy());
        model.setFollowing(friendship.isFollowing());
        return model;
    }

    /**
     * ユーザー関係
     */
    public static Relationship relationship(
            twitter4j.Relationship relationship) {

        Relationship model = new Relationship();
        model.setFollowed(relationship.isSourceFollowedByTarget());
        model.setFollowing(relationship.isSourceFollowingTarget());
        model.setBlocking(relationship.isSourceBlockingTarget());
        model.setMuting(relationship.isSourceMutingTarget());
        return model;
    }

    /**
     * アプリケーションマッピング
     */
    public static Application application(
            String source) {

        Application app = new Application();
        {
            Pattern p = Pattern.compile("href=\"(.+?)\"");
            Matcher m = p.matcher(source);
            if (m.find()) {
                app.setWebsite(m.group(1));
            }
        }
        {
            Pattern p = Pattern.compile(">(.+?)<");
            Matcher m = p.matcher(source);
            if (m.find()) {
                app.setName(m.group(1));
            }
        }
        return app;
    }

    /**
     * チャンネルマッピング
     */
    public static Channel channel(
            UserList list,
            Service service) {

        TwitterChannel channel = new TwitterChannel(service);
        channel.setAuthor(user(list.getUser(), service));

        channel.setId(list.getId());
        channel.setName(list.getName());
        channel.setCreateAt(list.getCreatedAt());
        channel.setDescription(list.getDescription());
        channel.setPublic(list.isPublic());

        return channel;
    }

    /**
     * リアクション候補マッピング
     */
    public static List<ReactionCandidate> reactionCandidates() {
        List<ReactionCandidate> candidates = new ArrayList<>();

        ReactionCandidate like = new ReactionCandidate();
        like.setCategory(EmojiCategoryType.Activities.getCode());
        like.setName(TwitterReactionType.Favorite.getCode().get(0));
        like.addAlias(TwitterReactionType.Favorite.getCode());
        candidates.add(like);

        ReactionCandidate share = new ReactionCandidate();
        share.setCategory(EmojiCategoryType.Activities.getCode());
        share.setName(TwitterReactionType.Retweet.getCode().get(0));
        share.addAlias(TwitterReactionType.Retweet.getCode());
        candidates.add(share);

        return candidates;
    }

    /**
     * ページングマッピング
     */
    public static Paging paging(
            twitter4j.Paging paging) {

        if (paging.getPage() > 0) {
            IndexPaging model = new IndexPaging();
            model.setPage((long) paging.getPage());
            model.setCount((long) paging.getCount());
            return model;

        } else {
            BorderPaging model = new BorderPaging();
            model.setMaxId(paging.getMaxId());
            model.setSinceId(paging.getSinceId());
            model.setCount(((long) paging.getCount()));
            beTwitterPaging(model);
            return model;
        }
    }

    public static twitter4j.Paging fromPaging(
            Paging paging) {

        twitter4j.Paging model = new twitter4j.Paging();

        if ((paging.getCount() != null) && (paging.getCount() != 0)) {
            model.setCount(paging.getCount().intValue());
        }

        if (paging instanceof IndexPaging) {
            IndexPaging index = (IndexPaging) paging;
            model.setPage(index.getPage().intValue());
        }

        if (paging instanceof BorderPaging) {
            BorderPaging border = (BorderPaging) paging;

            if (border.getMaxId() != null) {
                model.setMaxId(border.getMaxId());
            }
            if (border.getSinceId() != null) {
                model.setSinceId(border.getSinceId());
            }
        }

        return model;
    }

    public static twitter4j.Query queryFromPaging(
            Paging paging) {

        twitter4j.Query model = new twitter4j.Query();

        if ((paging.getCount() != null) && (paging.getCount() != 0)) {
            model.setCount(paging.getCount().intValue());
        }

        if (paging instanceof BorderPaging) {
            BorderPaging border = (BorderPaging) paging;

            if (border.getMaxId() != null) {
                model.setMaxId(border.getMaxId());
            }
            if (border.getSinceId() != null) {
                model.setSinceId(border.getSinceId());
            }
        }

        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            ResponseList<Status> statuses, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(statuses.stream().map(e -> comment(e, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(toList()));

        model.setPaging(beTwitterPaging(BorderPaging.fromPaging(paging)));
        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            QueryResult results, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(results.getTweets().stream().map(e -> comment(e, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(toList()));

        model.setPaging(beTwitterPaging(BorderPaging.fromPaging(paging)));
        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            PagableResponseList<twitter4j.User> users, //
            Service service, //
            Paging paging) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(users.stream()
                .map(e -> user(e, service))
                .collect(toList()));

        CursorPaging<Long> pg = CursorPaging.fromPaging(paging);
        TwitterMapper.setCursorPaging(pg, users);
        model.setPaging(pg);
        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            ResponseList<twitter4j.User> users, //
            Service service, //
            Paging paging) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(users.stream()
                .map(e -> user(e, service))
                .collect(toList()));

        IndexPaging pg = IndexPaging.fromPaging(paging);
        model.setPaging(pg);
        return model;
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channels(
            PagableResponseList<UserList> lists, //
            Service service, //
            Paging paging) {

        Pageable<Channel> model = new Pageable<>();
        model.setEntities(lists.stream().map(e -> channel(e, service)) //
                .collect(toList()));

        CursorPaging<Long> pg = CursorPaging.fromPaging(paging);
        TwitterMapper.setCursorPaging(pg, lists);
        model.setPaging(pg);
        return model;
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channels(
            ResponseList<UserList> lists, //
            Service service, //
            Paging paging) {

        Pageable<Channel> model = new Pageable<>();
        model.setEntities(lists.stream().map(e -> channel(e, service)) //
                .collect(toList()));

        Paging pg = paging.copy();
        paging.setHasNext(false);
        paging.setHasPast(false);
        model.setPaging(pg);
        return model;
    }

    /**
     * DM マッピング
     */
    public static List<Thread> message(
            List<DirectMessage> messages, //
            Map<Long, User> users,
            User me,
            Twitter twitter,
            Service service,
            Paging paging) {

        Map<Set<Long>, List<DirectMessage>> threads = new HashMap<>();

        messages.forEach(message -> {
            Set<Long> set = new HashSet<>();
            set.add(message.getRecipientId());
            set.add(message.getSenderId());

            if (!threads.containsKey(set)) {
                threads.put(set, new ArrayList<>());
            }
            // メッセージを追加
            threads.get(set).add(message);
        });

        List<Thread> model = new ArrayList<>();
        threads.forEach((set, ms) -> {
            TwitterThread thread = new TwitterThread(service);

            for (Long id : set) {

                // 存在しない場合は格納
                if (thread.getId() == null) {
                    thread.setId(id);
                }
                // 送信相手の ID を格納
                if (!id.equals(me.getId())) {
                    thread.setId(id);
                }
            }

            thread.setUsers(set.stream()
                    .map(users::get)
                    .collect(toList()));

            thread.setComments(new Pageable<>());
            thread.getComments().setPaging(paging);
            thread.getComments().setEntities(ms.stream()
                    .map(e -> comment(e, users, twitter, service))
                    .collect(toList()));

            // 最新のコメント情報を取得
            Comment lastComment = thread.getComments().getEntities().stream()
                    .max(Comparator.comparing(Comment::getCreateAt)).orElse(null);

            if (lastComment != null) {

                // 「名前: コメント内容」のフォーマットで説明文を作成
                String description = lastComment.getUser().getName()
                        + ": " + lastComment.getText().getDisplayText();

                thread.setLastUpdate(lastComment.getCreateAt());
                thread.setDescription(description);
            }

            model.add(thread);
        });

        return model;
    }

    // ============================================================== //
    // Paging
    // ============================================================== //
    private static void setCursorPaging(
            CursorPaging<Long> pg, PagableResponseList<?> lists) {

        pg.setPrevCursor(lists.getPreviousCursor());
        pg.setHasPrev(lists.hasPrevious());
        pg.setNextCursor(lists.getNextCursor());
        pg.setHasNext(lists.hasNext());
    }

    /**
     * for Twitter Timeline
     */
    public static BorderPaging beTwitterPaging(BorderPaging bp) {
        bp.setMaxInclude(true);
        bp.setSinceInclude(false);
        bp.setIdUnit(1L);
        return bp;
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    /**
     * Get Accounts to reply on this tweet text.
     * 返信対象のアカウントをテキストの一覧を取得
     */
    public static List<String> getRepliesScreenNames(String text) {
        Pattern p = Pattern.compile("(^@\\w+)|(\\s@\\w+)");
        List<String> results = new ArrayList<>();
        String target = text;

        while (true) {
            Matcher m = p.matcher(target);

            // 発見した場合
            if (m.find()) {
                String name = m.group().trim();
                target = target.replaceAll(name, "");
                results.add(name);
                continue;
            }
            break;
        }

        return results
                .stream()
                .distinct()
                .collect(toList());
    }

    /**
     * デフォルトアイコンサイズを取得
     */
    private static String getDefaultIconSize(twitter4j.User user) {
        return user.getProfileImageURLHttps() //
                .replace(TwitterIconSize.Normal.getSuffix(), DEFAULT_ICON_SIZE.getSuffix());
    }

    /**
     * Get rid of media url from status text.
     * Media の URL をテキストから除外
     */
    private static String displayText(Status status) {
        String text = status.getText();
        for (MediaEntity media : status.getMediaEntities()) {
            text = text.replace(media.getURL(), "");
        }
        return text.trim();
    }

    /**
     * Get rid of media url from status text.
     * Media の URL をテキストから除外
     */
    private static String displayText(DirectMessage message) {
        String text = message.getText();
        for (MediaEntity media : message.getMediaEntities()) {
            text = text.replace(media.getURL(), "");
        }
        return text.trim();
    }
}
