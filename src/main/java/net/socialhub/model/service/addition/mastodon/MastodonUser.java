package net.socialhub.model.service.addition.mastodon;

import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.addition.MiniBlogUser;

import java.net.URL;
import java.util.List;

/**
 * Mastodon User Model
 * Mastodon のユーザー情報
 */
public class MastodonUser extends MiniBlogUser {

    private List<AttributedFiled> fields;

    public MastodonUser(Service service) {
        super(service);
    }

    @Override
    public String getAccountIdentify() {
        try {
            // 外のホストの場合は既に URL が付与済
            if (getScreenName().contains("@")) {
                return "@" + getScreenName();
            }

            // URL と結合して Mastodon アカウント対応
            URL url = new URL(getProfileUrl().getDisplayText());
            return "@" + getScreenName() + "@" + url.getHost();

        } catch (Exception e) {
            return getScreenName();
        }
    }

    @Override
    public String getWebUrl() {
        String host = getAccountIdentify().split("@")[2];
        String identify = getAccountIdentify().split("@")[1];
        return "https://" + host + "/@" + identify;
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

