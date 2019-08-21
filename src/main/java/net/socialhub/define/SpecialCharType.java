package net.socialhub.define;

/**
 * Special char for xml
 * XHTML 内の特殊文字定義
 *
 * @link http://e-words.jp/p/r-htmlentity.html
 */
public enum SpecialCharType {

    nbsp("&nbsp;", "&#160;"), //
    lsquo("&lsquo;", "&#8216;"), //
    squo("&rsquo;", "&#8217;"), //
    ldquo("&ldquo;", "&#8220;"), //
    rdquo("&rdquo;", "&#8221;"), //
    hellip("&hellip;", "&#8230;"), //
    ;

    private String entityRepl;
    private String numberRepl;

    SpecialCharType(String entityRepl, String numberRepl) {
        this.entityRepl = entityRepl;
        this.numberRepl = numberRepl;
    }

    public String getEntityRepl() {
        return entityRepl;
    }

    public String getNumberRepl() {
        return numberRepl;
    }
}
