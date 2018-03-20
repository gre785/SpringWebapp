
package com.fh.filter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.java_websocket.WebSocketImpl;

import com.fh.plugin.websocketInstantMsg.ChatServer;
import com.fh.plugin.websocketOnline.OnlineChatServer;
import com.fh.controller.base.BaseController;

public class startFilter
    extends BaseController
    implements Filter
{
    int _instantPort = 8887;
    int _onlinePort = 8889;

    public void init(FilterConfig config)
        throws ServletException
    {
        startWebsocketInstantMsg();
        startWebsocketOnline();
    }

    public void startWebsocketInstantMsg()
    {
        WebSocketImpl.DEBUG = false;
        try {
            ChatServer server = new ChatServer(_instantPort);
            server.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void startWebsocketOnline()
    {
        WebSocketImpl.DEBUG = false;
        try {
            OnlineChatServer server = new OnlineChatServer(_onlinePort);
            server.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void timer()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                // PersonService personService = (PersonService)ApplicationContext.getBean("personService");
            }
        }, time, 1000 * 60 * 60 * 24);
    }

    public void destroy()
    {
        // TODO Auto-generated method stub
    }

    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
        throws IOException, ServletException
    {
        // TODO Auto-generated method stub
    }

}
