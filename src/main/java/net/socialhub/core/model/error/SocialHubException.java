package net.socialhub.core.model.error;

public class SocialHubException extends RuntimeException {

    /**
     * Error detail
     * エラー情報をまとめたものを設定
     */
    private SocialHubError error;

    public SocialHubException() {
        super();
    }

    public SocialHubException(String message) {
        super(message);
    }

    public SocialHubException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocialHubException(Throwable cause) {
        super(cause);
    }

    // region
    public SocialHubError getError() {
        return error;
    }

    public void setError(SocialHubError error) {
        this.error = error;
    }
    // endregion
}
