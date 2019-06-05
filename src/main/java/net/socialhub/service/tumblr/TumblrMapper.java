package net.socialhub.service.tumblr;

import com.tumblr.jumblr.types.Blog;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

public class TumblrMapper {

    /**
     * ユーザーマッピング
     */
    public static User user(
            com.tumblr.jumblr.types.User user, //
            Service service) {

        User model = new User(service);
        model.setName(user.getName());

        for (Blog blog : user.getBlogs()) {
            System.out.println(blog.getDescription());
        }

        return model;
    }
}
