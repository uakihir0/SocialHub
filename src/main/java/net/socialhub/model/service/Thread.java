package net.socialhub.model.service;

import java.util.Date;
import java.util.List;

/**
 * Thread of Group Messaging
 * グループメッセージスレッド
 */
public class Thread {

    /**
     * 参加者
     * Attendee
     */
    private List<User> users;

    /**
     * Last Update Datetime
     * 最終更新日時
     */
    private Date lastUpdate;

    /**
     * List of Comments
     * コメント一覧
     */
    private List<Comment> comments;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
