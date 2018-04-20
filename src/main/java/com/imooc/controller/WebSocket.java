package com.imooc.controller;

import lombok.extern.slf4j.Slf4j;

//@Component
//@ServerEndpoint("/webSocket")
@Slf4j
public class WebSocket {

    /*private Session session;

    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();

    @OnOpen
    public void opOpen(Session session){
        this.session = session;
        webSockets.add(this);
        log.info("【websocket消息】有新的连接，总数：{}",webSockets.size());
    }

    @OnClose
    public void onClose(){
        webSockets.remove(this);
        log.info("【websocket消息】连接断开，总数：{}",webSockets.size());
    }

    @OnMessage
    public void onMessage(String message){
        log.info("【websocket消息】收到客户端发来的消息：{}", message);
    }

    public void sendMessage(String message){
        for (WebSocket webSocket:webSockets){
            log.info("【websocket消息】广播消息，message={}",message);
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

}
