package net.socialhub.model.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 識別
 * Identify
 */
public class Identify {

    private Long numberId;
    private String stringId;

    /** 追加情報 (必要な場合のみ付与) */
    private Map<String, String> additions;

    public void setId(Long number) {
        this.numberId = number;
    }

    public void setId(String string) {
        this.stringId = string;
    }


    @Override
    public String toString() {
        if (this.numberId != null)
            return this.numberId.toString();
        return this.stringId;
    }

    //region // Getter&Setter
    public Long getNumberId() {
        return numberId;
    }

    public void setNumberId(Long numberId) {
        this.numberId = numberId;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public String getInfo(String key) {
        if (additions == null) return null;
        return additions.get(key);
    }

    private void setInfo(String key, String value) {
        if (additions == null) additions = new HashMap<>();
        additions.put(key, value);
    }
    //endregion
}
