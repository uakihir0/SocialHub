package net.socialhub.model.service.event;

public class IdentifyEvent {

    private Object id;

    public IdentifyEvent(Object id) {
        this.id = id;
    }

    //region // Getter&Setter
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
    //endregion
}
