package net.socialhub.core.j2objc;

import net.socialhub.core.j2objc.security.HmacProvider;

import java.security.Security;

public interface J2ObjcExtensions {

    Runnable getInitializer();

    enum Standard implements J2ObjcExtensions {

        Hmac(() -> Security.addProvider(new HmacProvider())), //

        ;
        private Runnable initializer;

        Standard(Runnable runnable) {
            this.initializer = runnable;
        }

        @Override
        public Runnable getInitializer() {
            return initializer;
        }
    }
}
