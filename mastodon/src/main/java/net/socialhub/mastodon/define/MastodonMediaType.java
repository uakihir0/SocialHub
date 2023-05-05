package net.socialhub.mastodon.define;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum MastodonMediaType {

    Image("image"),
    Video("video", "gifv"),
    Unknown("unknown");

    MastodonMediaType(String... codes) {
        this.codes = Arrays.asList(codes);
    }

    private List<String> codes;

    public List<String> getCodes() {
        return codes;
    }

    public static MastodonMediaType of(String code) {
        return Stream.of(values())
                .filter((e) -> e.getCodes().stream()
                        .anyMatch(c -> c.equalsIgnoreCase(code)))
                .findFirst().orElse(null);
    }
}