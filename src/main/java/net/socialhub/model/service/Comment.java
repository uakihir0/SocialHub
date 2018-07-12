package net.socialhub.model.service;

import net.socialhub.utils.MemoSupplier;

import java.util.Date;

/**
 * SNS コメント情報
 * SNS Comment Model
 */
public class Comment extends Identify {

    private Service service;

    private String comment;

    private Date createAt;

    private MemoSupplier<User> user;

    public Comment(Service service) {
        this.service = service;
    }

    //region // Getter&Setter
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(MemoSupplier<User> user) {
        this.user = user;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
    //endregion
}
