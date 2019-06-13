package net.socialhub.service.tumblr;

import com.tumblr.jumblr.types.Blog;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class TumblrMapper {

    /**
     * ユーザーマッピング
     */
    public static User user(com.tumblr.jumblr.types.User user, //
            Map<String, String> iconMap, Service service) {

        User model = new User(service);
        model.setName(user.getName());

        for (Blog blog : user.getBlogs()) {
            System.out.println(blog.getDescription());
            System.out.println(blog.getUrl());
            System.out.println(blog.getUuid());
        }

        return model;
    }

    /**
     * ホスト名を取得 (プライマリを取得)
     * Get primary blog host from url
     */
    public static String getUserIdentify(List<Blog> blogs) {
        for (Blog blog : blogs) {
            if (blog.isPrimary()) {
                return getBlogIdentify(blog);
            }
        }
        return null;
    }

    /**
     * ホスト名を取得
     * Get blog host from url
     */
    public static String getBlogIdentify(Blog blog) {
        try {
            URL url = new URL(blog.getUrl());
            return url.getHost();

        } catch (Exception ignore) {
            return null;
        }
    }

}
