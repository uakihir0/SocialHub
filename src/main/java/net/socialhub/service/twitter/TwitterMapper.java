package net.socialhub.service.twitter;

import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.twitter.TwitterIconSize;
import net.socialhub.define.service.twitter.TwitterMediaType;
import net.socialhub.define.service.twitter.TwitterReactionType;
import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.MiniBlogComment;
import net.socialhub.model.service.addition.twitter.TwitterMedia;
import net.socialhub.model.service.addition.twitter.TwitterUser;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.IndexPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.utils.MapperUtil;
import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitterMapper {

    private static final String HOST = "https://twitter.com/";

    public static TwitterIconSize DEFAULT_ICON_SIZE = TwitterIconSize.W200H200;

    /**
     * ユーザーマッピング
     */
    public static User user(
            twitter4j.User user, //
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
            AttributedString url = new AttributedString(user.getURL());
            model.setUrl(url);

            URLEntity entity = user.getURLEntity();
            for (AttributedElement elem : url.getAttribute()) {
                if (elem.getText().equals(entity.getText())) {
                    elem.setDisplayText(entity.getDisplayURL());
                    elem.setExpandedText(entity.getExpandedURL());
                }
            }
        }

        // プロフィールページの設定
        String profile = HOST + user.getScreenName();
        model.setProfileUrl(new AttributedString(profile));

        // 説明文の情報を設定
        AttributedString desc = new AttributedString(user.getDescription());
        model.setDescription(desc);

        // URL の DisplayURL ExpandedURL を設定
        for (URLEntity entity : user.getDescriptionURLEntities()) {
            for (AttributedElement elem : desc.getAttribute()) {
                if (elem.getText().equals(entity.getText())) {
                    elem.setDisplayText(entity.getDisplayURL());
                    elem.setExpandedText(entity.getExpandedURL());
                }
            }
        }

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            Status status,  //
            Service service) {

        MiniBlogComment model = new MiniBlogComment(service);

        model.setId(status.getId());
        model.setCreateAt(status.getCreatedAt());
        model.setUser(user(status.getUser(), service));
        model.setPossiblySensitive(status.isPossiblySensitive());
        model.setApplication(application(status.getSource()));

        model.setLikeCount((long) status.getFavoriteCount());
        model.setShareCount((long) status.getRetweetCount());

        // リツイートの場合内部を展開
        if (status.isRetweet()) {

            model.setSharedComment(comment(status.getRetweetedStatus(), service));
            model.setMedias(new ArrayList<>());

        } else {

            // 引用リツイートの場合はここで展開
            if (status.getQuotedStatus() != null) {
                model.setSharedComment(comment(status.getQuotedStatus(), service));
            }

            AttributedString text = new AttributedString(status.getText());
            model.setText(text);

            // URL の DisplayURL ExpandedURL を設定
            for (URLEntity entity : status.getURLEntities()) {
                for (AttributedElement elem : text.getAttribute()) {
                    if (elem.getText().equals(entity.getText())) {
                        elem.setDisplayText(entity.getDisplayURL());
                        elem.setExpandedText(entity.getExpandedURL());
                    }
                }
            }

            // メディア情報を取得時に展開
            model.setMedias(medias(status.getMediaEntities()));
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
            return model;
        }
    }

    public static twitter4j.Paging fromPaging(
            Paging paging) {

        twitter4j.Paging model = new twitter4j.Paging();

        if (paging.getCount() != null) {
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
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingBorderPaging(model, paging));
        return model;
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    /**
     * デフォルトアイコンサイズを取得
     */
    private static String getDefaultIconSize(twitter4j.User user) {
        return user.getProfileImageURLHttps() //
                .replace(TwitterIconSize.Normal.getSuffix(), DEFAULT_ICON_SIZE.getSuffix());
    }

}
