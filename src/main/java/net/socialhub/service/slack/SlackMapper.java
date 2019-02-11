package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.User.Profile;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.SlackUser;

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

        User model = new User(service);
        SlackUser addition = new SlackUser();
        model.setAdditions(new User.UserAdditions());
        model.getAdditions().setSlack(addition);

        model.setId(response.getUser().getId());
        model.setName(response.getUser().getName());
        model.setScreenName(response.getUser().getName());

        // Get users data form
        Profile profile = response.getUser().getProfile();
        model.setIconImageUrl(profile.getImage512());

        addition.setEmail(profile.getEmail());
        addition.setPhone(profile.getPhone());

        if (profile.getTitle() != null && !profile.getTitle().isEmpty()) {
            model.setDescription(profile.getTitle());
            addition.setTitle(profile.getTitle());
        }

        if (profile.getDisplayName() != null && !profile.getDisplayName().isEmpty()) {
            addition.setDisplayName(profile.getDisplayName());
            model.setName(profile.getDisplayName());
        }

        return model;
    }
}
