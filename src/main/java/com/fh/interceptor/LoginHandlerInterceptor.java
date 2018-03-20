
package com.fh.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fh.entity.system.User;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;

/**
 * 
 */
public class LoginHandlerInterceptor
    extends HandlerInterceptorAdapter
{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception
    {
        String path = request.getServletPath();
        if (path.matches(Const.NO_INTERCEPTOR_PATH)) {
            return true;
        }

        // shiro session
        Session session = SecurityUtils.getSubject().getSession();
        User user = (User)session.getAttribute(Const.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + Const.LOGIN);
            return false;
        }
        path = path.substring(1, path.length());
        boolean isHandled = Jurisdiction.hasJurisdiction(path);
        if (!isHandled) {
            response.sendRedirect(request.getContextPath() + Const.LOGIN);
        }
        return isHandled;
    }

}
