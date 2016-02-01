/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saturn.testsaturn;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author DmitryP
 */
@ServerEndpoint(value="/myendpoint", encoders = {LoginEncoder.class}, decoders = {LoginDecoder.class})

public class MyWSEndpoint {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void onMessage(Login login, Session session) {
        System.out.println("Получено сообщение от клиента:" + login.getJson().toString());
        try {
            //login.setSess(session.getId());
            session.getBasicRemote().sendObject(login);
            
        } catch (IOException | EncodeException ex) {
            ex.printStackTrace();
        }

    }

    
 /*    public String myendpoint(String message) {
        
        return "*** return *****";
    }

   public void broadcastlogin(Login login, Session session) throws IOException, EncodeException {
        System.out.println("broadcastLogin: " + login);
        for (Session peer : peers) {
            if (!peer.equals(session)) {
                peer.getBasicRemote().sendObject(login);
            }
        }
    }*/

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);    
    }

     
}
