package net.socialhub.utils;

import net.socialhub.define.ServiceType;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.DatePaging;

/**
 * Mapper 補助関数
 */
public class MapperUtil {

    /** BorderPaging の作成 */
    public static BorderPaging mappingBorderPaging(Paging paging, ServiceType service) {
        if (paging instanceof BorderPaging) {
            return ((BorderPaging) paging).copy();
        }

        // Count の取得
        BorderPaging pg = makeBorderPagingByServiceType(service);
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }

        return pg;
    }

    private static BorderPaging makeBorderPagingByServiceType(ServiceType service) {
        if (service == ServiceType.Twitter) {
            return BorderPaging.twitter();
        }
        if (service == ServiceType.Mastodon) {
            return BorderPaging.mastodon();
        }
        throw new IllegalStateException();
    }

    /** DatePaging の作成 */
    public static DatePaging mappingDatePaging(Paging paging) {
        if (paging instanceof DatePaging) {
            return ((DatePaging) paging).copy();
        }

        // Count の取得
        DatePaging pg = new DatePaging();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }

        return pg;
    }
}
