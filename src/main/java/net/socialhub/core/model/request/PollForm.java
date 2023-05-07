package net.socialhub.core.model.request;

import java.util.ArrayList;
import java.util.List;

public class PollForm {

    private List<String> options = new ArrayList<>();

    private Boolean multiple = null;

    /** Expires in (min) */
    private Long expiresIn = 1440L;

    public PollForm addOption(String option) {
        options.add(option);
        return this;
    }

    public PollForm multiple(Boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    public PollForm expiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public List<String> getOptions() {
        return options;
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
