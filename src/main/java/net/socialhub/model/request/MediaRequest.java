package net.socialhub.model.request;

import java.util.Map;
import java.util.concurrent.Future;

public class MediaRequest {

    private byte[] data;

    private Map<String, Future<Object>> uploadingAsyncTask;

    //region // Getter&Setter
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    //region
}
