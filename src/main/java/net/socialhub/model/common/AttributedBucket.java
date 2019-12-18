package net.socialhub.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributedBucket implements AttributedElement {

    private AttributedKind kind;

    private Map<String, String> params = new HashMap<>();

    private List<AttributedElement> children = new ArrayList<>();

    // region
    @Override
    public AttributedKind getKind() {
        return kind;
    }

    public void setKind(AttributedKind kind) {
        this.kind = kind;
    }

    @Override
    public String getDisplayText() {
        return this.children.stream()
                .map(AttributedElement::getDisplayText)
                .collect(Collectors.joining());
    }

    @Override
    public String getExpandedText() {
        return null;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public List<AttributedElement> getChildren() {
        return children;
    }

    public void setChildren(List<AttributedElement> children) {
        this.children = children;
    }
    // endregion
}
