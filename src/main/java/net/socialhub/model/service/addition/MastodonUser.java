package net.socialhub.model.service.addition;

import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Service;

import java.util.List;

/**
 * Mastodon における User 要素
 * Mastodon specified user's attributes
 */
public class MastodonUser extends MiniBlogUser {

    private List<MastodonUserFiled> fields;

    public MastodonUser(Service service) {
        super(service);
    }

    //region // Getter&Setter
    public List<MastodonUserFiled> getFields() {
        return fields;
    }

    public void setFields(List<MastodonUserFiled> fields) {
        this.fields = fields;
    }
    //endregions

    /**
     * Mastodon User における追加フィールド
     * Mastodon Extra Fields
     */
    public static class MastodonUserFiled {

        private String name;
        private AttributedString value;

        //region // Getter&Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AttributedString getValue() {
            return value;
        }

        public void setValue(AttributedString value) {
            this.value = value;
        }

        //endregion
    }
}

