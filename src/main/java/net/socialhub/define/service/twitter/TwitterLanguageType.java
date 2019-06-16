package net.socialhub.define.service.twitter;

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
