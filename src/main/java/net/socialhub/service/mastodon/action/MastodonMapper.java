package net.socialhub.service.mastodon.action;

import mastodon4j.entity.Account;
import mastodon4j.entity.Attachment;
import mastodon4j.entity.Field;
import mastodon4j.entity.Mention;
import mastodon4j.entity.Poll.Option;
import mastodon4j.entity.Status;
import mastodon4j.entity.share.Link;
import mastodon4j.entity.share.Response;
import net.socialhub.core.define.MediaType;
import net.socialhub.core.define.ServiceType;
import net.socialhub.core.model.Application;
import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Emoji;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Poll;
import net.socialhub.core.model.RateLimit;
import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.User;
import net.socialhub.core.model.common.AttributedElement;
import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.common.AttributedItem;
import net.socialhub.core.model.common.AttributedKind;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.common.xml.XmlConvertRule;
import net.socialhub.core.model.support.PollOption;
import net.socialhub.logger.Logger;
import net.socialhub.service.mastodon.define.MastodonMediaType;
import net.socialhub.service.mastodon.define.MastodonNotificationType;
import net.socialhub.service.mastodon.model.MastodonComment;
import net.socialhub.service.mastodon.model.MastodonPaging;
import net.socialhub.service.mastodon.model.MastodonPoll;
import net.socialhub.service.mastodon.model.MastodonUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MastodonMapper {

    private static final Logger logger = Logger.getLogger(MastodonMapper.class);

    /** 時間のパーサーオブジェクト */
    private static final Map<String, SimpleDateFormat> dateParsers = new HashMap<>();

    /** 時間のフォーマットの種類一覧 */
    private static final List<String> DATE_FORMATS = Arrays.asList(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
    );

    private static final XmlConvertRule XML_RULE = xmlConvertRule();

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList(
            mastodon4j.entity.pleroma.PleromaReaction.class,
            mastodon4j.entity.pleroma.PleromaContent.class,
            mastodon4j.entity.pleroma.PleromaStatus.class,
            mastodon4j.entity.History.class,
            mastodon4j.entity.Mention.class,
            mastodon4j.entity.Tag.class);


    // ============================================================== //
    // Single Object Mapper
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static User user(
            Account account,
            Service service) {

        MastodonUser model = new MastodonUser(service);

        model.setId(account.getId());
        model.setName(account.getDisplayName());
        model.setScreenName(account.getAccount());

        // AvatarStatic が PixelFed 等では存在しない
        model.setIconImageUrl(account.getAvatarStatic());
        if (model.getIconImageUrl() == null) {
            model.setIconImageUrl(account.getAvatar());
        }
        // HeaderStatic が PixelFed 等では存在しない
        model.setCoverImageUrl(account.getHeaderStatic());
        if (model.getCoverImageUrl() == null) {
            model.setCoverImageUrl(account.getHeader());
        }

        // 絵文字の追加
        model.setEmojis(emojis(account.getEmojis()));

        // ユーザー説明分の設定
        model.setDescription(AttributedString.xhtml(account.getNote(), XML_RULE));
        model.getDescription().addEmojiElement(model.getEmojis());

        model.setFollowersCount(account.getFollowersCount());
        model.setFollowingsCount(account.getFollowingCount());
        model.setStatusesCount(account.getStatusesCount());
        model.setProtected(account.isLocked());

        // プロフィールページの設定
        model.setProfileUrl(AttributedString.plain(account.getUrl()));

        model.setFields(new ArrayList<>());
        if ((account.getFields() != null) &&
                (account.getFields().length > 0)) {

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
            Status status,
            Service service
    ) {
        MastodonComment model = new MastodonComment(service);

        try {
            model.setId(status.getId());
            model.setUser(user(status.getAccount(), service));
            model.setCreateAt(parseDate(status.getCreatedAt()));
            model.setApplication(application(status.getApplication()));
            model.setPossiblySensitive(status.isSensitive());
            model.setVisibility(status.getVisibility());

            model.setLikeCount(status.getFavouritesCount());
            model.setShareCount(status.getReblogsCount());

            model.setLiked(status.isFavourited());
            model.setShared(status.isReblogged());

            // リプライの数はメンションの数を参照
            model.setReplyCount((status.getMentions() != null) ?
                    status.getMentions().length : 0L);

            // リツイートの場合は内部を展開
            if (status.getReblog() != null) {
                model.setSharedComment(comment(status.getReblog(), service));
                model.setMedias(new ArrayList<>());

            } else {

                // 絵文字の追加
                model.setEmojis(emojis(status.getEmojis()));

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

                // 投票の設定
                model.setPoll(poll(status.getPoll(), service));

                // リクエストホストを記録
                URL url = new URL(service.getApiHost());
                model.setRequesterHost(url.getHost());
            }
            return model;

        } catch (MalformedURLException e) {

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
     * 投票マッピング
     */
    public static Poll poll(
            mastodon4j.entity.Poll poll,
            Service service) {

        if (poll == null) {
            return null;
        }

        MastodonPoll model = new MastodonPoll(service);

        model.setId(poll.getId());
        model.setVoted(poll.isVoted());
        model.setMultiple(poll.isMultiple());
        model.setExpired(poll.isExpired());

        model.setVotesCount(poll.getVotesCount());
        model.setVotersCount(poll.getVotersCount());

        // 通行期限
        if (poll.getExpiresAt() != null) {
            model.setExpireAt(parseDate(poll.getExpiresAt()));
        }

        // 絵文字の追加
        model.setEmojis(emojis(poll.getEmojis()));

        // 投票候補の追加
        List<PollOption> options = new ArrayList<>();
        model.setOptions(options);

        long index = 0;
        for (Option option : poll.getOptions()) {
            PollOption op = new PollOption();
            options.add(op);

            // 投票のインデックスを記録
            op.setIndex(index);
            index += 1;

            op.setTitle(option.getTitle());
            op.setCount(option.getVotesCount());

            // 投票済みかどうかを確認
            if (poll.getOwnVotes() != null) {
                op.setVoted(Stream.of(poll.getOwnVotes())
                        .anyMatch(e -> (e.equals(op.getIndex()))));
            }
        }
        return model;
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

        Notification model = new Notification(service);
        model.setCreateAt(parseDate(notification.getCreatedAt()));
        model.setId(notification.getId());

        MastodonNotificationType type =
                MastodonNotificationType
                        .of(notification.getType());

        // 存在する場合のみ設定
        if (type != null) {
            model.setType(type.getCode());
            if (type.getAction() != null) {
                model.setAction(type.getAction().getCode());
            }
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
    }

    // ============================================================== //
    // List Object Mapper
    // ============================================================== //

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            Status[] statuses,
            Service service,
            Paging paging,
            Link link) {

        return timeLine(
                Arrays.asList(statuses),
                service,
                paging,
                link
        );
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            List<Status> statuses,
            Service service,
            Paging paging,
            Link link) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(statuses.stream().map(e -> comment(e, service))
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed())
                .collect(toList()));

        MastodonPaging mpg = MastodonPaging.fromPaging(paging);
        model.setPaging(withLink(mpg, link));
        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            Account[] accounts,
            Service service,
            Paging paging,
            Link link) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(Stream.of(accounts)
                .map(a -> user(a, service))
                .collect(toList()));

        model.setPaging(withLink(MastodonPaging.fromPaging(paging), link));
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
                .collect(toList()));
        return model;
    }

    /**
     * 通知マッピング
     */
    public static Pageable<Notification> notifications(
            mastodon4j.entity.Notification[] notifications,
            Service service,
            Paging paging,
            Link link) {

        Pageable<Notification> model = new Pageable<>();
        model.setEntities(Stream.of(notifications)
                .map(a -> notification(a, service))
                .collect(toList()));

        model.setPaging(withLink(MastodonPaging.fromPaging(paging), link));
        return model;
    }

    /**
     * 絵文字マッピング
     */
    public static Emoji emoji(
            mastodon4j.entity.Emoji emoji) {

        Emoji model = new Emoji();
        model.addShortCode(emoji.getShortcode());
        model.setImageUrl(emoji.getStaticUrl());
        return model;
    }

    /**
     * 絵文字マッピング
     */
    public static List<Emoji> emojis(
            mastodon4j.entity.Emoji[] emojis) {

        if (emojis == null) {
            return new ArrayList<>();
        }
        return Stream.of(emojis)
                .map(MastodonMapper::emoji)
                .collect(toList());
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
     * add link paging options.
     */
    public static MastodonPaging withLink(MastodonPaging mbp, Link link) {
        if (link != null) {
            mbp.setMinIdInLink(link.getPrevMinId());
            mbp.setMaxIdInLink(link.getNextMaxId());
        }
        return mbp;
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    public static Date parseDate(String str) {
        for (String dateFormat : DATE_FORMATS) {
            SimpleDateFormat dateParser = dateParsers.get(dateFormat);

            if (dateParser == null) {
                dateParser = new SimpleDateFormat(dateFormat);
                dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateParsers.put(dateFormat, dateParser);
            }
            try {
                return dateParser.parse(str);
            } catch (ParseException ignore) {
            }
        }
        throw new IllegalStateException("Unparseable date: " + str);
    }

    public static RateLimit.RateLimitValue rateLimit(Response<?> response) {

        // PixelFed は RateLimit に未対応
        if (response.getRateLimit() != null) {
            mastodon4j.entity.share.RateLimit rateLimit = response.getRateLimit();
            return new RateLimit.RateLimitValue(
                    ServiceType.Mastodon,
                    rateLimit.getLimit(),
                    rateLimit.getRemaining(),
                    rateLimit.getReset());
        }
        return null;
    }
}
