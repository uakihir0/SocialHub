package net.socialhub.model.error;

public class SocialHubException extends RuntimeException {

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

}
