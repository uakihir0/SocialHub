package net.socialhub.model.service;

import java.io.Serializable;
import java.util.Optional;

/**
 * Identify
 * 識別
 */
public class Identify implements Serializable {

    private Service service;

    private Object id;

    // Constructor
    public Identify(Service service) {
        this.service = service;
    }

    public Identify(Service service, Object id) {
        this.service = service;
        this.id = id;
    }

    /**
     * Get serialized string ID. (for serialize request)
     * (Get id string with type information.)
     * 識別情報の ID に型情報を入れて文字列として返却
     */
    public String getSerializedIdString() {
        if (id instanceof Integer) {
            return "I" + id.toString();
        }
        if (id instanceof Long) {
            return "L" + id.toString();
        }
        if (id instanceof String) {
            return "S" + id.toString();
        }
        throw new IllegalStateException("Not supported type.");
    }

    /**
     * Set serialized string ID. (for serialize request)
     * (Set id with typed id string.)
     * 型付き ID 文字列よりID情報を復元
     */
    public void setSerializedIdString(String idString) {
        if (idString.startsWith("I")) {
            id = Integer.parseInt(idString.substring(1));
            return;
        }
        if (idString.startsWith("L")) {
            id = Long.parseLong(idString.substring(1));
            return;
        }
        if (idString.startsWith("S")) {
            id = idString.substring(1);
            return;
        }
        throw new IllegalStateException("Not supported type.");
    }

    /**
     * Is same identify?
     * 同じ識別子か？
     */
    public boolean isSameIdentify(Identify id) {
        return (this.getService().getType() == id.getService().getType())
                && this.getId().equals(id.getId());
    }

    //region // Getter&Setter
    public void setId(Object id) {
        this.id = id;
    }

    public Object getId() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getId(Class<T> clazz) {
        return (clazz.isInstance(id)) ? Optional.of((T) id) : Optional.empty();
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
    //endregion
}
