package net.socialhub.service.bluesky.model;

import net.socialhub.core.model.Service;
import net.socialhub.service.microblog.model.MiniBlogUser;

/**
 * Bluesky User Model
 * Bluesky のユーザー情報
 */
public class BlueskyUser extends MiniBlogUser {
    public BlueskyUser(Service service) {
        super(service);
    }
}
