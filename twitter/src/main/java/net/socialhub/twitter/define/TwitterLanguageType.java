package net.socialhub.twitter.define;

public enum TwitterLanguageType {

    Ja("ja"),
    En("en"),
    ;

    private String code;

    TwitterLanguageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
