
package com.fh.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fh.util.Const;

/**
 * 
 */
public class WebAppContextListener
    implements ServletContextListener
{

    public void contextDestroyed(ServletContextEvent event)
    {
        // TODO Auto-generated method stub
    }

    public void contextInitialized(ServletContextEvent event)
    {
        Const.WEB_APP_CONTEXT = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
    }
}
