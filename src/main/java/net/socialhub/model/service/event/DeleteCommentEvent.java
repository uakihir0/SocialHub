package net.socialhub.model.service.event;

public class DeleteCommentEvent {

    private Object id;

    public DeleteCommentEvent(Object id) {
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
