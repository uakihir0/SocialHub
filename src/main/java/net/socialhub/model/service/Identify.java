package net.socialhub.model.service;

import java.io.Serializable;
import java.util.Optional;

/**
 * 識別
 * Identify
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
     * Is Same Identify?
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
