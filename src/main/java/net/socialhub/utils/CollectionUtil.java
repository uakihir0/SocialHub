package net.socialhub.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

    /**
     * Get Partitioned List
     * リストを分割
     */
    public static <T> List<List<T>> partitionList(List<T> list, int length) {
        List<List<T>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if ((i % length) == 0) {
                results.add(new ArrayList<>());
            }
            results.get(results.size() - 1).add(list.get(i));
        }
        return results;
    }
}
