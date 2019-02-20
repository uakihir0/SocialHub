package net.socialhub.model.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 識別
 * Identify
 */
public class Identify {

    private Service service;

    private Long numberId;
    private String stringId;

    /** 追加情報 (必要な場合のみ付与) */
    private Map<String, String> additions;

    // Constructor
    public Identify() {
    }

    // Constructor with Service
    public Identify(Service service) {
        this.service = service;
    }

    public void setId(Long number) {
        this.numberId = number;
    }

    public void setId(String string) {
        this.stringId = string;
    }

    public boolean isNumberId() {
        return (numberId != null);
    }

    public boolean isStringId() {
        return (stringId != null);
    }

    @Override
    public String toString() {
        if (this.numberId != null)
            return this.numberId.toString();
        return this.stringId;
    }

    //region // Getter&Setter
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

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
