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
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.mastodon.MastodonComment;
import net.socialhub.model.service.addition.mastodon.MastodonUser;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.utils.MapperUtil;
import net.socialhub.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.socialhub.define.ServiceType.Mastodon;

public class MastodonMapper {

    private static Logger logger = Logger.getLogger(MastodonMapper.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            mastodon4j.entity.Mention.class, //
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

        model.setFollowersCount(account.getFollowersCount());
        model.setFollowingsCount(account.getFollowingCount());
        model.setStatusesCount(account.getStatusesCount());
        model.setProtected(account.isLocked());


        // プロフィールページの設定
        AttributedString profile = new AttributedString(account.getUrl());
        model.setProfileUrl(profile);

        String note = removeHtmlElement(account.getNote());
        model.setDescription(new AttributedString(note));

        if ((account.getFields() != null) &&  //
                (account.getFields().length > 0)) {

            model.setFields(new ArrayList<>());
            for (Field field : account.getFields()) {
                AttributedFiled f = new AttributedFiled();

                String value = removeHtmlElement(field.getValue());
                f.setValue(new AttributedString(value));
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
                model.setSpoilerText(new AttributedString(status.getSpoilerText()));
                String text = removeHtmlElement(status.getContent());
                model.setText(new AttributedString(text));

                model.setMedias(medias(status.getMediaAttachments()));
            }
            return model;

        } catch (ParseException e) {
            logger.error(e);
            return null;
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

    private static String removeHtmlElement(String str) {
        String replaced = str //
                .replaceAll("<br>", "\n") //
                .replaceAll("<br />", "\n") //
                .replaceAll("</p><p>", "\n\n");
        return StringUtil.decodeUrl(StringUtil.removeXmlTags(replaced));
    }
}
