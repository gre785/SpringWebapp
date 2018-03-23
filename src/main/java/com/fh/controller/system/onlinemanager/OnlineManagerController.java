
package com.fh.controller.system.onlinemanager;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Jurisdiction;

@Controller
@RequestMapping(value = "/onlinemanager")
public class OnlineManagerController
    extends BaseController
{
    private static final String _menuUrl = "onlinemanager/list.do";

    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out)
    {
        logBefore(logger, "delete OnlineManager");
        if (!Jurisdiction.buttonJurisdiction(_menuUrl, "del")) {
            return;
        }
        try {
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @RequestMapping(value = "/edit")
    public ModelAndView edit()
        throws Exception
    {
        logBefore(logger, "update OnlineManager");
        if (!Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
            return null;
        }
        ModelAndView mv = getModelAndView();
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/list")
    public ModelAndView list(Page page)
    {
        logBefore(logger, "find OnlineManager");
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            page.setPd(pd);
            mv.setViewName("system/onlinemanager/onlinemanager_list");
            mv.addObject("pd", pd);
            mv.addObject(Const.SESSION_QX, this.getHC());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd()
    {
        logBefore(logger, "go to generated OnlineManager");
        ModelAndView mv = this.getModelAndView();
        PageData pd = getPageData();
        try {
            mv.setViewName("system/onlinemanager/onlinemanager_edit");
            mv.addObject("msg", "save");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getHC()
    {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        return (Map<String, String>)session.getAttribute(Const.SESSION_QX);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
