package net.socialhub.service.mastodon;

import mastodon4j.entity.Account;
import mastodon4j.entity.Attachment;
import mastodon4j.entity.Field;
import mastodon4j.entity.Mention;
import mastodon4j.entity.Status;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.mastodon.MastodonMediaType;
import net.socialhub.define.service.mastodon.MastodonNotificationType;
import net.socialhub.define.service.mastodon.MastodonReactionType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedItem;
import net.socialhub.model.common.AttributedKind;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.common.xml.XmlConvertRule;
import net.socialhub.model.service.Application;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Emoji;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Notification;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.mastodon.MastodonComment;
import net.socialhub.model.service.addition.mastodon.MastodonUser;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.support.ReactionCandidate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MastodonMapper {

    private static Logger logger = Logger.getLogger(MastodonMapper.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final XmlConvertRule XML_RULE = xmlConvertRule();

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            mastodon4j.entity.History.class, //
            mastodon4j.entity.Mention.class, //
            mastodon4j.entity.Tag.class);

    // ============================================================== //
    // Single Object Mapper
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static User user(
            Account account, //
            Service service) {

        MastodonUser model = new MastodonUser(service);

        model.setId(account.getId());
        model.setName(account.getDisplayName());
        model.setScreenName(account.getAccount());
        model.setIconImageUrl(account.getAvatarStatic());
        model.setCoverImageUrl(account.getHeaderStatic());

        // 絵文字の追加
        model.setEmojis(new ArrayList<>());
        if (account.getEmojis() != null) {
            model.getEmojis().addAll(
                    Stream.of(account.getEmojis())
                            .map(MastodonMapper::emoji)
                            .collect(Collectors.toList()));
        }

        // ユーザー説明分の設定
        model.setDescription(AttributedString.xhtml(account.getNote(), XML_RULE));
        model.getDescription().addEmojiElement(model.getEmojis());

        model.setFollowersCount(account.getFollowersCount());
        model.setFollowingsCount(account.getFollowingCount());
        model.setStatusesCount(account.getStatusesCount());
        model.setProtected(account.isLocked());

        // プロフィールページの設定
        model.setProfileUrl(AttributedString.plain(account.getUrl()));

        if ((account.getFields() != null) &&  //
                (account.getFields().length > 0)) {

            model.setFields(new ArrayList<>());
            for (Field field : account.getFields()) {
                AttributedFiled f = new AttributedFiled();

                f.setValue(AttributedString.xhtml(field.getValue(), XML_RULE));
                f.getValue().addEmojiElement(model.getEmojis());
                f.setName(field.getName());
                model.getFields().add(f);
            }
        }

        return model;
    }

    /**
     * ユーザー関係
     */
    public static Relationship relationship(
            mastodon4j.entity.Relationship relationship) {

        Relationship model = new Relationship();
        model.setFollowed(relationship.isFollowedBy());
        model.setFollowing(relationship.isFollowing());
        model.setBlocking(relationship.isBlocking());
        model.setMuting(relationship.isMuting());
        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            Status status, //
            Service service) {

        MastodonComment model = new MastodonComment(service);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            model.setId(status.getId());
            model.setUser(user(status.getAccount(), service));
            model.setCreateAt(format.parse(status.getCreatedAt()));
            model.setApplication(application(status.getApplication()));
            model.setPossiblySensitive(status.isSensitive());
            model.setVisibility(status.getVisibility());

            model.setLikeCount(status.getFavouritesCount());
            model.setShareCount(status.getReblogsCount());

            model.setLiked(status.isFavourited());
            model.setShared(status.isReblogged());

            // リツイートの場合は内部を展開
            if (status.getReblog() != null) {
                model.setSharedComment(comment(status.getReblog(), service));
                model.setMedias(new ArrayList<>());

            } else {

                // 絵文字の追加
                model.setEmojis(new ArrayList<>());
                if (status.getEmojis() != null) {
                    model.getEmojis().addAll(
                            Stream.of(status.getEmojis())
                                    .map(MastodonMapper::emoji)
                                    .collect(Collectors.toList()));
                }

                // 注釈の設定
                model.setSpoilerText(AttributedString.plain(status.getSpoilerText()));
                model.getSpoilerText().addEmojiElement(model.getEmojis());

                // 本文の設定
                model.setText(AttributedString.xhtml(status.getContent(), XML_RULE));
                model.getText().addEmojiElement(model.getEmojis());

                // メンションの設定
                if (status.getMentions() != null) {
                    for (Mention mention : status.getMentions()) {
                        for (AttributedElement elem : model.getText().getElements()) {

                            // 要素の種類が ACCOUNT の場合
                            if ((elem.getKind() == AttributedKind.ACCOUNT)
                                    && (elem instanceof AttributedItem)) {

                                // アカウントの ID をフラグメントで埋め込み参照できるように
                                if (elem.getExpandedText().equals(mention.getUrl())) {
                                    String url = mention.getUrl() + "#" + mention.getId();
                                    ((AttributedItem) elem).setExpandedText(url);
                                }
                            }
                        }
                    }
                }

                // メディアの設定
                model.setMedias(medias(status.getMediaAttachments()));
            }
            return model;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * メディアマッピング
     */
    public static List<Media> medias(
            Attachment[] attachments) {

        List<Media> medias = new ArrayList<>();

        if (attachments != null) {
            for (Attachment attachment : attachments) {
                medias.add(media(attachment));
            }
        }

        return medias;
    }

    /**
     * メディアマッピング
     */
    public static Media media(
            Attachment attachment) {

        Media media = new Media();
        media.setSourceUrl(attachment.getUrl());
        media.setPreviewUrl(attachment.getPreviewUrl());

        MastodonMediaType type = MastodonMediaType.of(attachment.getType());

        if (type == MastodonMediaType.Image) {
            media.setType(MediaType.Image);
            return media;
        }
        if (type == MastodonMediaType.Video) {
            media.setType(MediaType.Movie);
            return media;
        }
        return media;
    }

    /**
     * アプリケーションマッピング
     */
    public static Application application(
            mastodon4j.entity.Application application) {
        if (application == null) {
            return null;
        }

        Application app = new Application();
        app.setName(application.getName());
        app.setWebsite(application.getWebsite());
        return app;
    }

    /**
     * チャンネルマッピング
     */
    public static Channel channel(
            mastodon4j.entity.List list,
            Service service) {

        Channel channel = new Channel(service);

        channel.setId(list.getId());
        channel.setName(list.getTitle());
        channel.setPublic(false);
        return channel;
    }

    /**
     * 通知マッピング
     */
    public static Notification notification(
            mastodon4j.entity.Notification notification,
            Service service) {

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Notification model = new Notification(service);
            model.setCreateAt(format.parse(notification.getCreatedAt()));
            model.setId(notification.getId());

            MastodonNotificationType type =
                    MastodonNotificationType
                            .of(notification.getType());

            // 存在する場合のみ設定
            if (type != null) {
                model.setType(type.getType().getCode());
            }

            // ステータス情報
            if (notification.getStatus() != null) {
                model.setComments(Collections.singletonList(
                        comment(notification.getStatus(), service)));
            }

            // ユーザー情報
            if (notification.getAccount() != null) {
                model.setUsers(Collections.singletonList(
                        user(notification.getAccount(), service)));
            }
            return model;

        } catch (ParseException e) {
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
        like.setName(MastodonReactionType.Favorite.getCode().get(0));
        like.addAlias(MastodonReactionType.Favorite.getCode());
        candidates.add(like);

        ReactionCandidate share = new ReactionCandidate();
        share.setCategory(EmojiCategoryType.Activities.getCode());
        share.setName(MastodonReactionType.Reblog.getCode().get(0));
        share.addAlias(MastodonReactionType.Reblog.getCode());
        candidates.add(share);

        return candidates;
    }

    // ============================================================== //
    // List Object Mapper
    // ============================================================== //

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            Status[] statuses, //
            Service service, //
            Paging paging) {

        return timeLine(Arrays.asList(statuses),
                service,
                paging);
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            List<Status> statuses, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(statuses.stream().map(e -> comment(e, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        model.setPaging(beMastodonPaging(BorderPaging.fromPaging(paging)));
        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            Account[] accounts,
            Service service,
            Paging paging) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(Stream.of(accounts)
                .map(a -> user(a, service))
                .collect(Collectors.toList()));

        model.setPaging(beMastodonPaging(BorderPaging.fromPaging(paging)));
        return model;
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channels(
            mastodon4j.entity.List[] lists,
            Service service) {

        Pageable<Channel> model = new Pageable<>();
        model.setEntities(Stream.of(lists)
                .map(e -> channel(e, service))
                .collect(Collectors.toList()));
        return model;
    }

    /**
     * 通知マッピング
     */
    public static Pageable<Notification> notifications(
            mastodon4j.entity.Notification[] notifications,
            Service service,
            Paging paging) {

        Pageable<Notification> model = new Pageable<>();
        model.setEntities(Stream.of(notifications)
                .map(a -> notification(a, service))
                .collect(Collectors.toList()));

        model.setPaging(beMastodonPaging(BorderPaging.fromPaging(paging)));
        return model;
    }

    /**
     * 絵文字マッピング
     */
    public static Emoji emoji(
            mastodon4j.entity.Emoji emoji) {

        Emoji model = new Emoji();
        model.setCode(emoji.getShortcode());
        model.setUrl(emoji.getStaticUrl());
        return model;
    }

    /**
     * XHtml 変換規則
     */
    public static XmlConvertRule xmlConvertRule() {
        XmlConvertRule rule = new XmlConvertRule();
        rule.setP("\n\n");
        return rule;
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    /**
     * for Mastodon Timeline
     */
    public static BorderPaging beMastodonPaging(BorderPaging bp) {
        bp.setMaxInclude(false);
        bp.setSinceInclude(false);
        bp.setIdUnit(4L);
        return bp;
    }
}
