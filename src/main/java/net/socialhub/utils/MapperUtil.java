package net.socialhub.utils;

import net.socialhub.define.ServiceType;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.addition.tumblr.TumblrPaging;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.DatePaging;
import net.socialhub.model.service.paging.IndexPaging;

/**
 * Mapper 補助関数
 */
public class MapperUtil {

    /**
     * BorderPaging の作成
     */
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

    /**
     * DatePaging の作成
     */
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

    /**
     * TumblrPaging の作成
     */
    public static TumblrPaging mappingTumblrPaging(Paging paging) {
        if (paging instanceof TumblrPaging) {
            return ((TumblrPaging) paging).copy();
        }

        // Count の取得
        TumblrPaging pg = new TumblrPaging();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }

        return pg;
    }

    /**
     * IndexPaging の作成
     */
    public static IndexPaging mappingIndexPaging(Paging paging) {
        if (paging instanceof IndexPaging) {
            return ((IndexPaging) paging).copy();
        }

        // Count の取得
        IndexPaging pg = new IndexPaging();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }

        return pg;
    }

    /**
     * CursorPaging の作成
     */
    public static <T> CursorPaging<T> mappingCursorPaging(Paging paging) {

        // Count の取得
        CursorPaging<T> pg = new CursorPaging<>();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }
        return pg;
    }
}
