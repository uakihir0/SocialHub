package net.socialhub.model.request;


public class MediaRequest {

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
