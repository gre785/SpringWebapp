
package com.fh.controller.system.menu;

import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.system.Menu;
import com.fh.service.system.menu.MenuService;
import com.fh.util.PageData;


@Controller
@RequestMapping(value = "/menu")
public class MenuController
    extends BaseController
{
    @Resource(name = "menuService")
    private MenuService _menuService;

    @RequestMapping
    public ModelAndView list()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        try {
            List<Menu> menuList = _menuService.listAllParentMenu();
            mv.addObject("menuList", menuList);
            mv.setViewName("system/menu/menu_list");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        try {
            List<Menu> menuList = _menuService.listAllParentMenu();
            mv.addObject("menuList", menuList);
            mv.setViewName("system/menu/menu_add");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/add")
    public ModelAndView add(Menu menu)
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = getPageData();
        try {
            menu.setMENU_ID(String.valueOf(Integer.parseInt(_menuService.findMaxId(pd).get("MID").toString()) + 1));

            String PARENT_ID = menu.getPARENT_ID();
            if (!"0".equals(PARENT_ID)) {
                pd.put("MENU_ID", PARENT_ID);
                pd = _menuService.getMenuById(pd);
                menu.setMENU_TYPE(pd.getString("MENU_TYPE"));
            }
            _menuService.saveMenu(menu);
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;

    }

    @RequestMapping(value = "/toEdit")
    public ModelAndView toEdit(String MENU_ID)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = new PageData();
        try {
            pd = this.getPageData();
            pd.put("MENU_ID", MENU_ID);
            pd = _menuService.getMenuById(pd);
            List<Menu> menuList = _menuService.listAllParentMenu();
            mv.addObject("menuList", menuList);
            mv.addObject("pd", pd);
            mv.setViewName("system/menu/menu_edit");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/toEditicon")
    public ModelAndView toEditicon(String MENU_ID)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = new PageData();
        try {
            pd = this.getPageData();
            pd.put("MENU_ID", MENU_ID);
            mv.addObject("pd", pd);
            mv.setViewName("system/menu/menu_icon");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/editicon")
    public ModelAndView editicon()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = new PageData();
        try {
            pd = this.getPageData();
            pd = _menuService.editicon(pd);
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/edit")
    public ModelAndView edit()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = new PageData();
        try {
            pd = this.getPageData();

            String PARENT_ID = pd.getString("PARENT_ID");
            if (null == PARENT_ID || "".equals(PARENT_ID)) {
                PARENT_ID = "0";
                pd.put("PARENT_ID", PARENT_ID);
            }
            if ("0".equals(PARENT_ID)) {
                _menuService.editType(pd);
            }
            pd = _menuService.edit(pd);
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/sub")
    public void getSub(@RequestParam String MENU_ID, HttpServletResponse response)
        throws Exception
    {
        try {
            List<Menu> subMenu = _menuService.listSubMenuByParentId(MENU_ID);
            JSONArray arr = JSONArray.fromObject(subMenu);
            PrintWriter out;
            response.setCharacterEncoding("utf-8");
            out = response.getWriter();
            String json = arr.toString();
            out.write(json);
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @RequestMapping(value = "/del")
    public void delete(@RequestParam String MENU_ID, PrintWriter out)
        throws Exception
    {
        try {
            _menuService.deleteMenuById(MENU_ID);
            out.write("success");
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }
}
