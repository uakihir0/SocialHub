package net.socialhub.service.misskey.action;

import misskey4j.entity.Choice;
import misskey4j.entity.Field;
import misskey4j.entity.File;
import misskey4j.entity.Message;
import misskey4j.entity.Note;
import misskey4j.entity.Relation;
import net.socialhub.core.define.MediaType;
import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Emoji;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Poll;
import net.socialhub.core.model.Reaction;
import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.Thread;
import net.socialhub.core.model.Trend;
import net.socialhub.core.model.User;
import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.support.Color;
import net.socialhub.core.model.support.PollOption;
import net.socialhub.logger.Logger;
import net.socialhub.service.misskey.define.MisskeyNotificationType;
import net.socialhub.service.misskey.define.MisskeyVisibility;
import net.socialhub.service.misskey.model.MisskeyComment;
import net.socialhub.service.misskey.model.MisskeyNotification;
import net.socialhub.service.misskey.model.MisskeyPaging;
import net.socialhub.service.misskey.model.MisskeyPoll;
import net.socialhub.service.misskey.model.MisskeyThread;
import net.socialhub.service.misskey.model.MisskeyUser;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        model.setEmojis(selectEmojis(
                account.getEmojis(),
                account.getName(),
                host));

        // 説明文字列がない場合はシンプルオブジェクトと判定
        if (account.getDescription() == null) {
            model.setSimple(true);
        }

        // ユーザー説明分の設定
        model.setDescription(AttributedString.plain(account.getDescription()));
        model.getDescription().addEmojiElement(selectEmojis(
                account.getEmojis(),
                account.getDescription(),
                host));

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
                f.getValue().addEmojiElement(selectEmojis(
                        account.getEmojis(),
                        field.getValue(),
                        host));
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
                model.getSpoilerText().addEmojiElement(selectEmojis(
                        note.getEmojis(),
                        note.getCw(),
                        host));
            }

            // 本文の設定
            model.setText(AttributedString.plain(note.getText()));
            model.getText().addEmojiElement(selectEmojis(
                    note.getEmojis(),
                    note.getText(),
                    host));

            // メディアの設定
            model.setMedias(medias(note.getFiles()));

            // 投票の設定
            model.setPoll(poll(note, note.getPoll(), service));

            // リアクションの設定
            model.setReactions(reactions(
                    note.getReactions(),
                    note.getEmojis(),
                    note.getMyReaction(),
                    host));

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
        model.addShortCode(emoji.getName());
        model.setImageUrl(emoji.getUrl());
        model.setCategory(emoji.getCategory());

        if (emoji.getAliases() != null) {
            emoji.getAliases().forEach(model::addShortCode);
        }

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
            List<Emoji> emojis,
            String host,
            Service service) {

        try {
            MisskeyNotification model = new MisskeyNotification(service);
            model.setReaction(notification.getReaction());
            model.setId(notification.getId());

            // アンテナの通知などは時刻が含まれないので確認
            if (notification.getCreatedAt() != null) {
                String createdAt = notification.getCreatedAt();
                model.setCreateAt(getDateParser().parse(createdAt));
            }

            MisskeyNotificationType type =
                    MisskeyNotificationType.of(notification.getType());

            // ローカルアイコンの取得
            emojis.stream()
                    .filter(e -> e.getShortCodes().contains(notification.getReaction()))
                    .findFirst().ifPresent(c -> model.setIconUrl(c.getImageUrl()));

            // リモートアイコンの取得
            String reaction = model.getReaction();
            if ((type == MisskeyNotificationType.REACTION) &&
                    reaction.startsWith(":") &&
                    (model.getIconUrl() == null)) {

                String code = reaction.replaceAll(":", "");
                model.setIconUrl(getEmojiURL(host, code));
            }

            // 存在する場合のみ設定
            if (type != null) {
                model.setType(type.getCode());
                if (type.getAction() != null) {
                    model.setAction(type.getAction().getCode());
                }
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
            List<Emoji> emojis,
            Paging paging) {

        Pageable<Comment> model = new Pageable<>();

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
            String myReaction,
            String host
    ) {

        List<Reaction> models = new ArrayList<>();
        reactions.forEach((k, v) -> {

            // カスタム絵文字の場合
            if (k.startsWith(":")) {
                String name = k.split(":")[1];
                if (emojis != null) {

                    // 該当する絵文字の内容を取得
                    Optional<misskey4j.entity.Emoji> emoji = emojis.stream()
                            .filter(e -> e.getName().equals(name))
                            .findFirst();

                    if (emoji.isPresent()) {

                        // リアクションの追加
                        Reaction model = new Reaction();
                        model.setReacting(k.equals(myReaction));
                        model.setIconUrl(emoji.get().getUrl());
                        model.setName(k);
                        model.setCount(v);
                        models.add(model);
                    }
                }

                // V13 からリモート絵文字の取得方法が変更
                Reaction model = new Reaction();
                model.setReacting(k.equals(myReaction));
                model.setIconUrl(getEmojiURL(host, name));
                model.setName(k);
                model.setCount(v);
                models.add(model);

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
            List<Emoji> emojis,
            String host,
            Service service,
            Paging paging) {

        Pageable<Notification> model = new Pageable<>();
        model.setEntities(Stream.of(notifications)
                .map(a -> notification(a, emojis, host, service))
                .collect(toList()));

        model.setPaging(MisskeyPaging.fromPaging(paging));
        return model;
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

    // ============================================================== //
    // Emojis
    // ============================================================== //

    public static List<Emoji> selectEmojis(
            List<misskey4j.entity.Emoji> emojis,
            String text,
            String host) {

        if (emojis != null) {
            return emojis(emojis);
        }
        return extractEmojis(text, host);
    }

    public static List<Emoji> extractEmojis(
            String text,
            String host
    ) {
        List<Emoji> results = new ArrayList<>();
        String regex = ":([a-zA-Z0-9_]+(@[a-zA-Z0-9-.]+)?):";
        Pattern p = Pattern.compile(regex);

        if (text == null || text.isEmpty()) {
            return results;
        }

        while (true) {
            Matcher m = p.matcher(text);
            if (!m.find()) {
                break;
            }

            int i = m.start();
            String found = m.group();
            if (i < 0) {
                break;
            }

            text = text.substring(i + found.length());
            String code = found.replaceAll(":", "");

            Emoji emoji = new Emoji();
            emoji.setImageUrl(getEmojiURL(host, code));
            emoji.addShortCode(code);
            results.add(emoji);
        }

        return results;
    }


    /**
     * (from v13)
     * 絵文字の URL を取得
     * code としては以下のような文字列を想定
     * * emoji
     * * emoji@example.com
     * * emoji@.
     */
    private static String getEmojiURL(String host, String code) {
        code = code.replaceAll(":", "");
        if (code.endsWith("@.")) {
            code = code.substring(0, code.length() - 2);
        }
        return "https://" + host + "/emoji/" + code + ".webp";
    }
}
