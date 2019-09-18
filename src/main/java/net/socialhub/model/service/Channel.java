package net.socialhub.model.service;

import java.util.Date;

/**
 * SNS チャンネル (リスト) 情報
 * SNS Channel (List) Model
 */
public class Channel extends Identify {

    public Channel(Service service) {
        super(service);
    }

    private String name;

    private String description;

    private Date createAt;

    private Boolean isPublic;

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    //endregion
}

