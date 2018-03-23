
package com.fh.controller.system.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.system.Menu;
import com.fh.entity.system.Role;
import com.fh.entity.system.User;
import com.fh.service.system.menu.MenuService;
import com.fh.service.system.role.RoleService;
import com.fh.service.system.user.UserService;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.PageData;
import com.fh.util.RightsHelper;
import com.fh.util.Tools;

/*
 * main entry
 */
@Controller
public class LoginController
    extends BaseController
{
    @Resource(name = "userService")
    private UserService _userService;
    @Resource(name = "menuService")
    private MenuService _menuService;
    @Resource(name = "roleService")
    private RoleService _roleService;

    public void getRemortIP(String USERNAME)
        throws Exception
    {
        PageData pd = getPageData();
        HttpServletRequest request = getRequest();
        String ip = "";
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }
        pd.put("USERNAME", USERNAME);
        pd.put("IP", ip);
        _userService.saveIP(pd);
    }

    @RequestMapping(value = "/login_toLogin")
    public ModelAndView toLogin()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME));
        mv.setViewName("system/admin/login");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/login_login", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object login()
        throws Exception
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = getPageData();
        String errInfo = "";
        String KEYDATA[] = pd.getString("KEYDATA").replaceAll("qq313596790fh", "").replaceAll("QQ978336446fh", "").split(",fh,");

        if (null != KEYDATA && KEYDATA.length == 3) {
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession();
            String sessionCode = (String)session.getAttribute(Const.SESSION_SECURITY_CODE);

            String code = KEYDATA[2];
            if (null == code || "".equals(code)) {
                errInfo = "nullcode";
            } else {
                String USERNAME = KEYDATA[0];
                String PASSWORD = KEYDATA[1];
                pd.put("USERNAME", USERNAME);
                if (Tools.notEmpty(sessionCode) && sessionCode.equalsIgnoreCase(code)) {
                    String passwd = new SimpleHash("SHA-1", USERNAME, PASSWORD).toString();
                    pd.put("PASSWORD", passwd);
                    pd = _userService.getUserByNameAndPwd(pd);
                    if (pd != null) {
                        pd.put("LAST_LOGIN", DateUtil.getTime().toString());
                        _userService.updateLastLogin(pd);
                        User user = new User();
                        user.setUSER_ID(pd.getString("USER_ID"));
                        user.setUSERNAME(pd.getString("USERNAME"));
                        user.setPASSWORD(pd.getString("PASSWORD"));
                        user.setNAME(pd.getString("NAME"));
                        user.setRIGHTS(pd.getString("RIGHTS"));
                        user.setROLE_ID(pd.getString("ROLE_ID"));
                        user.setLAST_LOGIN(pd.getString("LAST_LOGIN"));
                        user.setIP(pd.getString("IP"));
                        user.setSTATUS(pd.getString("STATUS"));
                        session.setAttribute(Const.SESSION_USER, user);
                        session.removeAttribute(Const.SESSION_SECURITY_CODE);

                        Subject subject = SecurityUtils.getSubject();
                        UsernamePasswordToken token = new UsernamePasswordToken(USERNAME, PASSWORD);
                        try {
                            subject.login(token);
                        } catch (AuthenticationException e) {
                            errInfo = "login fails！";
                        }

                    } else {
                        errInfo = "usererror";
                    }
                } else {
                    errInfo = "codeerror";
                }
                if (Tools.isEmpty(errInfo)) {
                    errInfo = "success";
                }
            }
        } else {
            errInfo = "error";
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/main/{changeMenu}")
    public ModelAndView login_index(@PathVariable("changeMenu") String changeMenu)
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession();
            User user = (User)session.getAttribute(Const.SESSION_USER);
            if (user != null) {
                User userr = (User)session.getAttribute(Const.SESSION_USERROL);
                if (null == userr) {
                    user = _userService.getUserAndRoleById(user.getUSER_ID());
                    session.setAttribute(Const.SESSION_USERROL, user);
                } else {
                    user = userr;
                }
                Role role = user.getRole();
                String roleRights = role != null ? role.getRIGHTS() : "";
                session.setAttribute(Const.SESSION_ROLE_RIGHTS, roleRights);
                session.setAttribute(Const.SESSION_USERNAME, user.getUSERNAME());
                List<Menu> allmenuList = new ArrayList<Menu>();
                if (null == session.getAttribute(Const.SESSION_allmenuList)) {
                    allmenuList = _menuService.listAllMenu();
                    if (Tools.notEmpty(roleRights)) {
                        for (Menu menu : allmenuList) {
                            menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
                            if (menu.isHasMenu()) {
                                List<Menu> subMenuList = menu.getSubMenu();
                                for (Menu sub : subMenuList) {
                                    sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                                }
                            }
                        }
                    }
                    session.setAttribute(Const.SESSION_allmenuList, allmenuList);
                } else {
                    allmenuList = (List<Menu>)session.getAttribute(Const.SESSION_allmenuList);
                }
                List<Menu> menuList = new ArrayList<Menu>();
                if (null == session.getAttribute(Const.SESSION_menuList) || ("yes".equals(changeMenu))) {
                    List<Menu> menuList1 = new ArrayList<Menu>();
                    List<Menu> menuList2 = new ArrayList<Menu>();
                    for (Menu menu : allmenuList) {
                        if ("1".equals(menu.getMENU_TYPE())) {
                            menuList1.add(menu);
                        } else {
                            menuList2.add(menu);
                        }
                    }

                    session.removeAttribute(Const.SESSION_menuList);
                    if ("2".equals(session.getAttribute("changeMenu"))) {
                        session.setAttribute(Const.SESSION_menuList, menuList1);
                        session.removeAttribute("changeMenu");
                        session.setAttribute("changeMenu", "1");
                        menuList = menuList1;
                    } else {
                        session.setAttribute(Const.SESSION_menuList, menuList2);
                        session.removeAttribute("changeMenu");
                        session.setAttribute("changeMenu", "2");
                        menuList = menuList2;
                    }
                } else {
                    menuList = (List<Menu>)session.getAttribute(Const.SESSION_menuList);
                }

                if (null == session.getAttribute(Const.SESSION_QX)) {
                    session.setAttribute(Const.SESSION_QX, this.getUQX(session));
                }

                String strXML = "<graph caption='前12个月订单销量柱状图' xAxisName='月份' yAxisName='值' decimalPrecision='0' formatNumberScale='0'><set name='2013-05' value='4' color='AFD8F8'/><set name='2013-04' value='0' color='AFD8F8'/><set name='2013-03' value='0' color='AFD8F8'/><set name='2013-02' value='0' color='AFD8F8'/><set name='2013-01' value='0' color='AFD8F8'/><set name='2012-01' value='0' color='AFD8F8'/><set name='2012-11' value='0' color='AFD8F8'/><set name='2012-10' value='0' color='AFD8F8'/><set name='2012-09' value='0' color='AFD8F8'/><set name='2012-08' value='0' color='AFD8F8'/><set name='2012-07' value='0' color='AFD8F8'/><set name='2012-06' value='0' color='AFD8F8'/></graph>";
                mv.addObject("strXML", strXML);

                mv.setViewName("system/admin/index");
                mv.addObject("user", user);
                mv.addObject("menuList", menuList);
            } else {
                mv.setViewName("system/admin/login");
            }

        } catch (Exception e) {
            mv.setViewName("system/admin/login");
            logger.error(e.getMessage(), e);
        }
        pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME));
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/tab")
    public String tab()
    {
        return "system/admin/tab";
    }

    @RequestMapping(value = "/login_default")
    public String defaultPage()
    {
        return "system/admin/default";
    }

    @RequestMapping(value = "/logout")
    public ModelAndView logout()
    {
        ModelAndView mv = getModelAndView();
        PageData pd = new PageData();

        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();

        session.removeAttribute(Const.SESSION_USER);
        session.removeAttribute(Const.SESSION_ROLE_RIGHTS);
        session.removeAttribute(Const.SESSION_allmenuList);
        session.removeAttribute(Const.SESSION_menuList);
        session.removeAttribute(Const.SESSION_QX);
        session.removeAttribute(Const.SESSION_userpds);
        session.removeAttribute(Const.SESSION_USERNAME);
        session.removeAttribute(Const.SESSION_USERROL);
        session.removeAttribute("changeMenu");

        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        pd = this.getPageData();
        String msg = pd.getString("msg");
        pd.put("msg", msg);
        pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME));
        mv.setViewName("system/admin/login");
        mv.addObject("pd", pd);
        return mv;
    }

    public Map<String, String> getUQX(Session session)
    {
        PageData pd = new PageData();
        Map<String, String> map = new HashMap<String, String>();
        try {
            String USERNAME = session.getAttribute(Const.SESSION_USERNAME).toString();
            pd.put(Const.SESSION_USERNAME, USERNAME);
            String ROLE_ID = _userService.findByUId(pd).get("ROLE_ID").toString();
            pd.put("ROLE_ID", ROLE_ID);
            PageData pd2 = new PageData();
            pd2.put(Const.SESSION_USERNAME, USERNAME);
            pd2.put("ROLE_ID", ROLE_ID);
            pd = _roleService.findObjectById(pd);
            pd2 = _roleService.findGLbyrid(pd2);
            if (null != pd2) {
                map.put("FX_QX", pd2.get("FX_QX").toString());
                map.put("FW_QX", pd2.get("FW_QX").toString());
                map.put("QX1", pd2.get("QX1").toString());
                map.put("QX2", pd2.get("QX2").toString());
                map.put("QX3", pd2.get("QX3").toString());
                map.put("QX4", pd2.get("QX4").toString());

                pd2.put("ROLE_ID", ROLE_ID);
                pd2 = _roleService.findYHbyrid(pd2);
                map.put("C1", pd2.get("C1").toString());
                map.put("C2", pd2.get("C2").toString());
                map.put("C3", pd2.get("C3").toString());
                map.put("C4", pd2.get("C4").toString());
                map.put("Q1", pd2.get("Q1").toString());
                map.put("Q2", pd2.get("Q2").toString());
                map.put("Q3", pd2.get("Q3").toString());
                map.put("Q4", pd2.get("Q4").toString());
            }

            map.put("adds", pd.getString("ADD_QX"));
            map.put("dels", pd.getString("DEL_QX"));
            map.put("edits", pd.getString("EDIT_QX"));
            map.put("chas", pd.getString("CHA_QX"));
            this.getRemortIP(USERNAME);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return map;
    }

}
