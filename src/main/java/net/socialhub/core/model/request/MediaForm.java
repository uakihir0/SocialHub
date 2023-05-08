package net.socialhub.core.model.request;


public class MediaForm {

    // ============================================================== //
    // Fields
    // ============================================================== //

    /** Media Data */
    private byte[] data;

    /** Media File Name */
    private String name;

    public byte[] getData() {
        return data;
    }

    /** Copy this object */
    public MediaForm copy() {
        MediaForm model = new MediaForm();
        model.setData(data);
        model.setName(name);
        return model;
    }

    // ============================================================== //
    // Getters
    // ============================================================== //

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
