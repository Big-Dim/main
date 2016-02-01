
package com.saturn.testsaturn;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.NamingException;


/**
 *
 * @author DmitryP
 */
public class Login {
    private JsonObject json;
    private String login;
    private String type;
    private String password;
    private String token;
    private String msg;
    private String sequence_id;
    
    public Login(JsonObject json) {
        token = UUID.randomUUID().toString();
        msg = "DATABASE_ERROR";
        String data1="";
        String data2="";
        String errcod="0";
        try {
            this.json = json;
            this.type = json.getString("type");
            this.sequence_id = json.getString("sequence_id");
            JsonObject data = json.getJsonObject("data");
            this.login = data.getString("email");
            this.password = data.getString("password");
            
            DBconn_s db = new DBconn_s("rmlogin", login + "\t" + password + "\t" + token+ "\t" + type);

            db.getOutput();
            String out = db.out;
            for(int i=1; i < 4; i++){
                db.set(i);
                if(db == null) break;
                int rows = db.rowsize();
                if (rows > 0) {
                    msg = db.getString(rows - 1, 0);
                    if(msg.startsWith("MSG")){
                        msg = db.getString(rows - 1, 1);
                        data1 = db.getString(rows - 1, 2);
                        data2 = db.getString(rows - 1, 3);
                        break;
                    }else
                        continue;
                }
            }
            
            
        } catch (SQLException ex) {
            msg = ex.getMessage();
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            errcod="1";
        } catch (NamingException ex) {
            msg = ex.getMessage();
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            errcod="2";
        }
        
        if (msg.startsWith("CUSTOMER_")){// Успешная авторизация
            type ="CUSTOMER_API_TOKEN";
            this.setJson(type, "api_token", data1, "api_token_expiration_date", data2);
        } else{// Авторизация не прошла
            type ="CUSTOMER_ERROR";
            this.setJson(type, "ERROR_DESCRIPTION", this.msg, "ERROR_CODE", errcod);
        }

    }
    
    public JsonObject getJson() {
        System.out.println("Login getJson "+ json.toString());
        return json;
    }
    private void setJson(String type,String val1, String data1, String val2, String data2) {
        this.json = Json.createObjectBuilder()
                .add("type", type)
                .add("sequence_id", this.sequence_id)
                 .add("data", 
                     Json.createObjectBuilder().add(val1, data1)
                                               .add(val2, data2)
                                               .build()
                    )
                 .build();
        System.out.println("Object: " + this.json);
    }

    public void setJson(JsonObject json) {
        this.json = json;
        
    }
    
    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        Json.createWriter(writer).write(json);
        return writer.toString();
    }
}
