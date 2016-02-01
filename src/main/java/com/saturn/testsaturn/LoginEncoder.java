
package com.saturn.testsaturn;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author DmitryP
 */
public class LoginEncoder implements Encoder.Text<Login>{

    @Override
    public String encode(Login login) throws EncodeException {
        System.out.println("encode string - "+ login.getJson().toString());
        return login.getJson().toString();
    }

    @Override
    public void init(EndpointConfig config) {
        System.out.println("encoder init");
    }

    @Override
    public void destroy() {
        System.out.println("encoder destroy");
    }
    
}
