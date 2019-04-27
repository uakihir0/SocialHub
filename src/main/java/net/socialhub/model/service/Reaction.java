package net.socialhub.model.service;

/**
 * Reaction Model
 */
public class Reaction {

    private String name;

    private Long count;

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    //endregion
}
