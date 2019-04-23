package net.socialhub.define.service.twitter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum TwitterMediaType {

    Photo("photo"),
    Video("video", "animated_gif");

    TwitterMediaType(String... codes) {
        this.codes = Arrays.asList(codes);
    }

    private List<String> codes;

    public List<String> getCodes() {
        return codes;
    }

    public static TwitterMediaType of(String code) {
        return Stream.of(values()) //
                .filter((e) -> e.getCodes().contains(code)) //
                .findFirst().orElse(null);
    }
}
