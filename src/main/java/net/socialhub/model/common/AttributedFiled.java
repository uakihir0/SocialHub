package net.socialhub.model.common;

/**
 * 追加フィールド
 * Extra Fields
 */
public class AttributedFiled {

    private String name;
    private AttributedString value;

    public AttributedFiled() {
    }

    public AttributedFiled(String name, AttributedString value) {
        this.value = value;
        this.name = name;
    }

    public AttributedFiled(String name, String value) {
        this.value = new AttributedString(value);
        this.name = name;
    }

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributedString getValue() {
        return value;
    }

    public void setValue(AttributedString value) {
        this.value = value;
    }
    //endregion
}
