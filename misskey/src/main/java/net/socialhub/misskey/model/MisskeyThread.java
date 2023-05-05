package net.socialhub.misskey.model;

import net.socialhub.core.model.service.Service;
import net.socialhub.core.model.service.Thread;

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
