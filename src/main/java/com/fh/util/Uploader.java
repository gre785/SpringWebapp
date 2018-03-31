
package com.fh.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import Decoder.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;

public class Uploader
{
    private static final int MAX_SIZE = 500 * 1024;
    private String _url = "";
    private String _fileName = "";
    private String _state = "";
    private String _type = "";
    private String _originalName = "";
    private String _size = "";
    private String _title = "";
    private String _savePath = "upload";
    private String[] _allowFiles = { ".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv", ".gif", ".png", ".jpg", ".jpeg", ".bmp"};
    private HashMap<String, String> _errorInfo = new HashMap<String, String>();
    private Map<String, String> _params = null;
    private InputStream _inputStream = null;
    private HttpServletRequest _request = null;
    private long _maxSize = 0;

    public static final String ENCODEING = System.getProperties().getProperty("file.encoding");

    public Uploader(HttpServletRequest request)
    {
        _request = request;
        _params = new HashMap<String, String>();
        setMaxSize(Uploader.MAX_SIZE);
        // tmp is never userd?
        HashMap<String, String> tmp = _errorInfo;
        tmp.put("SUCCESS", "SUCCESS");
        tmp.put("NOFILE", "\\u672a\\u5305\\u542b\\u6587\\u4ef6\\u4e0a\\u4f20\\u57df");
        // 不允许的文件格式
        tmp.put("TYPE", "\\u4e0d\\u5141\\u8bb8\\u7684\\u6587\\u4ef6\\u683c\\u5f0f");
        // 文件大小超出限制
        tmp.put("SIZE", "\\u6587\\u4ef6\\u5927\\u5c0f\\u8d85\\u51fa\\u9650\\u5236");
        // 请求类型错误
        tmp.put("ENTYPE", "\\u8bf7\\u6c42\\u7c7b\\u578b\\u9519\\u8bef");
        // 上传请求异常
        tmp.put("REQUEST", "\\u4e0a\\u4f20\\u8bf7\\u6c42\\u5f02\\u5e38");
        // 未找到上传文件
        tmp.put("FILE", "\\u672a\\u627e\\u5230\\u4e0a\\u4f20\\u6587\\u4ef6");
        // IO异常
        tmp.put("IO", "IO\\u5f02\\u5e38");
        // 目录创建失败
        tmp.put("DIR", "\\u76ee\\u5f55\\u521b\\u5efa\\u5931\\u8d25");
        // 未知错误
        tmp.put("UNKNOWN", "\\u672a\\u77e5\\u9519\\u8bef");

        parseParams();
    }

    public void upload()
        throws Exception
    {
        boolean isMultipart = ServletFileUpload.isMultipartContent(_request);
        if (!isMultipart) {
            _state = _errorInfo.get("NOFILE");
            return;
        }
        if (_inputStream == null) {
            _state = _errorInfo.get("FILE");
            return;
        }
        _title = getParameter("pictitle");
        try {
            String savePath = getFolder(_savePath);
            if (!checkFileType(_originalName)) {
                _state = _errorInfo.get("TYPE");
                return;
            }
            _fileName = getName(_originalName);
            _type = getFileExt(_fileName);
            _url = savePath + "/" + _fileName;
            FileOutputStream fos = new FileOutputStream(getPhysicalPath(_url));
            BufferedInputStream bis = new BufferedInputStream(_inputStream);
            byte[] buff = new byte[128];
            int count = -1;
            while ((count = bis.read(buff)) != -1) {
                fos.write(buff, 0, count);
            }
            bis.close();
            fos.close();

            _state = _errorInfo.get("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            _state = _errorInfo.get("IO");
        }

    }

    public void uploadBase64(String fieldName)
    {
        String savePath = getFolder(_savePath);
        String base64Data = _request.getParameter(fieldName);
        _fileName = getName("test.png");
        _url = savePath + "/" + _fileName;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            File outFile = new File(getPhysicalPath(_url));
            OutputStream ro = new FileOutputStream(outFile);
            byte[] b = decoder.decodeBuffer(base64Data);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            ro.write(b);
            ro.flush();
            ro.close();
            _state = _errorInfo.get("SUCCESS");
        } catch (Exception e) {
            _state = _errorInfo.get("IO");
        }
    }

    public String getParameter(String name)
    {
        return _params.get(name);
    }

    private boolean checkFileType(String fileName)
    {
        Iterator<String> type = Arrays.asList(_allowFiles).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExt(String fileName)
    {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private void parseParams()
    {
        DiskFileItemFactory dff = new DiskFileItemFactory();
        try {
            ServletFileUpload sfu = new ServletFileUpload(dff);
            sfu.setSizeMax(_maxSize);
            sfu.setHeaderEncoding(Uploader.ENCODEING);
            FileItemIterator fii = sfu.getItemIterator(_request);
            while (fii.hasNext()) {
                FileItemStream item = fii.next();
                if (item.isFormField()) {
                    _params.put(item.getFieldName(), getParameterValue(item.openStream()));
                } else {
                    if (_inputStream == null) {
                        _inputStream = item.openStream();
                        _originalName = item.getName();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            _state = _errorInfo.get("UNKNOWN");
        }
    }

    private String getName(String fileName)
    {
        Random random = new Random();
        return _fileName = "" + random.nextInt(10000) + System.currentTimeMillis() + getFileExt(fileName);
    }

    private String getFolder(String path)
    {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        path += "/" + formater.format(new Date());
        File dir = new File(getPhysicalPath(path));
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                _state = _errorInfo.get("DIR");
                return "";
            }
        }
        return path;
    }

    private String getPhysicalPath(String path)
    {
        String servletPath = _request.getServletPath();
        String realPath = _request.getSession().getServletContext().getRealPath(servletPath);
        return new File(realPath).getParent() + "/" + path;
    }

    private String getParameterValue(InputStream in)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String result = "";
        String tmpString = null;
        try {
            while ((tmpString = reader.readLine()) != null) {
                result += tmpString;
            }
        } catch (Exception e) {}

        return result;

    }

    private byte[] getFileOutputStream(InputStream in)
    {
        try {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            return null;
        }
    }

    public void setSavePath(String savePath)
    {
        _savePath = savePath;
    }

    public void setAllowFiles(String[] allowFiles)
    {
        _allowFiles = allowFiles;
    }

    public void setMaxSize(long size)
    {
        _maxSize = size * 1024;
    }

    public String getSize()
    {
        return _size;
    }

    public String getUrl()
    {
        return _url;
    }

    public String getFileName()
    {
        return _fileName;
    }

    public String getState()
    {
        return _state;
    }

    public String getTitle()
    {
        return _title;
    }

    public String getType()
    {
        return _type;
    }

    public String getOriginalName()
    {
        return _originalName;
    }
}
