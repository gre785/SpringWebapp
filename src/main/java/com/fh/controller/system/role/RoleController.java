
package com.fh.controller.system.role;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.entity.system.Menu;
import com.fh.entity.system.Role;
import com.fh.service.system.menu.MenuService;
import com.fh.service.system.role.RoleService;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;
import com.fh.util.RightsHelper;
import com.fh.util.Tools;

@Controller
@RequestMapping(value = "/role")
public class RoleController
    extends BaseController
{
    private static final String _menuUrl = "role.do";
    @Resource(name = "menuService")
    private MenuService _menuService;
    @Resource(name = "roleService")
    private RoleService _roleService;

    @RequestMapping(value = "/qx")
    public ModelAndView qx()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            String msg = pd.getString("msg");
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
                _roleService.updateQx(msg, pd);
            }
            mv.setViewName("save_result");
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/kfqx")
    public ModelAndView kfqx()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            String msg = pd.getString("msg");
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
                _roleService.updateKFQx(msg, pd);
            }
            mv.setViewName("save_result");
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/gysqxc")
    public ModelAndView gysqxc()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = getPageData();
        try {
            String msg = pd.getString("msg");
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
                _roleService.gysqxc(msg, pd);
            }
            mv.setViewName("save_result");
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping
    public ModelAndView list(Page page)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();

        String roleId = pd.getString("ROLE_ID");
        if (roleId == null || "".equals(roleId)) {
            pd.put("ROLE_ID", "1");
        }
        List<Role> roleList = _roleService.listAllRoles();
        List<Role> roleList_z = _roleService.listAllRolesByPId(pd);
        List<PageData> kefuqxlist = _roleService.listAllkefu(pd);
        List<PageData> gysqxlist = _roleService.listAllGysQX(pd);
        pd = _roleService.findObjectById(pd);
        mv.addObject("pd", pd);
        mv.addObject("kefuqxlist", kefuqxlist);
        mv.addObject("gysqxlist", gysqxlist);
        mv.addObject("roleList", roleList);
        mv.addObject("roleList_z", roleList_z);
        mv.setViewName("system/role/role_list");
        mv.addObject(Const.SESSION_QX, this.getHC());

        return mv;
    }

    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd(Page page)
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            mv.setViewName("system/role/role_add");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            String parent_id = pd.getString("PARENT_ID");
            pd.put("ROLE_ID", parent_id);
            if ("0".equals(parent_id)) {
                pd.put("RIGHTS", "");
            } else {
                String rights = _roleService.findObjectById(pd).getString("RIGHTS");
                pd.put("RIGHTS", (null == rights) ? "" : rights);
            }
            pd.put("QX_ID", "");
            String UUID = this.get32UUID();

            pd.put("GL_ID", UUID);
            pd.put("FX_QX", 0);
            pd.put("FW_QX", 0);	
            pd.put("QX1", 0);
            pd.put("QX2", 0);
            pd.put("QX3", 0);
            pd.put("QX4", 0);
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "add")) {
                _roleService.saveKeFu(pd);
            }
            pd.put("U_ID", UUID);
            pd.put("C1", 0);
            pd.put("C2", 0);
            pd.put("C3", 0);
            pd.put("C4", 0);
            pd.put("Q1", 0);
            pd.put("Q2", 0);
            pd.put("Q3", 0);
            pd.put("Q4", 0);
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "add")) {
                _roleService.saveGYSQX(pd);
            }
            pd.put("QX_ID", UUID);
            pd.put("ROLE_ID", UUID);
            pd.put("ADD_QX", "0");
            pd.put("DEL_QX", "0");
            pd.put("EDIT_QX", "0");
            pd.put("CHA_QX", "0");
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "add")) {
                _roleService.add(pd);
            }
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/toEdit")
    public ModelAndView toEdit(String ROLE_ID)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            pd.put("ROLE_ID", ROLE_ID);
            pd = _roleService.findObjectById(pd);
            mv.setViewName("system/role/role_edit");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/edit")
    public ModelAndView edit()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
                pd = _roleService.edit(pd);
            }
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/auth")
    public String auth(@RequestParam String ROLE_ID, Model model)
        throws Exception
    {
        try {
            List<Menu> menuList = _menuService.listAllMenu();
            Role role = _roleService.getRoleById(ROLE_ID);
            String roleRights = role.getRIGHTS();
            if (Tools.notEmpty(roleRights)) {
                for (Menu menu : menuList) {
                    menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
                    if (menu.isHasMenu()) {
                        List<Menu> subMenuList = menu.getSubMenu();
                        for (Menu sub : subMenuList) {
                            sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                        }
                    }
                }
            }
            JSONArray arr = JSONArray.fromObject(menuList);
            String json = arr.toString();
            json = json.replaceAll("MENU_ID", "id").replaceAll("MENU_NAME", "name").replaceAll("subMenu", "nodes").replaceAll("hasMenu", "checked");
            model.addAttribute("zTreeNodes", json);
            model.addAttribute("roleId", ROLE_ID);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

        return "authorization";
    }

    @RequestMapping(value = "/button")
    public ModelAndView button(@RequestParam String ROLE_ID, @RequestParam String msg, Model model)
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        try {
            List<Menu> menuList = _menuService.listAllMenu();
            Role role = _roleService.getRoleById(ROLE_ID);
            String roleRights = "";
            if ("add_qx".equals(msg)) {
                roleRights = role.getADD_QX();
            } else if ("del_qx".equals(msg)) {
                roleRights = role.getDEL_QX();
            } else if ("edit_qx".equals(msg)) {
                roleRights = role.getEDIT_QX();
            } else if ("cha_qx".equals(msg)) {
                roleRights = role.getCHA_QX();
            }

            if (Tools.notEmpty(roleRights)) {
                for (Menu menu : menuList) {
                    menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
                    if (menu.isHasMenu()) {
                        List<Menu> subMenuList = menu.getSubMenu();
                        for (Menu sub : subMenuList) {
                            sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                        }
                    }
                }
            }
            JSONArray arr = JSONArray.fromObject(menuList);
            String json = arr.toString();
            json = json.replaceAll("MENU_ID", "id").replaceAll("MENU_NAME", "name").replaceAll("subMenu", "nodes").replaceAll("hasMenu", "checked");
            mv.addObject("zTreeNodes", json);
            mv.addObject("roleId", ROLE_ID);
            mv.addObject("msg", msg);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        mv.setViewName("system/role/role_button");
        return mv;
    }

    @RequestMapping(value = "/auth/save")
    public void saveAuth(@RequestParam String ROLE_ID, @RequestParam String menuIds, PrintWriter out)
        throws Exception
    {
        PageData pd = new PageData();
        try {
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
                if (null != menuIds && !"".equals(menuIds.trim())) {
                    BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray(menuIds));
                    Role role = _roleService.getRoleById(ROLE_ID);
                    role.setRIGHTS(rights.toString());
                    _roleService.updateRoleRights(role);
                    pd.put("rights", rights.toString());
                } else {
                    Role role = new Role();
                    role.setRIGHTS("");
                    role.setROLE_ID(ROLE_ID);
                    _roleService.updateRoleRights(role);
                    pd.put("rights", "");
                }

                pd.put("roleId", ROLE_ID);
                _roleService.setAllRights(pd);
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @RequestMapping(value = "/roleButton/save")
    public void orleButton(@RequestParam String ROLE_ID, @RequestParam String menuIds, @RequestParam String msg, PrintWriter out)
        throws Exception
    {
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "edit")) {
                if (null != menuIds && !"".equals(menuIds.trim())) {
                    BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray(menuIds));
                    pd.put("value", rights.toString());
                } else {
                    pd.put("value", "");
                }
                pd.put("ROLE_ID", ROLE_ID);
                _roleService.updateQx(msg, pd);
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object deleteRole(@RequestParam String ROLE_ID)
        throws Exception
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = new PageData();
        String errInfo = "";
        try {
            if (Jurisdiction.buttonJurisdiction(_menuUrl, "del")) {
                pd.put("ROLE_ID", ROLE_ID);
                List<Role> roleList_z = _roleService.listAllRolesByPId(pd);
                if (roleList_z.size() > 0) {
                    errInfo = "false";
                } else {
                    List<PageData> userlist = _roleService.listAllUByRid(pd);
                    List<PageData> appuserlist = _roleService.listAllAppUByRid(pd);
                    if (userlist.size() > 0 || appuserlist.size() > 0) {
                        errInfo = "false2";
                    } else {
                        _roleService.deleteRoleById(ROLE_ID);
                        _roleService.deleteKeFuById(ROLE_ID);
                        _roleService.deleteGById(ROLE_ID);
                        errInfo = "success";
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getHC()
    {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        return (Map<String, String>)session.getAttribute(Const.SESSION_QX);
    }
}
