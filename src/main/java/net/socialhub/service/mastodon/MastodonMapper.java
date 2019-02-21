package net.socialhub.service.mastodon;

import mastodon4j.entity.Account;
import mastodon4j.entity.Status;
import net.socialhub.logger.Logger;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.MastodonUser;
import net.socialhub.model.common.AttributedString;
import net.socialhub.utils.MemoSupplier;
import net.socialhub.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MastodonMapper {

    private static Logger logger = Logger.getLogger(MastodonMapper.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * ユーザーマッピング
     */
    public static User user(
            Account account, //
            Service service) {

        MastodonUser model = new MastodonUser(service);

        model.setId(account.getId());
        model.setName(account.getDisplayName());
        model.setIconImageUrl(account.getUserName());
        model.setDescription(new AttributedString(account.getNote()));

        model.setIconImageUrl(account.getAvatarStatic());
        model.setCoverImageUrl(account.getHeaderStatic());

        model.setFollowersCount(account.getFollowersCount());
        model.setFollowingsCount(account.getFollowingCount());
        model.setStatusesCount(account.getStatusesCount());

        model.setProtected(account.isLocked());

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            Status status, //
            Service service) {

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Comment model = new Comment(service);

        try {
            model.setId(status.getId());
            model.setComment(StringUtil.removeXmlTags(status.getContent()));
            model.setCreateAt(format.parse(status.getCreatedAt()));
            model.setUser(MemoSupplier.of(() -> user(status.getAccount(), service)));

            return model;

        } catch (ParseException e) {
            logger.error(e);
            return null;
        }
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            Status[] statuses, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(Stream.of(statuses).map(e -> comment(e, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        if (paging != null) {
            model.setPaging(paging);

        } else {
            // make paging info from response
            Paging pg = new Paging();
            int count = statuses.length;

            pg.setCount((long) count);
            pg.setMaxId(model.getEntities().get(0).getNumberId());
            pg.setSinceId(model.getEntities().get(count - 1).getNumberId());
        }

        return model;
    }
}
