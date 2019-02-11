package net.socialhub.model.service.addition;

/**
 * Slack における User 要素
 * Slack specified user's attributes
 */
public class SlackUser {

    /** email */
    private String email;

    /** phone */
    private String phone;

    /** what user's do */
    private String title;

    /** display name*/
    private String displayName;

    //region // Getter&Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    //endregion
}
