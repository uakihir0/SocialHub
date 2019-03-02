package net.socialhub.model.service;

/**
 * インスタンス情報
 * Instance Info
 * (for distributed SNS)
 */
public class Instance {

    private String name;

    private String host;

    private String description;

    private String iconImageUrl;

    private Long userCount;

    private Long statusesCount;

    private Long connectionCount;

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public void setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(Long statusesCount) {
        this.statusesCount = statusesCount;
    }

    public Long getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(Long connectionCount) {
        this.connectionCount = connectionCount;
    }
    //endregion
}
