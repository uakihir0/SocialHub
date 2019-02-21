package net.socialhub.model.service;

import net.socialhub.model.common.AttributedString;
import net.socialhub.service.action.UserAction;

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

    public UserAction getAction() {
        return UserAction.of(this);
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
