package net.socialhub.service.mastodon;

import mastodon4j.entity.Account;
import mastodon4j.entity.AccountSource;
import mastodon4j.entity.Attachment;
import mastodon4j.entity.Field;
import mastodon4j.entity.Status;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.mastodon.MastodonMediaType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Application;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.mastodon.MastodonComment;
import net.socialhub.model.service.addition.mastodon.MastodonUser;
import net.socialhub.utils.MapperUtil;
import net.socialhub.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MastodonMapper {

    private static Logger logger = Logger.getLogger(MastodonMapper.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * ユーザーマッピング
     */
    public static User user( //
            Account account, //
            Service service) {

        MastodonUser model = new MastodonUser(service);
        AccountSource source = account.getSource();

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

        if (source != null) {
            model.setDescription(new AttributedString(source.getNote()));

            if ((source.getFields() != null) &&  //
                    (source.getFields().length > 0)) {

                model.setFields(new ArrayList<>());
                for (Field field : source.getFields()) {
                    AttributedFiled f = new AttributedFiled();
                    f.setValue(new AttributedString(field.getValue()));
                    f.setName(field.getName());
                    model.getFields().add(f);
                }
            }
        }

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment( //
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

            // リツイートの場合は内部を展開
            if (status.getReblog() != null) {
                model.setSharedComment(comment(status.getReblog(), service));
                model.setMedias(new ArrayList<>());

            } else {
                model.setSpoilerText(new AttributedString(status.getSpoilerText()));
                String text = StringUtil.removeXmlTags(status.getContent());
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
    public static List<Media> medias( //
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
    public static Media media( //
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
    public static Application application( //
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
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine( //
            Status[] statuses, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(Stream.of(statuses).map(e -> comment(e, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingBorderPaging(model, paging));
        return model;
    }
}
