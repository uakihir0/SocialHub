package net.socialhub.model.service;

/**
 * Application posted by
 * 投稿されたアプリケーション
 */
public class Application {

    private String name;

    private String website;

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
    //endregion
}
