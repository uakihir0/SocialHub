package net.socialhub.model.service.addition;

import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.service.Service;

import java.net.URL;
import java.util.List;

/**
 * Mastodon における User 要素
 * Mastodon specified user's attributes
 */
public class MastodonUser extends MiniBlogUser {

    private List<AttributedFiled> fields;

    public MastodonUser(Service service) {
        super(service);
    }

    @Override
    public String getAccountIdentify() {
        try {
            URL url = new URL(getProfileUrl().getText());
            return "@" + getScreenName() + "@" + url.getHost();
        } catch (Exception e) {
            return getScreenName();
        }
    }

    @Override
    public List<AttributedFiled> getAdditionalFields() {
        return getFields();
    }

    //region // Getter&Setter
    public List<AttributedFiled> getFields() {
        return fields;
    }

    public void setFields(List<AttributedFiled> fields) {
        this.fields = fields;
    }
    //endregion
}

