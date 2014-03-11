package ctr.stuba.xss.console.comm;

public class LoginFailedException extends ManagementClientException {

    public LoginFailedException(String message) {
        super(message);
    }
}
