package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.User.Profile;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.SlackUser;
import net.socialhub.model.service.common.AttributedString;

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
        model.setIconImageUrl(response.getUser().getImage512());

        return model;
    }

    public static User user(
            UsersInfoResponse response, //
            Service service) {

        SlackUser model = new SlackUser(service);

        model.setId(response.getUser().getId());
        model.setName(response.getUser().getName());
        model.setScreenName(response.getUser().getName());

        // Get users data form
        Profile profile = response.getUser().getProfile();
        model.setIconImageUrl(profile.getImage512());

        model.setEmail(profile.getEmail());
        model.setPhone(profile.getPhone());

        if ((profile.getTitle() != null) &&
                !profile.getTitle().isEmpty()) {

            model.setDescription(new AttributedString(profile.getTitle()));
            model.setTitle(profile.getTitle());
        }

        if ((profile.getDisplayName() != null) &&
                !profile.getDisplayName().isEmpty()) {

            model.setDisplayName(profile.getDisplayName());
            model.setName(profile.getDisplayName());
        }

        return model;
    }
}
