
package com.fh.controller.information.pictures;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
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
import com.fh.util.AppUtil;
import com.fh.util.DateUtil;
import com.fh.util.DelAllFile;
import com.fh.util.FileUpload;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.PathUtil;
import com.fh.util.Tools;
import com.fh.util.Watermark;
import com.fh.service.information.pictures.PicturesService;

/** 
 */
@Controller
@RequestMapping(value = "/pictures")
public class PicturesController
    extends BaseController
{

    String menuUrl = "pictures/list.do";
    @Resource(name = "picturesService")
    private PicturesService _picturesService;

    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save(@RequestParam(required = false) MultipartFile file)
        throws Exception
    {
        logBefore(logger, "新增Pictures");
        Map<String, String> map = new HashMap<String, String>();
        String ffile = DateUtil.getDays(), fileName = "";
        PageData pd = new PageData();
        if (Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
            if (null != file && !file.isEmpty()) {
                String filePath = PathUtil.getClasspath() + Const.FILEPATHIMG + ffile;
                fileName = FileUpload.fileUp(file, filePath, this.get32UUID());
            } else {
                System.out.println("upload fails");
            }

            pd.put("PICTURES_ID", this.get32UUID());
            pd.put("TITLE", "图片");
            pd.put("NAME", fileName);
            pd.put("PATH", ffile + "/" + fileName);
            pd.put("CREATETIME", Tools.date2Str(new Date()));
            pd.put("MASTER_ID", "1");
            pd.put("BZ", "图片管理处上传");
            // 加水印
            Watermark.setWatemark(PathUtil.getClasspath() + Const.FILEPATHIMG + ffile + "/" + fileName);
            _picturesService.save(pd);
        }
        map.put("result", "ok");
        return AppUtil.returnObject(pd, map);
    }

    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out)
    {
        logBefore(logger, "delete Pictures");
        PageData pd = new PageData();
        try {
            if (Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
                pd = this.getPageData();
                DelAllFile.delFolder(PathUtil.getClasspath() + Const.FILEPATHIMG + pd.getString("PATH")); 
                _picturesService.delete(pd);
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

    }

    @RequestMapping(value = "/edit")
    public ModelAndView edit(HttpServletRequest request, @RequestParam(value = "tp", required = false) MultipartFile file,
        @RequestParam(value = "tpz", required = false) String tpz, @RequestParam(value = "PICTURES_ID", required = false) String PICTURES_ID,
        @RequestParam(value = "TITLE", required = false) String TITLE, @RequestParam(value = "MASTER_ID", required = false) String MASTER_ID,
        @RequestParam(value = "BZ", required = false) String BZ)
        throws Exception
    {
        logBefore(logger, "modify Pictures");
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        if (Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
            pd.put("PICTURES_ID", PICTURES_ID);
            pd.put("TITLE", TITLE);
            pd.put("MASTER_ID", MASTER_ID);
            pd.put("BZ", BZ);

            if (null == tpz) {
                tpz = "";
            }
            String ffile = DateUtil.getDays(), fileName = "";
            if (null != file && !file.isEmpty()) {
                String filePath = PathUtil.getClasspath() + Const.FILEPATHIMG + ffile;
                fileName = FileUpload.fileUp(file, filePath, this.get32UUID());
                pd.put("PATH", ffile + "/" + fileName);
                pd.put("NAME", fileName);
            } else {
                pd.put("PATH", tpz);
            }
            Watermark.setWatemark(PathUtil.getClasspath() + Const.FILEPATHIMG + ffile + "/" + fileName);
            _picturesService.edit(pd);
        }
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    @RequestMapping(value = "/list")
    public ModelAndView list(Page page)
    {
        logBefore(logger, "list Pictures");
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        try {
            pd = this.getPageData();

            String KEYW = pd.getString("keyword");

            if (null != KEYW && !"".equals(KEYW)) {
                KEYW = KEYW.trim();
                pd.put("KEYW", KEYW);
            }

            page.setPd(pd);
            List<PageData> varList = _picturesService.list(page);
            mv.setViewName("information/pictures/pictures_list");
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
        logBefore(logger, "go to new Pictures page");
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            mv.setViewName("information/pictures/pictures_add");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit()
    {
        logBefore(logger, "go to modify Pictures page");
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            pd = _picturesService.findById(pd);
            mv.setViewName("information/pictures/pictures_edit");
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
        logBefore(logger, "delete Pictures");
        PageData pd = new PageData();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            pd = this.getPageData();
            if (Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
                List<PageData> pdList = new ArrayList<PageData>();
                List<PageData> pathList = new ArrayList<PageData>();
                String DATA_IDS = pd.getString("DATA_IDS");
                if (null != DATA_IDS && !"".equals(DATA_IDS)) {
                    String ArrayDATA_IDS[] = DATA_IDS.split(",");
                    pathList = _picturesService.getAllById(ArrayDATA_IDS);
                    for (int i = 0; i < pathList.size(); i++) {
                        DelAllFile.delFolder(PathUtil.getClasspath() + Const.FILEPATHIMG + pathList.get(i).getString("PATH"));
                    }
                    _picturesService.deleteAll(ArrayDATA_IDS);
                    pd.put("msg", "ok");
                } else {
                    pd.put("msg", "no");
                }
                pdList.add(pd);
                map.put("list", pdList);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        return AppUtil.returnObject(pd, map);
    }

    /**
     * export excel
     * @return
     */
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel()
    {
        logBefore(logger, "export Pictures to excel");
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            List<String> titles = new ArrayList<String>();
            titles.add("标题");         // 1
            titles.add("文件名");       // 2
            titles.add("路径");        // 3
            titles.add("创建时间");     // 4
            titles.add("属于");        // 5
            titles.add("备注");        // 6
            dataMap.put("titles", titles);
            List<PageData> varOList = _picturesService.listAll(pd);
            List<PageData> varList = new ArrayList<PageData>();
            for (int i = 0; i < varOList.size(); i++) {
                PageData vpd = new PageData();
                vpd.put("var1", varOList.get(i).getString("TITLE"));	// 1
                vpd.put("var2", varOList.get(i).getString("NAME"));	// 2
                vpd.put("var3", varOList.get(i).getString("PATH"));	// 3
                vpd.put("var4", varOList.get(i).getString("CREATETIME"));	// 4
                vpd.put("var5", varOList.get(i).getString("MASTER_ID"));	// 5
                vpd.put("var6", varOList.get(i).getString("BZ"));	// 6
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

    @RequestMapping(value = "/deltp")
    public void deltp(PrintWriter out)
    {
        logBefore(logger, "delete picture");
        try {
            PageData pd = new PageData();
            pd = this.getPageData();
            String PATH = pd.getString("PATH");
            DelAllFile.delFolder(PathUtil.getClasspath() + Const.FILEPATHIMG + pd.getString("PATH")); 
            if (PATH != null) {
                _picturesService.delTp(pd);
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getHC()
    {
        Subject currentUser = SecurityUtils.getSubject();  // shiro session handling
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
