package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.channels.ChannelsHistoryResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.emoji.EmojiListResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.File;
import com.github.seratch.jslack.api.model.Message;
import com.github.seratch.jslack.api.model.User.Profile;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.EmojiType;
import net.socialhub.define.EmojiVariationType;
import net.socialhub.define.MediaType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.addition.slack.SlackMedia;
import net.socialhub.model.service.addition.slack.SlackTeam;
import net.socialhub.model.service.addition.slack.SlackUser;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.action.AccountAction;
import net.socialhub.utils.MapperUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class SlackMapper {

    private static Logger logger = Logger.getLogger(SlackMapper.class);

    /** 　J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            com.github.seratch.jslack.api.model.Attachment.class, //
            com.github.seratch.jslack.api.model.Option.class, //
            com.github.seratch.jslack.api.model.Action.class, //
            com.github.seratch.jslack.api.model.Icon.class, //
            com.github.seratch.jslack.api.model.Field.class);

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
        if (StringUtils.isEmpty(model.getIconImageUrl())) {
            model.setIconImageUrl(response.getUser().getImage192());
        }
        if (StringUtils.isEmpty(model.getIconImageUrl())) {
            model.setIconImageUrl(response.getUser().getImage72());
        }
        if (StringUtils.isEmpty(model.getIconImageUrl())) {
            model.setIconImageUrl(response.getUser().getImage48());
        }

        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static User user(
            UsersInfoResponse user, //
            SlackTeam team, //
            Service service) {

        SlackUser model = new SlackUser(service);

        model.setId(user.getUser().getId());
        model.setName(user.getUser().getName());
        model.setScreenName(user.getUser().getName());

        // Get users data form
        Profile profile = user.getUser().getProfile();

        // Get Image (Needs image)
        model.setIconImageUrl(profile.getImage512());
        if (StringUtils.isEmpty(model.getIconImageUrl())) {
            model.setIconImageUrl(profile.getImage192());
        }
        if (StringUtils.isEmpty(model.getIconImageUrl())) {
            model.setIconImageUrl(profile.getImage72());
        }
        if (StringUtils.isEmpty(model.getIconImageUrl())) {
            model.setIconImageUrl(profile.getImage48());
        }

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
    public static Comment comment(
            Message message, //
            User user, //
            List<ReactionCandidate> candidates, //
            String channel, //
            Service service) {

        SlackComment model = new SlackComment(service);

        model.setId(message.getTs());
        model.setText(new AttributedString(message.getText()));
        model.setCreateAt(getDateFromTimeStamp(message.getTs()));
        model.setUser(user);

        // チャンネルはモデルが正
        model.setChannel(channel);
        if (message.getChannel() != null) {
            model.setChannel(message.getChannel());
        }

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
            model.setReactions(reactions(message.getReactions(), candidates));
        }

        return model;
    }

    /**
     * メディアマッピング
     */
    public static List<Media> medias(
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
    public static Media media(
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
    public static List<Reaction> reactions(
            List<com.github.seratch.jslack.api.model.Reaction> reactions, //
            List<ReactionCandidate> candidates) {

        List<Reaction> models = new ArrayList<>();

        if (reactions != null) {
            reactions.forEach((reaction) -> {
                Reaction model = new Reaction();
                model.setCount((long) reaction.getCount());
                model.setName(reaction.getName());

                // 絵文字や URL を注入
                if (candidates != null) {
                    candidates.stream() //
                            .filter((c) -> c.getName().equals(reaction.getName())) //
                            .findFirst().ifPresent((c) -> {
                        model.setIconUrl(c.getIconUrl());
                        model.setEmoji(c.getEmoji());
                    });
                }

                models.add(model);
            });
        }

        return models;
    }

    /**
     * リアクション候補マッピング
     */
    public static List<ReactionCandidate> reactionCandidates(
            EmojiListResponse emojis) {

        List<ReactionCandidate> candidates = new ArrayList<>();
        Map<String, String> alias = new HashMap<>();

        for (EmojiType emoji : EmojiType.values()) {
            ReactionCandidate candidate = new ReactionCandidate();
            candidates.add(candidate);

            candidate.setCategory(emoji.getCategory().getCode());
            candidate.setEmoji(emoji.getEmoji());
            candidate.setName(emoji.getName());
        }

        for (EmojiVariationType emoji : EmojiVariationType.values()) {
            ReactionCandidate candidate = new ReactionCandidate();
            candidates.add(candidate);

            candidate.setCategory(emoji.getCategory().getCode());
            candidate.setEmoji(emoji.getEmoji());
            candidate.setName(emoji.getName());
        }

        emojis.getEmoji().forEach((key, value) -> {

            // エイリアスは一旦スキップして処理
            if (value.startsWith("alias")) {
                alias.put(key, value.split(":")[1]);

            } else {
                ReactionCandidate candidate = new ReactionCandidate();
                candidates.add(candidate);

                candidate.setCategory(EmojiCategoryType.Custom.getCode());
                candidate.setIconUrl(value);
                candidate.setName(key);
            }
        });

        // エイリアスの処理を実行
        // FIXME: エイリアスのエイリアス？
        alias.forEach((key, value) -> {
            candidates.stream() //
                    .filter((c) -> c.getName().equals(value)) //
                    .findFirst().ifPresent((c) -> c.addAlias(key));
        });


        return candidates;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            ChannelsHistoryResponse history, //
            Map<String, User> userMap, //
            List<ReactionCandidate> candidates, //
            String channel, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(history.getMessages().stream() //
                .map(e -> comment(e, userMap.get(e.getUser()), candidates, channel, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingDatePaging(paging));
        return model;
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channel(
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
    public static SlackTeam team(
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
