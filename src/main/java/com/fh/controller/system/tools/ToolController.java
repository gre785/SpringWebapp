
package com.fh.controller.system.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.MapDistance;
import com.fh.util.PageData;
import com.fh.util.PathUtil;
import com.fh.util.TwoDimensionCode;

@Controller
@RequestMapping(value = "/tool")
public class ToolController
    extends BaseController
{
    @RequestMapping(value = "/interfaceTest")
    public ModelAndView editEmail()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        mv.setViewName("system/tools/interfaceTest");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/severTest")
    @ResponseBody
    public Object severTest()
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String errInfo = "success", str = "", rTime = "";
        try {
            long startTime = System.currentTimeMillis();
            URL url = new URL(pd.getString("serverUrl"));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(pd.getString("requestMethod"));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            long endTime = System.currentTimeMillis();
            String temp = "";
            while ((temp = in.readLine()) != null) {
                str = str + temp;
            }
            rTime = String.valueOf(endTime - startTime);
        } catch (Exception e) {
            errInfo = "error";
        }
        map.put("errInfo", errInfo);
        map.put("result", str);
        map.put("rTime", rTime);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/goSendEmail")
    public ModelAndView goSendEmail()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        mv.setViewName("system/tools/email");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/goTwoDimensionCode")
    public ModelAndView goTwoDimensionCode()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        mv.setViewName("system/tools/twoDimensionCode");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/createTwoDimensionCode")
    @ResponseBody
    public Object createTwoDimensionCode()
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String errInfo = "success", encoderImgId = this.get32UUID() + ".png";
        String encoderContent = pd.getString("encoderContent");
        if (null == encoderContent) {
            errInfo = "error";
        } else {
            try {
                String filePath = PathUtil.getClasspath() + Const.FILEPATHTWODIMENSIONCODE + encoderImgId;
                TwoDimensionCode.encoderQRCode(encoderContent, filePath, "png");
            } catch (Exception e) {
                errInfo = "error";
            }
        }
        map.put("result", errInfo);
        map.put("encoderImgId", encoderImgId);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/readTwoDimensionCode")
    @ResponseBody
    public Object readTwoDimensionCode()
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String errInfo = "success", readContent = "";
        String imgId = pd.getString("imgId");
        if (null == imgId) {
            errInfo = "error";
        } else {
            try {
                String filePath = PathUtil.getClasspath() + Const.FILEPATHTWODIMENSIONCODE + imgId;
                readContent = TwoDimensionCode.decoderQRCode(filePath);
            } catch (Exception e) {
                errInfo = "error";
            }
        }
        map.put("result", errInfo);
        map.put("readContent", readContent);
        return AppUtil.returnObject(new PageData(), map);
    }

    @RequestMapping(value = "/ztree")
    public ModelAndView ztree()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        mv.setViewName("system/tools/ztree");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/map")
    public ModelAndView map()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        mv.setViewName("system/tools/map");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/mapXY")
    public ModelAndView mapXY()
        throws Exception
    {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        mv.setViewName("system/tools/mapXY");
        mv.addObject("pd", pd);
        return mv;
    }

    @RequestMapping(value = "/getDistance")
    @ResponseBody
    public Object getDistance()
    {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String errInfo = "success", distance = "";
        try {
            distance = MapDistance.getDistance(pd.getString("ZUOBIAO_Y"), pd.getString("ZUOBIAO_X"), pd.getString("ZUOBIAO_Y2"),
                pd.getString("ZUOBIAO_X2"));
        } catch (Exception e) {
            errInfo = "error";
        }
        map.put("result", errInfo);
        map.put("distance", distance);
        return AppUtil.returnObject(new PageData(), map);
    }
}
