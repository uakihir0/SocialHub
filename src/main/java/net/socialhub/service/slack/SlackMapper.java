package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.User.Profile;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.SlackTeam;
import net.socialhub.model.service.addition.SlackUser;
import net.socialhub.model.service.paging.CursorPaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class SlackMapper {

    private static Logger logger = Logger.getLogger(SlackMapper.class);

    /**
     * ユーザーマッピング
     */
    public static User user( //
            UsersIdentityResponse response, //
            Service service) {

        User model = new User(service);

        model.setId(response.getUser().getId());
        model.setName(response.getUser().getName());
        model.setIconImageUrl(response.getUser().getImage512());

        return model;
    }

    public static User user( //
            UsersInfoResponse user, //
            SlackTeam team, //
            Service service) {

        SlackUser model = new SlackUser(service);

        model.setId(user.getUser().getId());
        model.setName(user.getUser().getName());
        model.setScreenName(user.getUser().getName());

        // Get users data form
        Profile profile = user.getUser().getProfile();
        model.setIconImageUrl(profile.getImage512());

        model.setEmail(new AttributedString(profile.getEmail()));
        model.setPhone(new AttributedString(profile.getPhone()));

        if ((profile.getTitle() != null) && //
                !profile.getTitle().isEmpty()) {

            model.setDescription(new AttributedString(profile.getTitle()));
            model.setTitle(profile.getTitle());
        }

        if ((profile.getDisplayName() != null) && //
                !profile.getDisplayName().isEmpty()) {

            model.setDisplayName(profile.getDisplayName());
            model.setName(profile.getDisplayName());
        }

        model.setTeam(team);
        return model;
    }

    public static Pageable<Channel> channel( //
            ChannelsListResponse channels, //
            Service service) {

        Pageable<Channel> model = new Pageable<>();
        List<Channel> entities = new ArrayList<>();

        channels.getChannels().forEach((c) -> {
            Channel entity = new Channel(service);
            entities.add(entity);

            entity.setId(c.getId());
            entity.setName(c.getName());
            entity.setDescription(c.getPurpose().getValue());
            entity.setCreateAt(new Date(c.getCreated() * 1000));
        });

        CursorPaging pg = new CursorPaging();
        pg.setNextCursor(channels.getResponseMetadata().getNextCursor());
        pg.setCount((long) entities.size());

        return model;
    }

    /**
     * チーム情報マッチング
     */
    public static SlackTeam team( //
            TeamInfoResponse response) {

        // エラーの場合は空を返却
        if (!response.isOk()) {
            logger.debug("ERROR (in team.info)");
            return new SlackTeam();
        }

        SlackTeam team = new SlackTeam();
        team.setId(response.getTeam().getId());
        team.setName(response.getTeam().getName());
        team.setDomain(response.getTeam().getDomain());
        team.setIconImageUrl(response.getTeam().getIcon().getImage132());

        return team;
    }
}
