package net.socialhub.model.service.addition.slack;

/**
 * Slack Team Model
 * Slack のチーム情報
 */
public class SlackTeam {

    private String id;

    private String name;

    private String domain;

    private String iconImageUrl;

    //region // Getter&Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public void setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
    }
    //endregion
}
