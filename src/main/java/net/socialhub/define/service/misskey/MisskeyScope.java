package net.socialhub.define.service.misskey;

import misskey4j.entity.contant.Scope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class MisskeyScope {

    /**
     * Get all scopes.
     * 全ての権限のスコープを取得
     */
    static List<String> getAllScopes() {
        return Arrays.stream(Scope.ALL)
                .map(Objects::toString)
                .collect(toList());
    }
}
