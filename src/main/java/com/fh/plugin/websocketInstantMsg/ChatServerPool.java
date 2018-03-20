
package com.fh.plugin.websocketInstantMsg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;

/**
 * 
 */
public class ChatServerPool
{

    private static final Map<WebSocket, String> _userconnections = new HashMap<WebSocket, String>();

    public static String getUserByKey(WebSocket conn)
    {
        return _userconnections.get(conn);
    }

    public static WebSocket getWebSocketByUser(String user)
    {
        Set<WebSocket> keySet = _userconnections.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                if (_userconnections.get(conn).equals(user)) {
                    return conn;
                }
            }
        }
        return null;
    }

    public static void addUser(String user, WebSocket conn)
    {
        _userconnections.put(conn, user);
    }

    public static Collection<String> getOnlineUser()
    {
        List<String> setUsers = new ArrayList<String>();
        for (String user : _userconnections.values()) {
            setUsers.add("<a onclick=\"toUserMsg('" + user + "');\">" + user + "</a>");
        }
        return setUsers;
    }

    public static boolean removeUser(WebSocket conn)
    {
        if (!_userconnections.containsKey(conn)) {
            return false;
        }
        _userconnections.remove(conn);
        return true;
    }

    public static void sendMessageToUser(WebSocket conn, String message)
    {
        if (null != conn && null != _userconnections.get(conn)) {
            conn.send(message);
        }
    }

    public static void sendMessage(String message)
    {
        Set<WebSocket> keySet = _userconnections.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String user = _userconnections.get(conn);
                if (user != null) {
                    conn.send(message);
                }
            }
        }
    }
}
