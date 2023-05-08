package net.socialhub.core.model;

import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.error.NotImplimentedException;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.action.UserAction;
import net.socialhub.core.action.AccountAction;
import net.socialhub.core.action.UserActionImpl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * SNS ユーザーモデル
 * SNS User Model
 */
public class User extends Identify {

    /** User's display name */
    private String name;

    /** User's identified name */
    private String screenName;

    /** User's description */
    private AttributedString description;

    /** Icon image url */
    private String iconImageUrl;

    /** Cover image url */
    private String coverImageUrl;

    public User(Service service) {
        super(service);
    }

    /**
     * Get Action
     */
    @Nonnull
    public UserAction action() {
        AccountAction action = getService().getAccount().action();
        return new UserActionImpl(action).user(this);
    }

    /**
     * SNS アカウント ID 表現を取得
     * Get SNS Account Identify
     * Need each SNS implementation
     */
    public String getAccountIdentify() {
        return screenName;
    }

    /**
     * SNS アカウント毎の特殊属性を取得
     * 特定のクラスに変換する事を推奨
     * Get SNS Additional Fields
     * (recommend to cast specified SNS model)
     */
    public List<AttributedFiled> getAdditionalFields() {
        return new ArrayList<>();
    }

    /**
     * Get Comment Form
     * コメント投稿用のフォームを取得
     */
    public CommentForm getCommentForm() {
        throw new NotImplimentedException();
    }

    /**
     * Get Message Form
     * メッセージ投稿用のフォームを取得
     */
    public CommentForm getMessageForm() {
        throw new NotImplimentedException();
    }

    /**
     * Get Web Url
     * Web のアドレスを取得
     */
    public String getWebUrl() {
        throw new NotImplimentedException();
    }

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public AttributedString getDescription() {
        return description;
    }

    public void setDescription(AttributedString description) {
        this.description = description;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public void setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    //endregion
}
