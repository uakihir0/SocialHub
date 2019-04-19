package net.socialhub.define.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum TwitterMediaTypeEnum {

    Photo("photo"),
    Video("video", "animated_gif");

    TwitterMediaTypeEnum(String... codes) {
        this.codes = Arrays.asList(codes);
    }

    private List<String> codes;

    public List<String> getCodes() {
        return codes;
    }

    public static TwitterMediaTypeEnum of(String code) {
        return Stream.of(values()) //
                .filter((e) -> e.getCodes().contains(code)) //
                .findFirst().orElse(null);
    }
}
