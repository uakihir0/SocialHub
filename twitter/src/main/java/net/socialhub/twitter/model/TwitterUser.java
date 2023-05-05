package net.socialhub.twitter.model;

import net.socialhub.twitter.define.TwitterIconSize;
import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.service.Service;
import net.socialhub.microblog.model.MiniBlogUser;
import net.socialhub.twitter.action.TwitterMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Twitter User Model
 * Twitter のユーザー要素
 */
public class TwitterUser extends MiniBlogUser {

    public TwitterUser(Service service) {
        super(service);
    }

    /** User setting url */
    private AttributedString url;

    /** User setting location */
    private String location;

    /** Is verified account? */
    private boolean isVerified = false;

    /** Count of Favorites */
    private Long favoritesCount;

    @Override
    public String getAccountIdentify() {
        return "@" + getScreenName();
    }

    @Override
    public String getWebUrl() {
        return "https://twitter.com/" + getScreenName();
    }

    @Override
    public List<AttributedFiled> getAdditionalFields() {
        List<AttributedFiled> fields = new ArrayList<>();
        fields.add(new AttributedFiled("Location", getLocation()));
        fields.add(new AttributedFiled("URL", getUrl()));
        return fields;
    }

    /**
     * Direct Message Form
     * メッセージフォームは Twitter と Mastodon で扱いが異なる
     * Twitter の DM はユーザーの AccountIdentify は不要
     */
    @Override
    public CommentForm getMessageForm() {
        CommentForm form = new CommentForm();
        form.replyId(getId());
        form.message(true);
        return form;
    }

    /**
     * Get icon imageURL of specific size
     * 特定のサイズの画像アイコンを取得
     */
    public String getIconImageUrl(TwitterIconSize size) {
        TwitterIconSize beforeSize = TwitterMapper.DEFAULT_ICON_SIZE;
        return getIconImageUrl().replace(beforeSize.getSuffix(), size.getSuffix());
    }

    //region // Getter&Setter
    public AttributedString getUrl() {
        return url;
    }

    public void setUrl(AttributedString url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Long favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public boolean getVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    //endregion
}
