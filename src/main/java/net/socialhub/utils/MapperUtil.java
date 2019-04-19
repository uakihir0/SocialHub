package net.socialhub.utils;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.DatePaging;

import java.util.List;

/**
 * Mapper 補助関数
 */
public class MapperUtil {

    /** BorderPaging の作成 */
    public static BorderPaging mappingBorderPaging(Pageable<?> model, Paging paging) {
        List<? extends Identify> entities = model.getEntities();

        if (paging instanceof BorderPaging) {
            return (BorderPaging) paging;

        }

        BorderPaging pg = new BorderPaging();
        int count = entities.size();

        // Count の取得
        pg.setCount((long) count);
        if ((paging != null) && (paging.getCount() > 0L)) {
            pg.setCount(paging.getCount());
        }

        // MaxID SinceID の取得
        pg.setMaxId((long) entities.get(0).getId());
        pg.setSinceId((long) entities.get(count - 1).getId() - 1);

        return pg;
    }

    /** DatePaging の作成 */
    public static DatePaging mappingDatePaging(Pageable<Comment> model, Paging paging) {
        List<Comment> entities = model.getEntities();

        if (entities == null || entities.isEmpty()) {
            DatePaging pg = new DatePaging();
            paging.setHasMore(false);
            pg.setCount(0L);
            return pg;
        }

        if (paging instanceof DatePaging) {
            return (DatePaging) paging;
        }

        DatePaging pg = new DatePaging();
        int count = entities.size();

        // Count の取得
        pg.setCount((long) count);
        if ((paging != null) && (paging.getCount() > 0L)) {
            pg.setCount(paging.getCount());
        }

        // Latest Oldest の設定
        pg.setLatest(entities.get(0).getCreateAt());
        pg.setOldest(entities.get(count - 1).getCreateAt());
        pg.setInclusive(true);

        return pg;
    }
}
