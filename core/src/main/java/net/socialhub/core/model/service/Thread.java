package net.socialhub.core.model.service;

import java.util.Date;
import java.util.List;

/**
 * Thread of Group Messaging
 * グループメッセージスレッド
 */
public class Thread extends Identify {

    /**
     * Attendee
     * 参加者
     */
    private List<User> users;

    /**
     * Last Update Datetime
     * 最終更新日時
     */
    private Date lastUpdate;

    /**
     * Description about this thread
     * スレッドの簡単な説明文
     */
    private String description;

    public Thread(Service service) {
        super(service);
    }

    //region // Getter&Setter
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //endregion
}
