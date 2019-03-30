package net.socialhub.define.service;

public enum TwitterIconSizeEnum {

    /** 24x24 */
    Mini("_mini"),

    /** 48x48 */
    Normal("_normal"),

    /** 73x73 */
    Bigger("_bigger"),

    /** 200x200 */
    W200H200("_200x200"),

    /** 400x400 */
    W400H400("_400x400"),

    Original("");

    private String suffix;

    TwitterIconSizeEnum(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
