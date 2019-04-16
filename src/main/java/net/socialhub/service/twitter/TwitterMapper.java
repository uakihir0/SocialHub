package net.socialhub.service.twitter;

import net.socialhub.define.service.TwitterIconSizeEnum;
import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.TwitterUser;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.IndexPaging;
import net.socialhub.utils.MapperUtil;
import net.socialhub.utils.MemoSupplier;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TwitterMapper {

    private static final String HOST = "https://twitter.com/";

    public static TwitterIconSizeEnum DEFAULT_ICON_SIZE = TwitterIconSizeEnum.W200H200;

    /**
     * ユーザーマッピング
     */
    public static User user( //
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
    public static Comment comment( //
            Status status,  //
            Service service) {

        Comment model = new Comment(service);

        model.setId(status.getId());
        model.setComment(status.getText());
        model.setCreateAt(status.getCreatedAt());
        model.setUser(MemoSupplier.of(() -> user(status.getUser(), service)));

        return model;
    }

    /**
     * ページングマッピング
     */
    public static Paging paging( //
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

    public static twitter4j.Paging fromPaging( //
            Paging paging) {

        twitter4j.Paging model = new twitter4j.Paging();
        model.setCount(paging.getCount().intValue());

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
    public static Pageable<Comment> timeLine( //
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

    /**
     * デフォルトアイコンサイズを取得
     */
    private static String getDefaultIconSize(twitter4j.User user) {
        return user.getProfileImageURLHttps() //
                .replace(TwitterIconSizeEnum.Normal.getSuffix(), DEFAULT_ICON_SIZE.getSuffix());
    }
}
