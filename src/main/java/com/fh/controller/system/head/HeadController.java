
package com.fh.controller.system.head;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.service.system.appuser.AppuserService;
import com.fh.service.system.user.UserService;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.SmsUtil;
import com.fh.util.Tools;
import com.fh.util.Watermark;
import com.fh.util.mail.SimpleMailSender;

/** 
 */
@Controller
@RequestMapping(value = "/head")
public class HeadController
    extends BaseController
{

    @Resource(name = "userService")
    private UserService _userService;
    @Resource(name = "appuserService")
    private AppuserService _appuserService;

    @RequestMapping(value = "/getUname")
    @ResponseBody
    public Object getList()
    {
        PageData pd = getPageData();;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<PageData> pdList = new ArrayList<PageData>();
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession();
            PageData pds = (PageData)session.getAttribute(Const.SESSION_userpds);
            if (null == pds) {
                String USERNAME = session.getAttribute(Const.SESSION_USERNAME).toString();
                pd.put("USERNAME", USERNAME);
                pds = _userService.findByUId(pd);
                session.setAttribute(Const.SESSION_userpds, pds);
            }
            pdList.add(pds);
            map.put("list", pdList);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        return AppUtil.returnObject(pd, map);
    }

    @RequestMapping(value = "/setSKIN")
    public void setSkin(PrintWriter out)
    {
        PageData pd = getPageData();
        try {
            Subject currentUser = SecurityUtils.getSubject();
            Session session = currentUser.getSession();
            String USERNAME = session.getAttribute(Const.SESSION_USERNAME).toString();
            pd.put("USERNAME", USERNAME);
            _userService.setSkin(pd);
            session.removeAttribute(Const.SESSION_userpds);
            session.removeAttribute(Const.SESSION_USERROL);
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

    }

    @RequestMapping(value = "/editEmail")
    public ModelAndView editEmail()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        mv.setViewName("system/head/edit_email");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/goSendSms")
    public ModelAndView goSendSms()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        mv.setViewName("system/head/send_sms");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/sendSms")
    @ResponseBody
    public Object sendSms()
    {
        PageData pd = getPageData();
        Map<String, Object> map = new HashMap<String, Object>();
        String msg = "ok";
        int count = 0;
        int zcount = 0;

        List<PageData> pdList = new ArrayList<PageData>();
        String PHONEs = pd.getString("PHONE");
        String CONTENT = pd.getString("CONTENT");
        String isAll = pd.getString("isAll");
        String TYPE = pd.getString("TYPE");
        String fmsg = pd.getString("fmsg");

        if ("yes".endsWith(isAll)) {
            try {
                List<PageData> userList = new ArrayList<PageData>();
                userList = "appuser".equals(fmsg) ? _appuserService.listAllUser(pd) : _userService.listAllUser(pd);
                zcount = userList.size();
                for (int i = 0; i < userList.size(); i++) {
                    if (Tools.checkMobileNumber(userList.get(i).getString("PHONE"))) {
                        if ("1".equals(TYPE)) {
                            SmsUtil.sendSms1(userList.get(i).getString("PHONE"), CONTENT);
                        } else {
                            SmsUtil.sendSms2(userList.get(i).getString("PHONE"), CONTENT);
                        }
                        count++;
                    } else {
                        continue;
                    }
                }
                msg = "ok";
            } catch (Exception e) {
                msg = "error";
            }
        } else {
            PHONEs = PHONEs.replaceAll("；", ";");
            PHONEs = PHONEs.replaceAll(" ", "");
            String[] arrTITLE = PHONEs.split(";");
            zcount = arrTITLE.length;
            try {
                for (int i = 0; i < arrTITLE.length; i++) {
                    if (Tools.checkMobileNumber(arrTITLE[i])) {
                        if ("1".equals(TYPE)) {
                            SmsUtil.sendSms1(arrTITLE[i], CONTENT);
                        } else {
                            SmsUtil.sendSms2(arrTITLE[i], CONTENT);
                        }
                        count++;
                    } else {
                        continue;
                    }
                }
                msg = "ok";
            } catch (Exception e) {
                msg = "error";
            }
        }
        pd.put("msg", msg);
        pd.put("count", count);
        pd.put("ecount", zcount - count);
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    @RequestMapping(value = "/goSendEmail")
    public ModelAndView goSendEmail()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        mv.setViewName("system/head/send_email");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/sendEmail")
    @ResponseBody
    public Object sendEmail()
    {
        PageData pd = new PageData();
        pd = this.getPageData();
        Map<String, Object> map = new HashMap<String, Object>();
        String msg = "ok";
        int count = 0;
        int zcount = 0;
        String strEMAIL = Tools.readTxtFile(Const.EMAIL);
        List<PageData> pdList = new ArrayList<PageData>();

        String toEMAIL = pd.getString("EMAIL");
        String TITLE = pd.getString("TITLE");
        String CONTENT = pd.getString("CONTENT");
        String TYPE = pd.getString("TYPE");
        String isAll = pd.getString("isAll");
        String fmsg = pd.getString("fmsg");

        if (null != strEMAIL && !"".equals(strEMAIL)) {
            String strEM[] = strEMAIL.split(",fh,");
            if (strEM.length == 4) {
                if ("yes".endsWith(isAll)) {
                    try {
                        List<PageData> userList = new ArrayList<PageData>();
                        userList = "appuser".equals(fmsg) ? _appuserService.listAllUser(pd) : _userService.listAllUser(pd);
                        zcount = userList.size();
                        try {
                            for (int i = 0; i < userList.size(); i++) {
                                if (Tools.checkEmail(userList.get(i).getString("EMAIL"))) {
                                    SimpleMailSender.sendEmail(strEM[0], strEM[1], strEM[2], strEM[3], userList.get(i).getString("EMAIL"), TITLE,
                                        CONTENT, TYPE);
                                    count++;
                                } else {
                                    continue;
                                }
                            }
                            msg = "ok";
                        } catch (Exception e) {
                            msg = "error";
                        }

                    } catch (Exception e) {
                        msg = "error";
                    }
                } else {
                    toEMAIL = toEMAIL.replaceAll("；", ";");
                    toEMAIL = toEMAIL.replaceAll(" ", "");
                    String[] arrTITLE = toEMAIL.split(";");
                    zcount = arrTITLE.length;
                    try {
                        for (int i = 0; i < arrTITLE.length; i++) {
                            if (Tools.checkEmail(arrTITLE[i])) {
                                SimpleMailSender.sendEmail(strEM[0], strEM[1], strEM[2], strEM[3], arrTITLE[i], TITLE, CONTENT, TYPE);// 调用发送邮件函数
                                count++;
                            } else {
                                continue;
                            }
                        }
                        msg = "ok";
                    } catch (Exception e) {
                        msg = "error";
                    }
                }
            } else {
                msg = "error";
            }
        } else {
            msg = "error";
        }
        pd.put("msg", msg);
        pd.put("count", count);
        pd.put("ecount", zcount - count);
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    @RequestMapping(value = "/goSystem")
    public ModelAndView goEditEmail()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        pd.put("YSYNAME", Tools.readTxtFile(Const.SYSNAME));
        pd.put("COUNTPAGE", Tools.readTxtFile(Const.PAGE));
        String strEMAIL = Tools.readTxtFile(Const.EMAIL);
        String strSMS1 = Tools.readTxtFile(Const.SMS1);
        String strSMS2 = Tools.readTxtFile(Const.SMS2);
        String strFWATERM = Tools.readTxtFile(Const.FWATERM);
        String strIWATERM = Tools.readTxtFile(Const.IWATERM);
        pd.put("Token", Tools.readTxtFile(Const.WEIXIN));
        if (null != strEMAIL && !"".equals(strEMAIL)) {
            String strEM[] = strEMAIL.split(",fh,");
            if (strEM.length == 4) {
                pd.put("SMTP", strEM[0]);
                pd.put("PORT", strEM[1]);
                pd.put("EMAIL", strEM[2]);
                pd.put("PAW", strEM[3]);
            }
        }
        if (null != strSMS1 && !"".equals(strSMS1)) {
            String strS1[] = strSMS1.split(",fh,");
            if (strS1.length == 2) {
                pd.put("SMSU1", strS1[0]);
                pd.put("SMSPAW1", strS1[1]);
            }
        }
        if (null != strSMS2 && !"".equals(strSMS2)) {
            String strS2[] = strSMS2.split(",fh,");
            if (strS2.length == 2) {
                pd.put("SMSU2", strS2[0]);
                pd.put("SMSPAW2", strS2[1]);
            }
        }
        if (null != strFWATERM && !"".equals(strFWATERM)) {
            String strFW[] = strFWATERM.split(",fh,");
            if (strFW.length == 5) {
                pd.put("isCheck1", strFW[0]);
                pd.put("fcontent", strFW[1]);
                pd.put("fontSize", strFW[2]);
                pd.put("fontX", strFW[3]);
                pd.put("fontY", strFW[4]);
            }
        }
        if (null != strIWATERM && !"".equals(strIWATERM)) {
            String strIW[] = strIWATERM.split(",fh,");
            if (strIW.length == 4) {
                pd.put("isCheck2", strIW[0]);
                pd.put("imgUrl", strIW[1]);
                pd.put("imgX", strIW[2]);
                pd.put("imgY", strIW[3]);
            }
        }
        mv.setViewName("system/head/sys_edit");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/saveSys")
    public ModelAndView saveSys()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        Tools.writeFile(Const.SYSNAME, pd.getString("YSYNAME"));
        Tools.writeFile(Const.PAGE, pd.getString("COUNTPAGE"));	
        Tools.writeFile(Const.EMAIL,
            pd.getString("SMTP") + ",fh," + pd.getString("PORT") + ",fh," + pd.getString("EMAIL") + ",fh," + pd.getString("PAW"));
        Tools.writeFile(Const.SMS1, pd.getString("SMSU1") + ",fh," + pd.getString("SMSPAW1"));
        Tools.writeFile(Const.SMS2, pd.getString("SMSU2") + ",fh," + pd.getString("SMSPAW2"));
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/saveSys2")
    public ModelAndView saveSys2()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        Tools.writeFile(Const.FWATERM, pd.getString("isCheck1") + ",fh," + pd.getString("fcontent") + ",fh," + pd.getString("fontSize") + ",fh,"
                + pd.getString("fontX") + ",fh," + pd.getString("fontY"));
        Tools.writeFile(Const.IWATERM,
            pd.getString("isCheck2") + ",fh," + pd.getString("imgUrl") + ",fh," + pd.getString("imgX") + ",fh," + pd.getString("imgY"));
        Watermark.fushValue();
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/saveSys3")
    public ModelAndView saveSys3()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        PageData pd = getPageData();
        Tools.writeFile(Const.WEIXIN, pd.getString("Token"));
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/goProductCode")
    public ModelAndView goProductCode()
        throws Exception
    {
        ModelAndView mv = getModelAndView();
        mv.setViewName("system/head/productCode");
        return mv;
    }
}
