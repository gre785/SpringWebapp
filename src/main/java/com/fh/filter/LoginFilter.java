
package com.fh.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.fh.controller.base.BaseController;

public class LoginFilter
    extends BaseController
    implements Filter
{

    public void init(FilterConfig fc)
        throws ServletException
    {
        // FileUtil.createDir("d:/FH/topic/");
    }

    public void destroy()
    {}

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException
    {
        chain.doFilter(req, res);
    }

}
