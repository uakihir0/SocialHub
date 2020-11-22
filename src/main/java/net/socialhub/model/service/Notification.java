package net.socialhub.model.service;

import net.socialhub.define.NotificationActionType;
import net.socialhub.define.service.mastodon.MastodonNotificationType;

import java.util.Date;
import java.util.List;

/**
 * Notification
 * 通知
 */
public class Notification extends Identify {

    /**
     * Notification type name
     * Origin name form social media.
     *
     * @see MastodonNotificationType (code field)
     */
    private String type;

    /**
     * Common Action type name
     * SocialHub common action name.
     *
     * @see NotificationActionType (code field)
     */
    private String action;

    /** Date of created */
    private Date createAt;

    /** Associated users */
    private List<User> users;

    /** Associated comments */
    private List<Comment> comments;

    public Notification(Service service) {
        super(service);
    }

    // region // Getter&Setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    // endregion
}
