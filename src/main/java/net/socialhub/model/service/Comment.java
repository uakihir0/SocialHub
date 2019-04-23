package net.socialhub.model.service;

import net.socialhub.model.common.AttributedString;
import net.socialhub.service.action.AccountAction;
import net.socialhub.service.action.CommentAction;
import net.socialhub.service.action.CommentActionImpl;

import java.util.Date;
import java.util.List;

/**
 * SNS コメント情報
 * SNS Comment Model
 */
public class Comment extends Identify {

    /** Text */
    private AttributedString text;

    /** Date of text created */
    private Date createAt;

    /** User who create this text */
    private User user;

    /**
     * Files which attached with this text
     * if no media with this tweet, return empty list.
     */
    private List<Media> medias;

    /**
     * Shared text
     * (ReTweeted or Quoted text in Twitter Term)
     * (Null if text shared any text)
     */
    private Comment sharedComment;

    /**
     * Is possibly sensitive?
     * NSFW in mastodon term.
     */
    private Boolean possiblySensitive;

    /**
     * Application which user used
     * (with application link)
     */
    private Application application;

    public Comment(Service service) {
        super(service);
    }

    public CommentAction action() {
        AccountAction action = getService().getAccount().action();
        return new CommentActionImpl(action).comment(this);
    }

    //region // Getter&Setter
    public AttributedString getText() {
        return text;
    }

    public void setText(AttributedString text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Comment getSharedComment() {
        return sharedComment;
    }

    public void setSharedComment(Comment sharedComment) {
        this.sharedComment = sharedComment;
    }

    public Boolean getPossiblySensitive() {
        return possiblySensitive;
    }

    public void setPossiblySensitive(Boolean possiblySensitive) {
        this.possiblySensitive = possiblySensitive;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    //endregion
}
