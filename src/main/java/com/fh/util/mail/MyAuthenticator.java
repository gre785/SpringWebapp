
package com.fh.util.mail;

/**   
 *  
 */
import javax.mail.*;

public class MyAuthenticator
    extends Authenticator
{
    String _userName = null;
    String _password = null;

    public MyAuthenticator()
    {}

    public MyAuthenticator(String username, String password)
    {
        _userName = username;
        _password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(_userName, _password);
    }
}
