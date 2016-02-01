
package com.saturn.testsaturn;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author DmitryP
 */
public class LoginDecoder implements Decoder.Text<Login> {

    @Override
    public Login decode(String str) throws DecodeException {
        JsonObject jsonObject = Json.createReader(new StringReader(str)).readObject();
        return  new Login(jsonObject);

    }

    @Override
    public boolean willDecode(String str) {
        try {
            Json.createReader(new StringReader(str)).readObject();
            System.out.println("decode string - "+ str);
            return true;
        } catch (JsonException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
        System.out.println("decoder init");
    }

    @Override
    public void destroy() {
        System.out.println("decoder destroy");
    }

}
