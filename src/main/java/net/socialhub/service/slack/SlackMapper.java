package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

public final class SlackMapper {

    /**
     * ユーザーマッピング
     */
    public static User user(
            UsersIdentityResponse response, //
            Service service) {

        User model = new User(service);

        model.setId(response.getUser().getId());
        model.setName(response.getUser().getName());
        model.setImageUrl(response.getUser().getImage512());

        return model;
    }

    public static User user(
            UsersInfoResponse response, //
            Service service) {

        User model = new User(service);

        model.setId(response.getUser().getId());
        model.setName(response.getUser().getName());
        model.setImageUrl(response.getUser().getProfile().getImage512());

        return model;
    }
}
