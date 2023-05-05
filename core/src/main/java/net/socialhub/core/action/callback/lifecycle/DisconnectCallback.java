package net.socialhub.core.action.callback.lifecycle;

import net.socialhub.core.action.callback.EventCallback;

public interface DisconnectCallback extends EventCallback {

    void onDisconnect();
}
