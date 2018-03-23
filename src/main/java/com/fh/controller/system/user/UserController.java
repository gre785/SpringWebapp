
package com.fh.controller.system.user;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.entity.system.Role;
import com.fh.service.system.menu.MenuService;
import com.fh.service.system.role.RoleService;
import com.fh.service.system.user.UserService;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.FileDownload;
import com.fh.util.FileUpload;
import com.fh.util.GetPinyin;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelRead;
import com.fh.util.PageData;
import com.fh.util.ObjectExcelView;
import com.fh.util.PathUtil;
import com.fh.util.Tools;

@Controller
@RequestMapping(value = "/user")
public class UserController
    extends BaseController
{

    String menuUrl = "user/listUsers.do";
    @Resource(name = "userService")
    private UserService _userService;
    @Resource(name = "roleService")
    private RoleService _roleService;
    @Resource(name = "menuService")
    private MenuService _menuService;

    @RequestMapping(value = "/saveU")
    public ModelAndView saveU(PrintWriter out)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();

        pd.put("USER_ID", get32UUID());
        pd.put("RIGHTS", "");
        pd.put("LAST_LOGIN", "");
        pd.put("IP", "");
        pd.put("STATUS", "0");
        pd.put("SKIN", "default");
        pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());

        if (null == _userService.findByUId(pd)) {
            if (Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
                _userService.saveU(pd);
            }
            mv.addObject("msg", "success");
        } else {
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/hasU")
    @ResponseBody
    public Object hasU()
    {
        Map<String, String> map = new HashMap<String, String>();
        String errInfo = "success";
        PageData pd = getPageData();
        try {
            if (_userService.findByUId(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/hasE")
    @ResponseBody
    public Object hasE()
    {
        Map<String, String> map = new HashMap<String, String>();
        String errInfo = "success";
        PageData pd = getPageData();
        try {
            if (_userService.findByUE(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/hasN")
    @ResponseBody
    public Object hasN()
    {
        Map<String, String> map = new HashMap<String, String>();
        String errInfo = "success";
        PageData pd = getPageData();
        try {
            if (_userService.findByUN(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/editU")
    public ModelAndView editU()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        if (pd.getString("PASSWORD") != null && !"".equals(pd.getString("PASSWORD"))) {
            pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());
        }
        if (Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
            _userService.editU(pd);
        }
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/goEditU")
    public ModelAndView goEditU()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        String fx = pd.getString("fx");
        if ("head".equals(fx)) {
            mv.addObject("fx", "head");
        } else {
            mv.addObject("fx", "user");
        }

        List<Role> roleList = _roleService.listAllERRoles();
        pd = _userService.findByUiId(pd);
        mv.setViewName("system/user/user_edit");
        mv.addObject("msg", "editU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        return mv;
    }

    @RequestMapping(value = "/goAddU")
    public ModelAndView goAddU()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        List<Role> roleList;
        roleList = _roleService.listAllERRoles();

        mv.setViewName("system/user/user_edit");
        mv.addObject("msg", "saveU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);

        return mv;
    }

    @RequestMapping(value = "/listUsers")
    public ModelAndView listUsers(Page page)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        String USERNAME = pd.getString("USERNAME");
        if (null != USERNAME && !"".equals(USERNAME)) {
            USERNAME = USERNAME.trim();
            pd.put("USERNAME", USERNAME);
        }
        String lastLoginStart = pd.getString("lastLoginStart");
        String lastLoginEnd = pd.getString("lastLoginEnd");
        if (lastLoginStart != null && !"".equals(lastLoginStart)) {
            lastLoginStart = lastLoginStart + " 00:00:00";
            pd.put("lastLoginStart", lastLoginStart);
        }
        if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
            lastLoginEnd = lastLoginEnd + " 00:00:00";
            pd.put("lastLoginEnd", lastLoginEnd);
        }
        page.setPd(pd);
        List<PageData> userList = _userService.listPdPageUser(page);
        List<Role> roleList = _roleService.listAllERRoles();
        mv.setViewName("system/user/user_list");
        mv.addObject("userList", userList);
        mv.addObject("roleList", roleList);
        mv.addObject("pd", pd);
        mv.addObject(Const.SESSION_QX, getHC());
        return mv;
    }

    @RequestMapping(value = "/listtabUsers")
    public ModelAndView listtabUsers(Page page)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        List<PageData> userList = _userService.listAllUser(pd);
        mv.setViewName("system/user/user_tb_list");
        mv.addObject("userList", userList);
        mv.addObject("pd", pd);
        mv.addObject(Const.SESSION_QX, getHC());
        return mv;
    }

    @RequestMapping(value = "/deleteU")
    public void deleteU(PrintWriter out)
    {
        PageData pd = getPageData();
        try {
            if (Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
                _userService.deleteU(pd);
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

    }

    @RequestMapping(value = "/deleteAllU")
    @ResponseBody
    public Object deleteAllU()
    {
        PageData pd = new PageData();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            pd = getPageData();
            List<PageData> pdList = new ArrayList<PageData>();
            String USER_IDS = pd.getString("USER_IDS");
            if (null != USER_IDS && !"".equals(USER_IDS)) {
                String ArrayUSER_IDS[] = USER_IDS.split(",");
                if (Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
                    _userService.deleteAllU(ArrayUSER_IDS);
                }
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
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        try {
            if (Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
                String USERNAME = pd.getString("USERNAME");
                if (null != USERNAME && !"".equals(USERNAME)) {
                    USERNAME = USERNAME.trim();
                    pd.put("USERNAME", USERNAME);
                }
                String lastLoginStart = pd.getString("lastLoginStart");
                String lastLoginEnd = pd.getString("lastLoginEnd");
                if (lastLoginStart != null && !"".equals(lastLoginStart)) {
                    lastLoginStart = lastLoginStart + " 00:00:00";
                    pd.put("lastLoginStart", lastLoginStart);
                }
                if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
                    lastLoginEnd = lastLoginEnd + " 00:00:00";
                    pd.put("lastLoginEnd", lastLoginEnd);
                }
                Map<String, Object> dataMap = new HashMap<String, Object>();
                List<String> titles = new ArrayList<String>();

                titles.add("用户名"); 		// 1
                titles.add("编号");  		// 2
                titles.add("姓名");			// 3
                titles.add("职位");			// 4
                titles.add("手机");			// 5
                titles.add("邮箱");			// 6
                titles.add("最近登录");		// 7
                titles.add("上次登录IP");	// 8

                dataMap.put("titles", titles);

                List<PageData> userList = _userService.listAllUser(pd);
                List<PageData> varList = new ArrayList<PageData>();
                for (int i = 0; i < userList.size(); i++) {
                    PageData vpd = new PageData();
                    vpd.put("var1", userList.get(i).getString("USERNAME"));		// 1
                    vpd.put("var2", userList.get(i).getString("NUMBER"));		// 2
                    vpd.put("var3", userList.get(i).getString("NAME"));			// 3
                    vpd.put("var4", userList.get(i).getString("ROLE_NAME"));	// 4
                    vpd.put("var5", userList.get(i).getString("PHONE"));		// 5
                    vpd.put("var6", userList.get(i).getString("EMAIL"));		// 6
                    vpd.put("var7", userList.get(i).getString("LAST_LOGIN"));	// 7
                    vpd.put("var8", userList.get(i).getString("IP"));			// 8
                    varList.add(vpd);
                }
                dataMap.put("varList", varList);
                ObjectExcelView erv = new ObjectExcelView();
                mv = new ModelAndView(erv, dataMap);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/goUploadExcel")
    public ModelAndView goUploadExcel()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        mv.setViewName("system/user/uploadexcel");
        return mv;
    }

    @RequestMapping(value = "/downExcel")
    public void downExcel(HttpServletResponse response)
        throws Exception
    {

        FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Users.xls", "Users.xls");

    }

    @RequestMapping(value = "/readExcel")
    public ModelAndView readExcel(@RequestParam(value = "excel", required = false) MultipartFile file)
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = new PageData();
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
            return null;
        }
        if (null != file && !file.isEmpty()) {
            String filePath = PathUtil.getClasspath() + Const.FILEPATHFILE;
            String fileName = FileUpload.fileUp(file, filePath, "userexcel");

            List<PageData> listPd = (List)ObjectExcelRead.readExcel(filePath, fileName, 2, 0, 0);
            pd.put("RIGHTS", "");
            pd.put("LAST_LOGIN", "");
            pd.put("IP", "");
            pd.put("STATUS", "0");
            pd.put("SKIN", "default");

            List<Role> roleList = _roleService.listAllERRoles();
            pd.put("ROLE_ID", roleList.get(0).getROLE_ID());
            /**
             * var0 :id
             * var1 :name
             * var2 :cell
             * var3 :email
             * var4 :comment
             */
            for (int i = 0; i < listPd.size(); i++) {
                pd.put("USER_ID", get32UUID());
                pd.put("NAME", listPd.get(i).getString("var1"));
                String USERNAME = GetPinyin.getPingYin(listPd.get(i).getString("var1"));
                pd.put("USERNAME", USERNAME);
                if (_userService.findByUId(pd) != null) {
                    USERNAME = GetPinyin.getPingYin(listPd.get(i).getString("var1")) + Tools.getRandomNum();
                    pd.put("USERNAME", USERNAME);
                }
                pd.put("BZ", listPd.get(i).getString("var4"));
                if (Tools.checkEmail(listPd.get(i).getString("var3"))) {
                    pd.put("EMAIL", listPd.get(i).getString("var3"));
                    if (_userService.findByUE(pd) != null) {
                        continue;
                    }
                } else {
                    continue;
                }

                pd.put("NUMBER", listPd.get(i).getString("var0"));
                pd.put("PHONE", listPd.get(i).getString("var2"));
                pd.put("PASSWORD", new SimpleHash("SHA-1", USERNAME, "123").toString()); // default password 123
                if (_userService.findByUN(pd) != null) {
                    continue;
                }
                _userService.saveU(pd);
            }
            mv.addObject("msg", "success");
        }

        mv.setViewName("save_result");
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getHC()
    {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        return (Map<String, String>)session.getAttribute(Const.SESSION_QX);
    }
}
