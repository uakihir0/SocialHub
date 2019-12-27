package net.socialhub.utils;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;

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
