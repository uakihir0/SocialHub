package net.socialhub.service.mastodon;

import mastodon4j.entity.Account;
import mastodon4j.entity.Attachment;
import mastodon4j.entity.Field;
import mastodon4j.entity.Status;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.mastodon.MastodonMediaType;
import net.socialhub.define.service.mastodon.MastodonReactionType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.common.xml.XmlConvertRule;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.mastodon.MastodonComment;
import net.socialhub.model.service.addition.mastodon.MastodonUser;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.utils.MapperUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.socialhub.define.ServiceType.Mastodon;

public class MastodonMapper {

    private static Logger logger = Logger.getLogger(MastodonMapper.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final XmlConvertRule XML_RULE = xmlConvertRule();

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            mastodon4j.entity.History.class, //
            mastodon4j.entity.Mention.class, //
            mastodon4j.entity.Emoji.class, //
            mastodon4j.entity.Tag.class);

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
        model.setIconImageUrl(account.getUserName());

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

        model.setDescription(AttributedString.xhtml(account.getNote(), XML_RULE));
        model.setFollowersCount(account.getFollowersCount());
        model.setFollowingsCount(account.getFollowingCount());
        model.setStatusesCount(account.getStatusesCount());
        model.setProtected(account.isLocked());

        // プロフィールページの設定
        AttributedString profile = AttributedString.plain(account.getUrl());
        profile.addEmojiElement(model.getEmojis());
        model.setProfileUrl(profile);

        if ((account.getFields() != null) &&  //
                (account.getFields().length > 0)) {

            model.setFields(new ArrayList<>());
            for (Field field : account.getFields()) {
                AttributedFiled f = new AttributedFiled();

                f.setValue(AttributedString.xhtml(field.getValue(), XML_RULE));
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
        switch (MastodonMediaType.of(attachment.getType())) {

            case Image: {
                media.setType(MediaType.Image);
                break;
            }
            case Video: {
                media.setType(MediaType.Movie);
                break;
            }
        }
        media.setSourceUrl(attachment.getUrl());
        media.setPreviewUrl(attachment.getPreviewUrl());
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

        model.setPaging(MapperUtil.mappingBorderPaging(paging, Mastodon));
        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            Account[] accounts, //
            Service service,
            Paging paging) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(Stream.of(accounts) //
                .map((a) -> user(a, service)) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingBorderPaging(paging, Mastodon));
        return model;
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channels(
            mastodon4j.entity.List[] lists, //
            Service service) {

        Pageable<Channel> model = new Pageable<>();
        model.setEntities(Stream.of(lists).map(e -> channel(e, service)) //
                .collect(Collectors.toList()));
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
}
