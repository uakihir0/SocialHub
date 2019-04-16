package net.socialhub.model.service;

import java.io.Serializable;
import java.util.Optional;

/**
 * 識別
 * Identify
 */
public class Identify implements Serializable {

    // Constructor
    public Identify(Service service) {
        this.service = service;
    }

    private Service service;

    private Object id;

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
