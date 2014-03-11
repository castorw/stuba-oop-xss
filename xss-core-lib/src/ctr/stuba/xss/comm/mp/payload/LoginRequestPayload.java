package ctr.stuba.xss.comm.mp.payload;

/**
 * Login request payload.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class LoginRequestPayload extends Payload {

    protected final String username;
    protected final String password;

    public LoginRequestPayload(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
