package net.socialhub.service.misskey.model;

import net.socialhub.core.model.Service;
import net.socialhub.core.model.Thread;

public class MisskeyThread extends Thread {

    /** グループスレッドかどうか？ */
    private boolean isGroup = false;

    public MisskeyThread(Service service) {
        super(service);
    }

    // region
    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
    // endregion
}
