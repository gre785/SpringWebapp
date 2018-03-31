
package com.fh.controller.app.appuser;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fh.controller.base.BaseController;
import com.fh.service.system.appuser.AppuserService;
import com.fh.util.AppUtil;
import com.fh.util.PageData;
import com.fh.util.Tools;

/**
  * 00	request fail
  * 01	request success
  * 02	return null
  * 03	not complete
  * 04  wrong username / password
  * 05  FKEY fail
 */
@Controller
@RequestMapping(value = "/appuser")
public class IntAppuserController
    extends BaseController
{
    @Resource(name = "appuserService")
    private AppuserService _appuserService;

    @RequestMapping(value = "/getAppuserByUm")
    @ResponseBody
    public Object getAppuserByUsernmae()
    {
        logBefore(logger, "retrieve user infor through UID");
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = getPageData();
        String result = "00";
        try {
            if (Tools.checkKey("USERNAME", pd.getString("FKEY"))) {
                if (AppUtil.checkParam("getAppuserByUsernmae", pd)) {
                    pd = _appuserService.findByUId(pd);
                    map.put("pd", pd);
                    result = (null == pd) ? "02" : "01";
                } else {
                    result = "03";
                }
            } else {
                result = "05";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            map.put("result", result);
            logAfter(logger);
        }
        return AppUtil.returnObject(new PageData(), map);
    }

}
