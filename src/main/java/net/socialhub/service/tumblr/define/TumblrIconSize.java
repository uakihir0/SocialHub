package net.socialhub.service.tumblr.define;

public enum TumblrIconSize {

    S512(512);

    private Integer size;

    TumblrIconSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }
}
