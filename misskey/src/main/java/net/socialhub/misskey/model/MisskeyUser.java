package net.socialhub.misskey.model;

import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Emoji;
import net.socialhub.core.model.service.Service;
import net.socialhub.microblog.model.MiniBlogUser;
import net.socialhub.core.model.service.support.Color;

import java.util.List;

import static java.util.Collections.emptyList;

public class MisskeyUser extends MiniBlogUser {

    /** attributed name (custom emoji included) */
    private AttributedString attributedName;

    /** attributed filed that user input */
    private List<AttributedFiled> fields;

    /** user pinned owned comments */
    private List<Comment> pinnedComments;

    /** emojis which contains in name */
    private List<Emoji> emojis;

    /** User setting location */
    private String location;

    /** Host account belong to */
    private String host;

    private boolean isCat = false;
    private boolean isBot = false;

    /** Simple object for timeline */
    private boolean isSimple = false;

    /** Color of users */
    private Color avatarColor;
    private Color bannerColor;

    public MisskeyUser(Service service) {
        super(service);
    }

    /**
     * Get Attributed Name
     * 絵文字付き属性文字列を取得
     */
    public AttributedString getAttributedName() {
        if (attributedName == null) {
            attributedName = AttributedString.plain(getName(), emptyList());
            attributedName.addEmojiElement(emojis);
        }
        return attributedName;
    }

    /**
     * Get is custom emoji included user.
     * 絵文字付きのユーザー情報かを取得
     */
    public boolean isEmojiIncluded() {
        return emojis != null && !emojis.isEmpty();
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (name == null || name.isEmpty()) {
            return getScreenName().split("@")[0];
        }
        return name;
    }

    @Override
    public String getAccountIdentify() {
        return "@" + getScreenName() + "@" + getHost();
    }

    @Override
    public String getWebUrl() {
        String host = getAccountIdentify().split("@")[2];
        String identify = getAccountIdentify().split("@")[1];
        return "https://" + host + "/@" + identify;
    }

    @Override
    public List<AttributedFiled> getAdditionalFields() {
        return getFields();
    }

    /**
     * Direct Message Form
     * メッセージフォームは Twitter と Misskey で扱いが異なる
     * Misskey の DM はユーザーの AccountIdentify が必要
     */
    @Override
    public CommentForm getMessageForm() {
        CommentForm form = new CommentForm();
        form.text(getAccountIdentify() + " ");
        form.message(true);
        return form;
    }

    // region // Getter&Setter
    public List<AttributedFiled> getFields() {
        return fields;
    }

    public void setFields(List<AttributedFiled> fields) {
        this.fields = fields;
    }

    public List<Comment> getPinnedComments() {
        return pinnedComments;
    }

    public void setPinnedComments(List<Comment> pinnedComments) {
        this.pinnedComments = pinnedComments;
    }

    public List<Emoji> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<Emoji> emojis) {
        this.emojis = emojis;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isCat() {
        return isCat;
    }

    public void setCat(boolean cat) {
        isCat = cat;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public Color getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(Color avatarColor) {
        this.avatarColor = avatarColor;
    }

    public Color getBannerColor() {
        return bannerColor;
    }

    public void setBannerColor(Color bannerColor) {
        this.bannerColor = bannerColor;
    }
    // endregion
}
