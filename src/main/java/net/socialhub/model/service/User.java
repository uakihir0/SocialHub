package net.socialhub.model.service;

import net.socialhub.model.service.addition.MastodonUser;
import net.socialhub.model.service.addition.SlackUser;
import net.socialhub.model.service.addition.TwitterUser;
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
    private String description;

    /** Icon image url */
    private String iconImageUrl;

    /** Cover image url*/
    private String coverImageUrl;

    /** User addition data*/
    private UserAdditions additions;

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

    public UserAdditions getAdditions() {
        return additions;
    }

    public void setAdditions(UserAdditions additions) {
        this.additions = additions;
    }
    //endregion

    /**
     * SNS 毎の要素
     * SNS specified attributes
     */
    public static class UserAdditions {

        private SlackUser slack;

        private TwitterUser twitter;

        private MastodonUser mastodon;

        //region // Getter&Setter
        public SlackUser getSlack() {
            return slack;
        }

        public void setSlack(SlackUser slack) {
            this.slack = slack;
        }

        public TwitterUser getTwitter() {
            return twitter;
        }

        public void setTwitter(TwitterUser twitter) {
            this.twitter = twitter;
        }

        public MastodonUser getMastodon() {
            return mastodon;
        }

        public void setMastodon(MastodonUser mastodon) {
            this.mastodon = mastodon;
        }
        //endregion
    }
}
