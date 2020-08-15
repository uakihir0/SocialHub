package net.socialhub.service.misskey;

import misskey4j.entity.Choice;
import misskey4j.entity.Field;
import misskey4j.entity.File;
import misskey4j.entity.Message;
import misskey4j.entity.Note;
import misskey4j.entity.Relation;
import net.socialhub.define.EmojiType;
import net.socialhub.define.EmojiVariationType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.misskey.MisskeyNotificationType;
import net.socialhub.define.service.misskey.MisskeyVisibility;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Emoji;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Notification;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Poll;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.misskey.MisskeyComment;
import net.socialhub.model.service.addition.misskey.MisskeyNotification;
import net.socialhub.model.service.addition.misskey.MisskeyPaging;
import net.socialhub.model.service.addition.misskey.MisskeyPoll;
import net.socialhub.model.service.addition.misskey.MisskeyThread;
import net.socialhub.model.service.addition.misskey.MisskeyUser;
import net.socialhub.model.service.support.Color;
import net.socialhub.model.service.support.PollOption;
import net.socialhub.model.service.support.ReactionCandidate;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class MisskeyMapper {

    private static Logger logger = Logger.getLogger(MisskeyMapper.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList(
            misskey4j.entity.Choice.class);

    /** 時間のパーサーオブジェクト */
    private static SimpleDateFormat dateParser = null;

    // ============================================================== //
    // Single Object Mapper
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static User user(
            misskey4j.entity.User account,
            String host,
            Service service) {

        MisskeyUser model = new MisskeyUser(service);

        model.setId(account.getId());
        model.setName(account.getName());
        model.setScreenName(account.getUsername());
        model.setIconImageUrl(account.getAvatarUrl());
        model.setCoverImageUrl(account.getBannerUrl());
        model.setLocation(account.getLocation());

        model.setHost(account.getHost());
        if (account.getHost() == null) {
            model.setHost(host);
        }

        model.setPinnedComments(emptyList());
        if (account.getPinnedNotes() != null) {
            model.setPinnedComments(account.getPinnedNotes().stream()
                    .map(c -> comment(c, host, service)).collect(toList()));
        }

        // 絵文字の追加
        model.setEmojis(emojis(account.getEmojis()));

        // 説明文字列がない場合はシンプルオブジェクトと判定
        if (account.getDescription() == null) {
            model.setSimple(true);
        }

        // ユーザー説明分の設定
        model.setDescription(AttributedString.plain(account.getDescription()));
        model.getDescription().addEmojiElement(model.getEmojis());

        model.setFollowersCount(account.getFollowersCount());
        model.setFollowingsCount(account.getFollowingCount());
        model.setStatusesCount(account.getNotesCount());

        if (account.getLocked() != null) {
            model.setProtected(account.getLocked());
        }
        if (account.getBot() != null) {
            model.setBot(account.getBot());
        }
        if (account.getCat() != null) {
            model.setCat(account.getCat());
        }

        // 色設定
        if (account.getAvatarColor() != null) {
            model.setAvatarColor(color(account.getAvatarColor()));
        }
        if (account.getBannerColor() != null) {
            model.setBannerColor(color(account.getBannerColor()));
        }

        // プロフィールページの設定
        model.setProfileUrl(AttributedString.plain(account.getUrl()));

        if ((account.getFields() != null) &&  //
                (account.getFields().size() > 0)) {

            model.setFields(new ArrayList<>());
            for (Field field : account.getFields()) {
                AttributedFiled f = new AttributedFiled();

                f.setValue(AttributedString.plain(field.getValue()));
                f.getValue().addEmojiElement(model.getEmojis());
                f.setName(field.getName());
                model.getFields().add(f);
            }
        }

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            Note note,
            String host,
            Service service) {

        MisskeyComment model = new MisskeyComment(service);

        try {
            model.setId(note.getId());
            model.setUser(user(note.getUser(), host, service));
            model.setCreateAt(getDateParser().parse(note.getCreatedAt()));
            model.setShareCount(note.getRenoteCount());
            model.setReplyCount(note.getRepliesCount());
            model.setVisibility(note.getVisibility());

            // 絵文字の追加
            model.setEmojis(emojis(note.getEmojis()));

            // Misskey ではファイル単位でセンシティブかを判断
            model.setPossiblySensitive(note.getFiles()
                    .stream().anyMatch(File::getSensitive));

            // リツイートの場合は内部を展開
            if (note.getRenote() != null) {
                model.setSharedComment(comment(
                        note.getRenote(), host, service));
            }

            // 注釈の設定
            if (note.getCw() != null) {
                model.setSpoilerText(AttributedString.plain(note.getCw()));
                model.getSpoilerText().addEmojiElement(model.getEmojis());
            }

            // 本文の設定
            model.setText(AttributedString.plain(note.getText()));
            model.getText().addEmojiElement(model.getEmojis());

            // メディアの設定
            model.setMedias(medias(note.getFiles()));

            // 投票の設定
            model.setPoll(poll(note, note.getPoll(), service));

            // リアクションの設定
            model.setReactions(reactions(
                    note.getReactions(),
                    note.getEmojis(),
                    note.getMyReaction()));

            // リクエストホストを記録
            URL url = new URL(service.getApiHost());
            model.setRequesterHost(url.getHost());

            return model;

        } catch (ParseException | MalformedURLException e) {

            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * 通知メンションマッピング
     */
    public static Comment mention(
            misskey4j.entity.Notification notification,
            String host,
            Service service) {

        Comment comment = comment(notification.getNote(), host, service);
        ((MisskeyComment) comment).setPagingId(notification.getId());
        return comment;
    }

    /**
     * ユーザー関係
     */
    public static Relationship relationship(
            Relation relation) {

        Relationship model = new Relationship();
        model.setFollowed(relation.getFollowed());
        model.setFollowing(relation.getFollowing());
        model.setBlocking(relation.getBlocking());
        model.setMuting(relation.getMuted());
        return model;
    }

    /**
     * 絵文字マッピング
     */
    public static Emoji emoji(
            misskey4j.entity.Emoji emoji) {

        Emoji model = new Emoji();
        model.setCode(emoji.getName());
        model.setUrl(emoji.getUrl());
        return model;
    }

    /**
     * 絵文字マッピング
     */
    public static Emoji emoji(
            ReactionCandidate candidate) {

        Emoji model = new Emoji();
        model.setCode(candidate.getName().replaceAll(":", ""));
        model.setUrl(candidate.getIconUrl());
        return model;
    }

    /**
     * メディアマッピング
     */
    public static Media media(
            File file) {

        // 画像の場合
        if (file.getType().startsWith("image/")) {

            Media media = new Media();
            media.setType(MediaType.Image);
            media.setSourceUrl(file.getUrl());

            // サムネイル画像が設定されている場合
            if (file.getThumbnailUrl() != null) {
                media.setPreviewUrl(file.getThumbnailUrl());
            } else {
                media.setPreviewUrl(file.getUrl());
            }
            return media;
        }

        // 動画の場合
        if (file.getType().startsWith("video/")) {

            Media media = new Media();
            media.setType(MediaType.Movie);
            media.setSourceUrl(file.getUrl());

            // サムネイル画像が設定されている場合
            if (file.getThumbnailUrl() != null) {
                media.setPreviewUrl(file.getThumbnailUrl());
            }
            return media;
        }
        return null;
    }

    /**
     * チャンネルマッピング
     */
    public static Channel channel(
            misskey4j.entity.List list,
            Service service) {
        try {

            Channel model = new Channel(service);

            model.setId(list.getId());
            model.setName(list.getName());
            model.setCreateAt(getDateParser().parse(list.getCreatedAt()));
            model.setPublic(false);

            return model;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * 通知マッピング
     */
    public static Notification notification(
            misskey4j.entity.Notification notification,
            List<ReactionCandidate> reactions,
            String host,
            Service service) {

        try {
            MisskeyNotification model = new MisskeyNotification(service);
            model.setCreateAt(getDateParser().parse(notification.getCreatedAt()));
            model.setReaction(notification.getReaction());
            model.setId(notification.getId());

            reactions.stream()
                    .filter(e -> e.getAllNames().contains(notification.getReaction()))
                    .findFirst().ifPresent(c -> model.setIconUrl(c.getIconUrl()));

            MisskeyNotificationType type =
                    MisskeyNotificationType.of(notification.getType());

            // 存在する場合のみ設定
            if (type != null) {
                model.setType(type.getCode());
            }

            // ステータス情報
            if (notification.getNote() != null) {
                model.setComments(Collections.singletonList(
                        comment(notification.getNote(), host, service)));
            }

            // ユーザー情報
            if (notification.getUser() != null) {
                model.setUsers(Collections.singletonList(
                        user(notification.getUser(), host, service)));
            }
            return model;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * メッセージスレッドマッピング
     */
    public static Thread thread(
            Message message,
            String host,
            User me,
            Map<String, User> userMap,
            Service service) {

        try {
            // 「名前: コメント内容」のフォーマットで説明文を作成
            String description = message.getUser().getName()
                    + ": " + message.getText();

            MisskeyThread thread = new MisskeyThread(service);
            thread.setLastUpdate(getDateParser().parse(message.getCreatedAt()));
            thread.setDescription(description);

            // ユーザー個人チャットの場合
            if (message.getUser() != null) {
                User user = user(message.getUser(), host, service);
                thread.setUsers(singletonList(user));

                thread.setId(message.getUserId().equals(me.getId())
                        ? message.getRecipientId()
                        : message.getUserId());
                thread.setGroup(false);
            }

            // グループ向けチャットの場合
            if (message.getGroup() != null) {
                thread.setUsers(message.getGroup().getUserIds().stream()
                        .map(userMap::get).filter(Objects::nonNull).collect(toList()));
                thread.setId(message.getGroupId());
                thread.setGroup(true);
            }

            // 自分が含まれていない場合は追加
            if (thread.getUsers().stream()
                    .noneMatch(e -> e.getId().equals(me.getId()))) {
                List<User> users = new ArrayList<>(thread.getUsers());
                thread.setUsers(users);
                users.add(me);
            }

            return thread;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * メッセージマッピング
     */
    public static Comment message(
            Message message,
            List<Emoji> emojis,
            String host,
            Service service) {

        MisskeyComment model = new MisskeyComment(service);

        try {
            model.setId(message.getId());
            model.setUser(user(message.getUser(), host, service));
            model.setCreateAt(getDateParser().parse(message.getCreatedAt()));
            model.setVisibility(MisskeyVisibility.Message);
            model.setReactions(new ArrayList<>());
            model.setShareCount(0L);
            model.setReplyCount(0L);
            model.setDirectMessage(true);

            // 本文の設定
            model.setText(AttributedString.plain(message.getText()));
            model.getText().addEmojiElement(emojis);

            // メディアの設定
            model.setMedias(emptyList());
            if (message.getFile() != null) {
                model.setMedias(medias(singletonList(message.getFile())));
            }

            return model;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * 投票オブジェクトマッピング
     */
    public static Poll poll(
            Note note,
            misskey4j.entity.Poll poll,
            Service service) {

        if (poll == null) {
            return null;
        }

        try {
            MisskeyPoll model = new MisskeyPoll(service);
            model.setNoteId(note.getId());

            if (poll.getExpiresAt() != null) {
                Date expiredAt = getDateParser().parse(poll.getExpiresAt());
                model.setExpired(expiredAt.before(new Date()));
                model.setExpireAt(expiredAt);

            } else {
                // 無期限の投票の場合
                model.setExpired(false);
                model.setExpireAt(null);
            }

            model.setMultiple(poll.getMultiple());
            model.setVoted(poll.getChoices().stream().anyMatch(Choice::getVoted));
            model.setVotesCount(poll.getChoices().stream().mapToLong(Choice::getVotes).sum());
            model.setVotersCount(null);

            long index = 0;
            List<PollOption> options = new ArrayList<>();
            for (Choice choice : poll.getChoices()) {
                PollOption option = new PollOption();
                options.add(option);

                option.setCount(choice.getVotes());
                option.setVoted(choice.getVoted());
                option.setTitle(choice.getText());
                option.setIndex(index);
                index++;
            }

            model.setOptions(options);
            return model;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * トレンドマッピング
     */
    public static Trend trend(
            misskey4j.entity.Trend trend) {

        Trend model = new Trend();
        model.setName("#" + trend.getTag());
        model.setQuery("#" + trend.getTag());
        model.setVolume(trend.getUsersCount().intValue());
        return model;
    }

    /**
     * カラーマッピング
     */
    public static Color color(
            misskey4j.entity.Color color) {

        Color model = new Color();
        model.setR(color.getR());
        model.setG(color.getG());
        model.setB(color.getB());
        model.setA(255);
        return model;
    }

    // ============================================================== //
    // List Object Mappers
    // ============================================================== //

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            List<misskey4j.entity.User> accounts,
            String host,
            Service service,
            Paging paging) {

        Pageable<User> model = new Pageable<>();
        model.setEntities(accounts.stream()
                .map(a -> user(a, host, service))
                .collect(toList()));

        model.setPaging(MisskeyPaging.fromPaging(paging));
        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            Note[] notes,
            String host,
            Service service,
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(Stream.of(notes).map(e -> comment(e, host, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(toList()));

        model.setPaging(MisskeyPaging.fromPaging(paging));
        return model;
    }

    /**
     * 通知メンションマッピング
     */
    public static Pageable<Comment> mentions(
            misskey4j.entity.Notification[] notifications,
            String host,
            Service service,
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(Stream.of(notifications).map(n -> mention(n, host, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(toList()));

        model.setPaging(MisskeyPaging.fromPaging(paging));
        return model;
    }

    /**
     * メッセージマッピング
     */
    public static Pageable<Comment> messages(
            Message[] messages,
            String host,
            Service service,
            List<ReactionCandidate> candidates,
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        List<Emoji> emojis = candidates.stream()
                .filter(e -> (e.getIconUrl() != null))
                .map(MisskeyMapper::emoji)
                .collect(toList());

        model.setEntities(Stream.of(messages).map(e -> message(e, emojis, host, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(toList()));

        model.setPaging(MisskeyPaging.fromPaging(paging));
        return model;
    }

    /**
     * リアクションマッピング
     */
    public static List<Reaction> reactions(
            Map<String, Long> reactions,
            List<misskey4j.entity.Emoji> emojis,
            String myReaction) {

        List<Reaction> models = new ArrayList<>();
        reactions.forEach((k, v) -> {

            // カスタム絵文字の場合
            if (k.startsWith(":")) {
                String name = k.split(":")[1];

                // 該当する絵文字の内容を取得
                Optional<misskey4j.entity.Emoji> emoji = emojis.stream()
                        .filter(e -> e.getName().equals(name)).findFirst();

                if (emoji.isPresent()) {

                    // リアクションの追加
                    Reaction model = new Reaction();
                    model.setReacting(k.equals(myReaction));
                    model.setIconUrl(emoji.get().getUrl());
                    model.setName(k);
                    model.setCount(v);
                    models.add(model);
                }

            } else {

                // 一般的な絵文字の場合
                Reaction model = new Reaction();
                model.setReacting(k.equals(myReaction));
                model.setEmoji(k);
                model.setName(k);
                model.setCount(v);
                models.add(model);
            }
        });

        return models;
    }

    /**
     * 絵文字マッピング
     */
    public static List<Emoji> emojis(
            List<misskey4j.entity.Emoji> emojis) {

        if (emojis == null) {
            return new ArrayList<>();
        }
        return emojis.stream()
                .map(MisskeyMapper::emoji)
                .collect(toList());
    }

    /**
     * メディアマッピング
     */
    public static List<Media> medias(
            List<File> files) {

        return files.stream()
                .map(MisskeyMapper::media)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    /**
     * チャンネルマッピング
     */
    public static Pageable<Channel> channels(
            misskey4j.entity.List[] lists,
            Service service) {

        Pageable<Channel> model = new Pageable<>();
        model.setEntities(Stream.of(lists)
                .map(e -> channel(e, service))
                .collect(toList()));
        return model;
    }

    /**
     * 通知マッピング
     */
    public static Pageable<Notification> notifications(
            misskey4j.entity.Notification[] notifications,
            List<ReactionCandidate> reactions,
            String host,
            Service service,
            Paging paging) {

        Pageable<Notification> model = new Pageable<>();
        model.setEntities(Stream.of(notifications)
                .map(a -> notification(a, reactions, host, service))
                .collect(toList()));

        model.setPaging(MisskeyPaging.fromPaging(paging));
        return model;
    }

    /**
     * リアクション候補マッピング
     * (Misskey のリアクション一覧には Like or Share は省く)
     */
    public static List<ReactionCandidate> reactionCandidates(
            List<misskey4j.entity.Emoji> emojis) {

        List<ReactionCandidate> candidates = new ArrayList<>();

        // Misskey はカスタムが先
        emojis.forEach(emoji -> {
            ReactionCandidate candidate = new ReactionCandidate();
            candidates.add(candidate);

            candidate.setCategory(emoji.getCategory());
            candidate.setIconUrl(emoji.getUrl());
            candidate.setName(":" + emoji.getName() + ":");

            candidate.setSearchWord(candidate.getName());
            candidate.setFrequentlyUsed(true);
        });

        for (EmojiType emoji : EmojiType.values()) {
            ReactionCandidate candidate = new ReactionCandidate();
            candidates.add(candidate);

            candidate.setCategory(emoji.getCategory().getCode());
            candidate.setEmoji(emoji.getEmoji());
            candidate.setName(emoji.getEmoji());

            candidate.setSearchWord(emoji.getName());
            candidate.setFrequentlyUsed((emoji.getLevel() != null) && (emoji.getLevel() <= 10));
        }

        for (EmojiVariationType emoji : EmojiVariationType.values()) {
            ReactionCandidate candidate = new ReactionCandidate();
            candidates.add(candidate);

            candidate.setCategory(emoji.getCategory().getCode());
            candidate.setEmoji(emoji.getEmoji());
            candidate.setName(emoji.getEmoji());

            candidate.setSearchWord(emoji.getName());
            candidate.setFrequentlyUsed((emoji.getLevel() != null) && (emoji.getLevel() <= 10));
        }

        return candidates;
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    public static SimpleDateFormat getDateParser() {
        if (dateParser == null) {
            dateParser = new SimpleDateFormat(DATE_FORMAT);
            dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return dateParser;
    }
}
