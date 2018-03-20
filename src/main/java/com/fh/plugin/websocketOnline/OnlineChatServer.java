
package com.fh.plugin.websocketOnline;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import net.sf.json.JSONObject;

/**
 * 
 */
public class OnlineChatServer
    extends WebSocketServer
{
    private static int _port = 8887;

    public OnlineChatServer(int port) throws UnknownHostException
    {
        super(new InetSocketAddress(port));
    }

    public OnlineChatServer(InetSocketAddress address)
    {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        // this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        userLeave(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        message = message.toString();
        if (null != message && message.startsWith("[join]")) {
            this.userjoin(message.replaceFirst("\\[join\\]", ""), conn);
        }
        if (null != message && message.startsWith("[goOut]")) {
            this.goOut(message.replaceFirst("\\[goOut\\]", ""));
        } else if (null != message && message.startsWith("[leave]")) {
            this.userLeave(conn);
        } else if (null != message && message.startsWith("[count]")) {
            this.getUserCount(conn);
        } else if (null != message && message.startsWith("[getUserlist]")) {
            this.getUserList(conn);
        } else {
            OnlineChatServerPool.sendMessageToUser(conn, message);
        }
    }

    public void onFragment(WebSocket conn, Framedata fragment)
    {}

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        // ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    public void userjoin(String user, WebSocket conn)
    {
        if (null == OnlineChatServerPool.getWebSocketByUser(user)) {
            OnlineChatServerPool.addUser(user, conn);
        } else {
            goOut(conn, "goOut");
        }
    }

    public void goOut(String user)
    {
        this.goOut(OnlineChatServerPool.getWebSocketByUser(user), "thegoout");
    }

    public void goOut(WebSocket conn, String type)
    {
        JSONObject result = new JSONObject();
        result.element("type", type);
        result.element("msg", "goOut");
        OnlineChatServerPool.sendMessageToUser(conn, result.toString());
    }

    public void userLeave(WebSocket conn)
    {
        OnlineChatServerPool.removeUser(conn);
    }

    public void getUserCount(WebSocket conn)
    {
        JSONObject result = new JSONObject();
        result.element("type", "count");
        result.element("msg", OnlineChatServerPool.getUserCount());
        OnlineChatServerPool.sendMessageToUser(conn, result.toString());
    }

    public void getUserList(WebSocket conn)
    {
        JSONObject result = new JSONObject();
        result.element("type", "userlist");
        result.element("list", OnlineChatServerPool.getOnlineUser());
        OnlineChatServerPool.sendMessageToUser(conn, result.toString());
    }

    public static void main(String[] args)
        throws InterruptedException, IOException
    {
        WebSocketImpl.DEBUG = false;
        (new OnlineChatServer(_port)).start();
    }
}
