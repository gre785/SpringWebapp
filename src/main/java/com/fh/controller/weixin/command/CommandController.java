
package com.fh.controller.weixin.command;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.util.AppUtil;
import com.fh.util.ObjectExcelView;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Tools;
import com.fh.util.Jurisdiction;
import com.fh.service.weixin.command.CommandService;

@Controller
@RequestMapping(value = "/command")
public class CommandController
    extends BaseController
{

    private static final String MENU_URL = "command/list.do";
    @Resource(name = "commandService")
    private CommandService _commandService;

    @RequestMapping(value = "/save")
    public ModelAndView save()
        throws Exception
    {
        logBefore(logger, "add Command");
        if (!Jurisdiction.buttonJurisdiction(MENU_URL, "add")) {
            return null;
        }
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        pd.put("COMMAND_ID", this.get32UUID());
        pd.put("CREATETIME", Tools.date2Str(new Date()));
        _commandService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out)
    {
        logBefore(logger, "delete Command");
        if (!Jurisdiction.buttonJurisdiction(MENU_URL, "del")) {
            return;
        }
        PageData pd = getPageData();
        try {
            _commandService.delete(pd);
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
        logBefore(logger, "update Command");
        if (!Jurisdiction.buttonJurisdiction(MENU_URL, "edit")) {
            return null;
        }
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        _commandService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/list")
    public ModelAndView list(Page page)
    {
        logBefore(logger, "list Command");
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            String KEYWORD = pd.getString("KEYWORD");
            if (null != KEYWORD && !"".equals(KEYWORD)) {
                pd.put("KEYWORD", KEYWORD.trim());
            }
            page.setPd(pd);
            List<PageData> varList = _commandService.list(page);
            mv.setViewName("weixin/command/command_list");
            mv.addObject("varList", varList);
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
        logBefore(logger, "to generated Command");
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            mv.setViewName("weixin/command/command_edit");
            mv.addObject("msg", "save");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit()
    {
        logBefore(logger, "go to generated Command");
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            pd = _commandService.findById(pd);
            mv.setViewName("weixin/command/command_edit");
            mv.addObject("msg", "edit");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll()
    {
        logBefore(logger, "batch delete Command");
        if (!Jurisdiction.buttonJurisdiction(MENU_URL, "dell")) {
            return null;
        }
        PageData pd = getPageData();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<PageData> pdList = new ArrayList<PageData>();
            String DATA_IDS = pd.getString("DATA_IDS");
            if (null != DATA_IDS && !"".equals(DATA_IDS)) {
                String ArrayDATA_IDS[] = DATA_IDS.split(",");
                _commandService.deleteAll(ArrayDATA_IDS);
                pd.put("msg", "ok");
            } else {
                pd.put("msg", "no");
            }
            pdList.add(pd);
            map.put("list", pdList);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        return AppUtil.returnObject(pd, map);
    }

    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel()
    {
        logBefore(logger, "export to excel");
        if (!Jurisdiction.buttonJurisdiction(MENU_URL, "cha")) {
            return null;
        }
        ModelAndView mv = new ModelAndView();
        PageData pd = getPageData();
        try {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            List<String> titles = new ArrayList<String>();
            titles.add("关键词");	// 1
            titles.add("应用路径");	// 2
            titles.add("创建时间");	// 3
            titles.add("状态");	// 4
            titles.add("备注");	// 5
            dataMap.put("titles", titles);
            List<PageData> varOList = _commandService.listAll(pd);
            List<PageData> varList = new ArrayList<PageData>();
            for (int i = 0; i < varOList.size(); i++) {
                PageData vpd = new PageData();
                vpd.put("var1", varOList.get(i).getString("KEYWORD"));	// 1
                vpd.put("var2", varOList.get(i).getString("COMMANDCODE"));	// 2
                vpd.put("var3", varOList.get(i).getString("CREATETIME"));	// 3
                vpd.put("var4", varOList.get(i).get("STATUS").toString());	// 4
                vpd.put("var5", varOList.get(i).getString("BZ"));	// 5
                varList.add(vpd);
            }
            dataMap.put("varList", varList);
            ObjectExcelView erv = new ObjectExcelView();
            mv = new ModelAndView(erv, dataMap);
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
