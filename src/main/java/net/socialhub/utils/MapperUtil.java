package net.socialhub.utils;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.paging.BorderPaging;

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

        } else {
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
    }
}
