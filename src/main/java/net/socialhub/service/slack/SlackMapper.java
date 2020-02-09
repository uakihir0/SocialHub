package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.response.bots.BotsInfoResponse;
import com.github.seratch.jslack.api.methods.response.bots.BotsInfoResponse.Bot;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.conversations.ConversationsListResponse;
import com.github.seratch.jslack.api.methods.response.emoji.EmojiListResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.model.File;
import com.github.seratch.jslack.api.model.Message;
import com.github.seratch.jslack.api.model.User.Profile;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.EmojiType;
import net.socialhub.define.EmojiVariationType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.slack.SlackMessageSubType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedItem;
import net.socialhub.model.common.AttributedKind;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.addition.slack.SlackMedia;
import net.socialhub.model.service.addition.slack.SlackTeam;
import net.socialhub.model.service.addition.slack.SlackUser;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.DatePaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.action.AccountAction;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static net.socialhub.define.service.slack.SlackAttributedTypes.simple;
import static net.socialhub.define.service.slack.SlackMessageSubType.BotMessage;

public final class SlackMapper {

    private static Logger logger = Logger.getLogger(SlackMapper.class);

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            com.github.seratch.jslack.api.model.Option.class, //
            com.github.seratch.jslack.api.model.Action.class, //
            com.github.seratch.jslack.api.model.Field.class);

    /**
     * ユーザーマッピング
     */
    public static User user(
            UsersIdentityResponse response, //
            Service service) {

        SlackUser model = new SlackUser(service);

        model.setBot(false);
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

        model.setBot(false);
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

        model.setEmail(AttributedString.plain(profile.getEmail()));
        model.setPhone(AttributedString.plain(profile.getPhone()));

        if ((profile.getTitle() != null) && //
                !profile.getTitle().isEmpty()) {

            model.setDescription(AttributedString.plain((profile.getTitle())));
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
     * ボットマッピング
     */
    public static User bots(
            BotsInfoResponse response, //
            Service service) {

        Bot bot = response.getBot();
        SlackUser model = new SlackUser(service);

        model.setBot(true);
        model.setId(bot.getId());
        model.setName(bot.getName());

        if (bot.getIcons() != null) {
            model.setIconImageUrl(bot.getIcons().getImage72());
            if (StringUtils.isEmpty(model.getIconImageUrl())) {
                model.setIconImageUrl(bot.getIcons().getImage48());
            }
            if (StringUtils.isEmpty(model.getIconImageUrl())) {
                model.setIconImageUrl(bot.getIcons().getImage36());
            }
        }

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            Message message, //
            User user, //
            User userMe, //
            List<ReactionCandidate> candidates, //
            String channel, //
            Service service) {

        SlackComment model = new SlackComment(service);

        model.setId(message.getTs());
        model.setThreadId(message.getThreadTs());
        model.setCreateAt(getDateFromTimeStamp(message.getTs()));
        model.setUser(user);

        // BOT のメッセージである場合
        if (SlackMessageSubType.BotMessage.getCode().equals(message.getSubtype())) {

            for (Attachment attachment : message.getAttachments()) {
                StringBuilder builder = new StringBuilder();

                // プレテキストを設定
                String pretext = attachment.getPretext();
                if ((pretext != null) && !pretext.isEmpty()) {
                    builder.append(pretext);
                    builder.append("\n");
                }

                // テキストの設定
                String text = attachment.getText();
                if ((text != null) && !text.isEmpty()) {
                    builder.append(text);
                }

                model.setText(AttributedString.plain(builder.toString(), simple()));
            }

        } else {

            // 通常のメッセージの場合
            model.setText(AttributedString.plain(message.getText(), simple()));
        }

        // チャンネルはモデルが正
        model.setChannel(channel);
        if (message.getChannel() != null) {
            model.setChannel(message.getChannel());
        }

        // リアクションを追加で格納
        model.setReactions(reactions(message, userMe, candidates));

        // Action オブジェクトを取得
        AccountAction action = service.getAccount().action();

        // メディアは Token が取れた場合にのみ取得
        if (action instanceof SlackAction) {
            String token = ((SlackAction) action).getAuth().getAccessor().getToken();
            model.setMedias(medias(message, token));

        } else {
            model.setMedias(new ArrayList<>());
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
            Message message, //
            User userMe, //
            List<ReactionCandidate> candidates) {

        List<Reaction> models = new ArrayList<>();
        List<com.github.seratch.jslack.api.model.Reaction> reactions = message.getReactions();

        if (reactions != null) {
            reactions.forEach((reaction) -> {
                Reaction model = new Reaction();
                model.setCount((long) reaction.getCount());
                model.setName(reaction.getName());

                // 自分がリアクションしたかどうかを設定
                model.setReacting(reaction.getUsers().contains((String) userMe.getId()));

                // 絵文字や URL を注入
                if (candidates != null) {
                    candidates.stream() //
                            .filter(c -> c.getName().equals(reaction.getName())) //
                            .findFirst().ifPresent(c -> {
                        model.setIconUrl(c.getIconUrl());
                        model.setEmoji(c.getEmoji());
                    });
                }

                models.add(model);
            });
        }

        if (message.getReplyCount() != null) {
            Reaction model = new Reaction();
            model.setCount(message.getReplyCount().longValue());
            model.setName("reply");
            models.add(model);
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
                    .filter(c -> c.getName().equals(value)) //
                    .findFirst().ifPresent(c -> c.addAlias(key));
        });

        return candidates;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            List<Message> messages, //
            Map<String, User> userMap, //
            Map<String, User> botMap, //
            User userMe, //
            List<ReactionCandidate> candidates, //
            String channel, //
            Service service, //
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(messages.stream() //
                .map(e -> {
                    // BOT の投稿かどうかで分岐
                    User user = BotMessage.getCode().equals(e.getSubtype()) ?
                            botMap.get(e.getBotId()) : userMap.get(e.getUser());
                    return comment(e, user, userMe, candidates, channel, service);
                }) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(toList()));

        model.setPaging(DatePaging.fromPaging(paging));
        return model;
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channel(
            ConversationsListResponse channels, //
            Service service) {

        Pageable<Channel> model = new Pageable<>();
        List<Channel> entities = new ArrayList<>();

        channels.getChannels().forEach(c -> {
            Channel entity = new Channel(service);
            entities.add(entity);

            entity.setId(c.getId());
            entity.setName(c.getName());
            entity.setDescription(c.getPurpose().getValue());
            entity.setCreateAt(getFromDateString(c.getCreated()));
        });

        CursorPaging<String> pg = new CursorPaging<>();
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
     * DM スレッドマッピング
     */
    public static List<Thread> thread(
            ConversationsListResponse response, //
            Map<String, List<String>> memberMap, //
            Map<String, Date> historyMap, //
            Map<String, User> accountMap, //
            Service service) {

        return response.getChannels().stream().map((e) -> {

            // アーカイブ済みの場合
            if (e.isArchived()) {
                return null;
            }

            Thread thread = new Thread(service);
            thread.setId(e.getId());
            thread.setUsers(memberMap.get(e.getId())
                    .stream().map(accountMap::get)
                    .filter(Objects::nonNull).collect(toList()));
            thread.setLastUpdate(historyMap.get(e.getId()));

            // 一度も更新されていないスレッドの場合
            if (thread.getLastUpdate() == null) {
                thread.setLastUpdate(getFromDateString(e.getCreated()));
            }

            return thread;
        }).filter(Objects::nonNull)
                .collect(toList());
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

    /**
     * リプライしているユーザーを全て取得
     */
    public static List<String> getReplayUserIds(List<Comment> comments) {

        return comments.stream().map(c -> {

            // 特定のコメントについて含まれるメンションを取得
            return c.getText().getElements().stream() //
                    .filter(a -> a.getKind() == AttributedKind.ACCOUNT)
                    .map(a -> a.getDisplayText().substring(1))
                    .filter(n -> !n.equals("here")) //
                    .filter(n -> !n.equals("all")) //
                    .collect(toList());

        }).flatMap(Collection::stream).distinct() //
                .collect(toList());
    }

    /**
     * リプライしているユーザーの名前を埋める
     */
    public static void setMentionName(List<Comment> comments, Map<String, User> userMap) {
        comments.forEach(c -> {

            // メンション情報を書き換える
            c.getText().getElements().stream() //
                    .filter(a -> a.getKind() == AttributedKind.ACCOUNT)
                    .forEach(elem -> {

                        String userId = elem.getDisplayText().substring(1);
                        if (elem instanceof AttributedItem) {
                            if (userMap.containsKey(userId)) {
                                String display = "@" + userMap.get(userId).getName();
                                ((AttributedItem) elem).setDisplayText(display);
                                ((AttributedItem) elem).setExpandedText(userId);
                            }
                        }
                    });
        });
    }

    public static Date getFromDateString(String date){
        return new Date(Long.parseLong(date) * 1000L);
    }

    public static Date getDateFromTimeStamp(String ts) {

        if (ts != null && !ts.isEmpty()) {
            String unixTime = ts.split("\\.")[0];
            return new Date(Long.parseLong(unixTime) * 1000);
        }
        return null;
    }
}
