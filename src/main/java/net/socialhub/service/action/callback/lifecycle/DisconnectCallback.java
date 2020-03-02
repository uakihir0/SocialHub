package net.socialhub.service.action.callback.lifecycle;

import net.socialhub.service.action.callback.EventCallback;

public interface DisconnectCallback extends EventCallback {

    void onDisconnect();
}
