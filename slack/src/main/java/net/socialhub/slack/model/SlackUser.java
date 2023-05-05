package net.socialhub.slack.model;

import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.service.Service;
import net.socialhub.core.model.service.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Slack User Model
 * Slack のユーザー情報
 */
public class SlackUser extends User {

    public SlackUser(Service service) {
        super(service);
    }

    /** email */
    private AttributedString email;

    /** phone */
    private AttributedString phone;

    /** what user's do */
    private String title;

    /** display name */
    private String displayName;

    /** team information */
    private SlackTeam team;

    /** is bot account? */
    private Boolean isBot;

    @Override
    public String getWebUrl() {
        return "https://app.slack.com/client/"
                + team.getId()
                + "/user_profile/"
                + this.getId();
    }

    @Override
    public CommentForm getMessageForm() {
        CommentForm form = new CommentForm();
        form.replyId(getId());
        form.message(true);
        return form;
    }

    @Override
    public String getAccountIdentify() {
        if ((team != null) && (team.getName() != null)) {
            return team.getName() + ":" + getScreenName();
        }
        return getScreenName();
    }

    @Override
    public List<AttributedFiled> getAdditionalFields() {
        List<AttributedFiled> fields = new ArrayList<>();
        fields.add(new AttributedFiled("Email", getEmail()));
        fields.add(new AttributedFiled("Phone", getPhone()));
        return fields;
    }

    //region // Getter&Setter
    public AttributedString getEmail() {
        return email;
    }

    public void setEmail(AttributedString email) {
        this.email = email;
    }

    public AttributedString getPhone() {
        return phone;
    }

    public void setPhone(AttributedString phone) {
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

    public SlackTeam getTeam() {
        return team;
    }

    public void setTeam(SlackTeam team) {
        this.team = team;
    }

    public Boolean getBot() {
        return isBot;
    }

    public void setBot(Boolean bot) {
        isBot = bot;
    }
    //endregion
}
