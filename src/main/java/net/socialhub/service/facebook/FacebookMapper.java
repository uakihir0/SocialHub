package net.socialhub.service.facebook;

import facebook4j.Message;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.utils.MemoSupplier;

import java.util.function.Supplier;

public class FacebookMapper {

    /**
     * ユーザーマッピング
     */
    public static User user( //
                             facebook4j.User user, //
                             Service service) {

        User model = new User(service);

        model.setId(user.getId());
        model.setName(user.getName());
        model.setDescription(user.getBio());

        model.setIconImageUrl(user.getPicture().getURL().toString());
        model.setCoverImageUrl(user.getCover().getSource());

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment( //
                                   Message message, //
                                   Supplier<facebook4j.User> user, //
                                   Service service) {

        Comment model = new Comment(service);

        model.setId(message.getId());
        model.setComment(message.getMessage());
        model.setCreateAt(message.getCreatedTime());
        model.setUser(MemoSupplier.of(() -> user(user.get(), service)));

        return model;
    }
}
