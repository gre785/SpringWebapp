
package com.fh.controller.base;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fh.entity.Page;
import com.fh.util.Logger;
import com.fh.util.PageData;
import com.fh.util.UuidUtil;

public class BaseController
{

    protected Logger logger = Logger.getLogger(this.getClass());

    private static final long serialVersionUID = 6357869213649815390L;

    public PageData getPageData()
    {
        return new PageData(this.getRequest());
    }

    public ModelAndView getModelAndView()
    {
        return new ModelAndView();
    }

    public HttpServletRequest getRequest()
    {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        return request;
    }

    public String get32UUID()
    {

        return UuidUtil.get32UUID();
    }

    public Page getPage()
    {

        return new Page();
    }

    public static void logBefore(Logger logger, String interfaceName)
    {
        logger.info("");
        logger.info("start");
        logger.info(interfaceName);
    }

    public static void logAfter(Logger logger)
    {
        logger.info("end");
        logger.info("");
    }

}
