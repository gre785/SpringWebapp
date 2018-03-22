
package com.fh.controller.system.dictionaries;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.system.dictionaries.DictionariesService;
import com.fh.service.system.menu.MenuService;
import com.fh.util.AppUtil;
import com.fh.util.PageData;

/** 
 */
@Controller
@RequestMapping(value = "/dictionaries")
public class DictionariesController
    extends BaseController
{
    @Resource(name = "menuService")
    private MenuService _menuService;
    @Resource(name = "dictionariesService")
    private DictionariesService _dictionariesService;
    
    List<PageData> _szdList;

    @RequestMapping(value = "/save")
    public ModelAndView save(PrintWriter out)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        PageData pdp =  getPageData();
        String PARENT_ID = pd.getString("PARENT_ID");
        pdp.put("ZD_ID", PARENT_ID);

        if (null == pd.getString("ZD_ID") || "".equals(pd.getString("ZD_ID"))) {
            if (null != PARENT_ID && "0".equals(PARENT_ID)) {
                pd.put("JB", 1);
                pd.put("P_BM", pd.getString("BIANMA"));
            } else {
                pdp = _dictionariesService.findById(pdp);
                pd.put("JB", Integer.parseInt(pdp.get("JB").toString()) + 1);
                pd.put("P_BM", pdp.getString("BIANMA") + "_" + pd.getString("BIANMA"));
            }
            pd.put("ZD_ID", this.get32UUID());	// ID
            _dictionariesService.save(pd);
        } else {
            pdp = _dictionariesService.findById(pdp);
            if (null != PARENT_ID && "0".equals(PARENT_ID)) {
                pd.put("P_BM", pd.getString("BIANMA"));
            } else {
                pd.put("P_BM", pdp.getString("BIANMA") + "_" + pd.getString("BIANMA"));
            }

            _dictionariesService.edit(pd);
        }
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping
    public ModelAndView list(Page page)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        String PARENT_ID = pd.getString("PARENT_ID");
        if (null != PARENT_ID && !"".equals(PARENT_ID) && !"0".equals(PARENT_ID)) {

            PageData pdp = new PageData();
            pdp = this.getPageData();
            pdp.put("ZD_ID", PARENT_ID);
            pdp = _dictionariesService.findById(pdp);
            mv.addObject("pdp", pdp);

            _szdList = new ArrayList<PageData>();
            this.getZDname(PARENT_ID);
            Collections.reverse(_szdList);
        }

        String NAME = pd.getString("NAME");
        if (null != NAME && !"".equals(NAME)) {
            NAME = NAME.trim();
            pd.put("NAME", NAME);
        }
        page.setShowCount(5);
        page.setPd(pd);
        List<PageData> varList = _dictionariesService.dictlistPage(page);

        mv.setViewName("system/dictionaries/zd_list");
        mv.addObject("varList", varList);
        mv.addObject("varsList", _szdList);
        mv.addObject("pd", pd);
        return mv;
    }

    public void getZDname(String PARENT_ID)
    {
        logBefore(logger, "递归");
        try {
            PageData pdps = getPageData();
            pdps.put("ZD_ID", PARENT_ID);
            pdps = _dictionariesService.findById(pdps);
            if (pdps != null) {
                _szdList.add(pdps);
                String PARENT_IDs = pdps.getString("PARENT_ID");
                this.getZDname(PARENT_IDs);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd(Page page)
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            mv.setViewName("system/dictionaries/zd_edit");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/toEdit")
    public ModelAndView toEdit(String ROLE_ID)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        pd = _dictionariesService.findById(pd);
        if (Integer.parseInt(_dictionariesService.findCount(pd).get("ZS").toString()) != 0) {
            mv.addObject("msg", "no");
        } else {
            mv.addObject("msg", "ok");
        }
        mv.setViewName("system/dictionaries/zd_edit");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/has")
    public void has(PrintWriter out)
    {
        PageData pd = getPageData();
        try {
            if (_dictionariesService.findBmCount(pd) != null) {
                out.write("error");
            } else {
                out.write("success");
            }
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @RequestMapping(value = "/del")
    @ResponseBody
    public Object del()
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = getPageData();
        String errInfo = "";
        try {
            if (Integer.parseInt(_dictionariesService.findCount(pd).get("ZS").toString()) != 0) {
                errInfo = "false";
            } else {
                _dictionariesService.delete(pd);
                errInfo = "success";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }
}
