package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;

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
