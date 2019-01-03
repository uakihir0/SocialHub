package net.socialhub.model.service;

import net.socialhub.service.action.UserAction;

/**
 * SNS ユーザーモデル
 * SNS User Model
 */

public class User extends Identify {

    private String name;
    private String imageUrl;
    private String screenName;
    private String description;

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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    //endregion
}
