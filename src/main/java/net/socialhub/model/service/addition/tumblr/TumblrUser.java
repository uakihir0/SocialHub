package net.socialhub.model.service.addition.tumblr;

import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

public class TumblrUser extends User {

    public TumblrUser(Service service) {
        super(service);
    }

    /** Count of followers */
    private Integer followersCount;

    /** Count of posts */
    private Integer postsCount;

    /** Count of likes */
    private Integer likesCount;

    /** Blog Url */
    private String blogUrl;

    /** Blog Title */
    private String blogTitle;

    /**
     * Relationship
     * (Only following and blocked)
     */
    private Relationship relationship;

    @Override
    public String getWebUrl() {
        return blogUrl;
    }

    //region // Getter&Setter
    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
    //endregion
}
