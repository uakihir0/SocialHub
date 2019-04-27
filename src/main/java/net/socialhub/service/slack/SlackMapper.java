package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.channels.ChannelsHistoryResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.File;
import com.github.seratch.jslack.api.model.Message;
import com.github.seratch.jslack.api.model.User.Profile;
import net.socialhub.define.MediaType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.addition.slack.SlackMedia;
import net.socialhub.model.service.addition.slack.SlackTeam;
import net.socialhub.model.service.addition.slack.SlackUser;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.service.action.AccountAction;
import net.socialhub.utils.MapperUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * ユーザーマッピング
     */
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

    /**
     * コメントマッピング
     */
    public static Comment comment( //
            Message message, //
            User user, //
            Service service) {

        SlackComment model = new SlackComment(service);

        model.setId(message.getTs());
        model.setText(new AttributedString(message.getText()));
        model.setCreateAt(getDateFromTimeStamp(message.getTs()));
        model.setUser(user);

        // Action
        AccountAction action = service.getAccount().action();

        // メディアは Token が取れた場合にのみ取得
        if (action instanceof SlackAction) {
            String token = ((SlackAction) action) //
                    .getAuth().getAccessor().getToken();
            model.setMedias(medias(message, token));
        } else {
            model.setMedias(new ArrayList<>());
        }

        // リアクションを追加で格納
        if ((message.getReactions() != null) //
                && !message.getReactions().isEmpty()) {
            model.setReactions(reactions(message.getReactions()));
        }

        return model;
    }

    /**
     * メディアマッピング
     */
    public static List<Media> medias( //
            Message message, //
            String token) {

        List<Media> medias = new ArrayList<>();

        if (message.getFile() != null) {
            medias.add(media(message.getFile(), token));
        }
        if (message.getFiles() != null) {
            for (File file : message.getFiles()) {
                medias.add(media(file, token));
            }
        }

        return medias;
    }

    /**
     * メディアマッピング
     * see https://api.slack.com/events/message/file_share
     */
    public static Media media( //
            File file, //
            String token) {

        SlackMedia model = new SlackMedia();

        model.setAccessToken(token);
        model.setSourceUrl(file.getUrlPrivate());
        model.setPreviewUrl(file.getUrlPrivate());

        if (file.getMimetype().startsWith("image")) {
            model.setType(MediaType.Image);
        }

        return model;
    }

    /**
     * リアクションマッピング
     */
    public static List<Reaction> reactions( //
            List<com.github.seratch.jslack.api.model.Reaction> reactions) {
        List<Reaction> models = new ArrayList<>();

        if (reactions != null) {
            reactions.forEach((reaction) -> {
                Reaction model = new Reaction();
                model.setCount((long) reaction.getCount());
                model.setName(reaction.getName());
                models.add(model);
            });
        }

        return models;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine( //
            ChannelsHistoryResponse history, //
            Map<String, User> userMap, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(history.getMessages().stream() //
                .map(e -> comment(e, userMap.get(e.getUser()), service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingDatePaging(model, paging));
        return model;
    }

    /**
     * チャンネルマッピング
     */
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
        pg.setCount((long) entities.size());

        // カーソル情報が存在する場合は設定
        if (channels.getResponseMetadata() != null) {
            pg.setNextCursor(channels.getResponseMetadata().getNextCursor());
        }

        model.setEntities(entities);
        model.setPaging(pg);
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

    // ============================================================== //
    // Support
    // ============================================================== //

    private static Date getDateFromTimeStamp(String ts) {

        if (ts != null && !ts.isEmpty()) {
            String unixTime = ts.split("\\.")[0];
            return new Date(Long.valueOf(unixTime) * 1000);
        }
        return null;
    }
}
