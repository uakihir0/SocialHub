package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Paging with max and since
 * Max Since 管理のページング
 * (Twitter, Mastodon)
 */
public class BorderPaging extends Paging {

    /** Max Id */
    private Long maxId;
    private Boolean maxInclude = true;

    /** Since Id */
    private Long sinceId;
    private Boolean sinceInclude = false;

    /** Hint to next paging */
    private Boolean hintNewer = false;

    /**
     * ID のスキップ単位
     * Mastodon のタイムラインに於いて MaxID を指定した場合
     * (MaxID + 1) の ID のコメントが取得出来ないので追加
     * TODO: Mastodon の実装を眺めて実装を確認する
     */
    private Long idUnit = 1L;

    /** Cannot Access */
    private BorderPaging() {

    }

    /**
     * for Twitter Timeline
     */
    public static BorderPaging twitter() {
        BorderPaging pg = new BorderPaging();
        pg.setMaxInclude(true);
        pg.setSinceInclude(false);
        pg.setIdUnit(1L);
        return pg;
    }

    /**
     * for Mastodon Timeline
     */
    public static BorderPaging mastodon() {
        BorderPaging pg = new BorderPaging();
        pg.setMaxInclude(false);
        pg.setSinceInclude(false);
        pg.setIdUnit(4L);
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        BorderPaging newPage = new BorderPaging();
        newPage.setSinceInclude(getSinceInclude());
        newPage.setMaxInclude(getMaxInclude());
        newPage.setIdUnit(getIdUnit());
        newPage.setCount(getCount());
        newPage.setHintNewer(true);

        if (entities != null && !entities.isEmpty()) {

            Long offset = sinceInclude ? 1L : 0L;
            Long id = (Long) entities.get(0).getId();
            newPage.setSinceId(id + (offset * idUnit));
            return newPage;

            // [offset]
            // m  s
            // in in +1
            // ex in +1
            // in ex 0
            // ex ex 0

        } else {
            if (getMaxId() != null) {

                Long offset = (-1 + (sinceInclude ? 1L : 0L) + (maxInclude ? 1L : 0L));
                newPage.setSinceId(getMaxId() + (offset * idUnit));
                return newPage;

                // [offset]
                // m  s
                // in in +1
                // ex in 0
                // in ex 0
                // ex ex -1
            }
            return this.copy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        BorderPaging newPage = new BorderPaging();
        newPage.setSinceInclude(getSinceInclude());
        newPage.setMaxInclude(getMaxInclude());
        newPage.setIdUnit(getIdUnit());
        newPage.setCount(getCount());

        if (entities != null && !entities.isEmpty()) {

            Long offset = maxInclude ? -1L : 0L;
            T last = entities.get(entities.size() - 1);
            Long id = (Long) last.getId();
            newPage.setMaxId(id + (offset * idUnit));
            return newPage;

            // [offset]
            // m  s
            // in in -1
            // in ex -1
            // ex in 0
            // ex ex 0

        } else {
            if (getSinceId() != null) {

                Long offset = (1 + (maxInclude ? -1L : 0L) + (sinceInclude ? -1L : 0L));
                newPage.setMaxId(getSinceId() + (offset * idUnit));
                return newPage;

                // [offset]
                // m  s
                // in in -1
                // in ex 0
                // ex in 0
                // ex ex +1
            }
            return this.copy();
        }
    }

    /**
     * オブジェクトコピー
     */
    public BorderPaging copy() {
        BorderPaging pg = new BorderPaging();
        pg.setCount(getCount());
        pg.setMaxId(getMaxId());
        pg.setSinceId(getSinceId());
        pg.setMaxInclude(getMaxInclude());
        pg.setSinceInclude(getSinceInclude());
        pg.setIdUnit(getIdUnit());
        pg.setHasMore(getHasMore());
        pg.setHintNewer(getHintNewer());
        return pg;
    }

    //region // Getter&Setter
    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }

    public Boolean getMaxInclude() {
        return maxInclude;
    }

    public void setMaxInclude(Boolean maxInclude) {
        this.maxInclude = maxInclude;
    }

    public Boolean getSinceInclude() {
        return sinceInclude;
    }

    public void setSinceInclude(Boolean sinceInclude) {
        this.sinceInclude = sinceInclude;
    }

    public Long getIdUnit() {
        return idUnit;
    }

    public void setIdUnit(Long idUnit) {
        this.idUnit = idUnit;
    }

    public Boolean getHintNewer() {
        return hintNewer;
    }

    public void setHintNewer(Boolean hintNewer) {
        this.hintNewer = hintNewer;
    }
    //endregion
}
