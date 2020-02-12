package net.socialhub.define.service.slack;

/**
 * Slack Scope Builder
 * {@see https://api.slack.com/docs/oauth-scopes}
 */
public class SlackScope {

    private String group;
    private String action;
    private String target;

    /**
     * Get Scope String
     * スコープ文字列を出力
     */
    public String getCode() {
        StringBuilder builder = new StringBuilder();
        builder.append(group);

        if (action != null && !action.isEmpty()) {
            builder.append(":").append(action);
        }
        if (target != null && !target.isEmpty()) {
            builder.append(":").append(target);
        }
        return builder.toString();
    }

    // ================================================= //
    // Group
    // ================================================= //

    public SlackScope channels() {
        group = "channels";
        return this;
    }

    public SlackScope chat() {
        group = "chat";
        return this;
    }

    public SlackScope dnd() {
        group = "dnd";
        return this;
    }

    public SlackScope emoji() {
        group = "emoji";
        return this;
    }

    public SlackScope files() {
        group = "files";
        return this;
    }

    public SlackScope groups() {
        group = "groups";
        return this;
    }

    public SlackScope identity() {
        group = "identity";
        return this;
    }

    public SlackScope identityAvatar() {
        group = "identity.avatar";
        return this;
    }

    public SlackScope identityBasic() {
        group = "identity.basic";
        return this;
    }

    public SlackScope identityEmail() {
        group = "identity.email";
        return this;
    }

    public SlackScope identityTeam() {
        group = "identity.team";
        return this;
    }

    public SlackScope im() {
        group = "im";
        return this;
    }

    public SlackScope links() {
        group = "links";
        return this;
    }

    public SlackScope mpim() {
        group = "mpim";
        return this;
    }

    public SlackScope pins() {
        group = "pins";
        return this;
    }

    public SlackScope reactions() {
        group = "reactions";
        return this;
    }

    public SlackScope reminders() {
        group = "reminders";
        return this;
    }

    public SlackScope search() {
        group = "search";
        return this;
    }

    public SlackScope stars() {
        group = "stars";
        return this;
    }

    public SlackScope team() {
        group = "team";
        return this;
    }

    public SlackScope tokensBasic() {
        group = "tokens.basic";
        return this;
    }

    public SlackScope usergroups() {
        group = "usergroups";
        return this;
    }

    public SlackScope usersProfile() {
        group = "users.profile";
        return this;
    }

    public SlackScope users() {
        group = "users";
        return this;
    }

    // ================================================= //
    // Action
    // ================================================= //

    public SlackScope read() {
        action = "read";
        return this;
    }

    public SlackScope readEmail() {
        action = "read.email";
        return this;
    }

    public SlackScope write() {
        action = "write";
        return this;
    }

    // ================================================= //
    // Targets
    // ================================================= //

    public SlackScope history() {
        target = "history";
        return this;
    }

    public SlackScope user() {
        target = "user";
        return this;
    }

    public SlackScope bot() {
        target = "bot";
        return this;
    }

}
