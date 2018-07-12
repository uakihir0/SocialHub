package net.socialhub.service.mastodon;

import mastodon4j.entity.Account;
import mastodon4j.entity.Status;
import net.socialhub.logger.Logger;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.utils.MemoSupplier;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MastodonMapper {

    private static Logger logger = Logger.getLogger(MastodonMapper.class);

    /**
     * ユーザーマッピング
     */
    public static User user( //
                             Account account, //
                             Service service) {

        User model = new User(service);

        model.setId(account.getId());
        model.setName(account.getDisplayName());
        model.setImageUrl(account.getAvatarStatic());
        model.setDescription(account.getNote());

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment( //
                                   Status status, //
                                   Service service) {

        SimpleDateFormat format = new SimpleDateFormat();
        Comment model = new Comment(service);

        try {
            model.setId(status.getId());
            model.setComment(status.getContent());
            model.setCreateAt(format.parse(status.getCreatedAt()));
            model.setUser(MemoSupplier.of(() -> user(status.getAccount(), service)));

            return model;

        } catch (ParseException e) {
            logger.error(e);
            return null;
        }
    }
}
