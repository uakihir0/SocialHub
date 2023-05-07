package net.socialhub.core.utils;

import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Context;

import java.util.Comparator;

/**
 * Mapper 補助関数
 */
public class MapperUtil {

    // ============================================================== //
    // Context
    // ============================================================== //

    /**
     * Sort Context
     */
    public static void sortContext(Context context) {
        context.getDescendants().sort(Comparator.comparing(Comment::getCreateAt).reversed());
        context.getAncestors().sort(Comparator.comparing(Comment::getCreateAt).reversed());
    }
}
