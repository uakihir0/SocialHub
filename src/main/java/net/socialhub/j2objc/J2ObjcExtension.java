package net.socialhub.j2objc;

import java.util.Arrays;
import java.util.List;

public class J2ObjcExtension {

    public static void initialize(List<J2ObjcExtensions> extensions) {
        extensions.forEach(e -> e.getInitializer().run());
    }

    public static void initialize(J2ObjcExtensions... extensions) {
        initialize(Arrays.asList(extensions));
    }
}
