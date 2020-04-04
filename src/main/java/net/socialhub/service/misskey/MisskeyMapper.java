package net.socialhub.service.misskey;

import misskey4j.entity.Field;
import misskey4j.entity.File;
import misskey4j.entity.Note;
import misskey4j.entity.Relation;
import net.socialhub.define.EmojiType;
import net.socialhub.define.EmojiVariationType;
import net.socialhub.define.MediaType;
import net.socialhub.logger.Logger;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Emoji;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.misskey.MisskeyComment;
import net.socialhub.model.service.addition.misskey.MisskeyPaging;
import net.socialhub.model.service.addition.misskey.MisskeyUser;
import net.socialhub.model.service.support.ReactionCandidate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

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

        // 絵文字の追加
        model.setEmojis(emojis(account.getEmojis()));

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

            // リアクションの設定
            model.setReactions(reactions(
                    note.getReactions(),
                    note.getEmojis(),
                    note.getMyReaction()));

            return model;

        } catch (ParseException e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
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

    // ============================================================== //
    // List Object Mapper
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
            candidate.setName(emoji.getName());
        });

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
