
package com.fh.plugin.websocketOnline;

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
public class OnlineChatServerPool
{

    private static final Map<WebSocket, String> _userconnections = new HashMap<WebSocket, String>();

    public static String getUserByKey(WebSocket conn)
    {
        return _userconnections.get(conn);
    }

    public static int getUserCount()
    {
        return _userconnections.size();
    }

    public static WebSocket getWebSocketByUser(String user)
    {
        Set<WebSocket> keySet = _userconnections.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String cuser = _userconnections.get(conn);
                if (cuser.equals(user)) {
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
        for (String u :  _userconnections.values()) {
            setUsers.add(u);
        }
        return setUsers;
    }

    public static boolean removeUser(WebSocket conn)
    {
        if (_userconnections.containsKey(conn)) {
            _userconnections.remove(conn);
            return true;
        } else {
            return false;
        }
    }

    public static void sendMessageToUser(WebSocket conn, String message)
    {
        if (null != conn) {
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
