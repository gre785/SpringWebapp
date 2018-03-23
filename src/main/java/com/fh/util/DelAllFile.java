
package com.fh.util;

import java.io.File;

public class DelAllFile
{
    public static void main(String args[])
    {
        delFolder("e:/e/a");
        // delFolder("D:/WEBSerser/apache-tomcat-8.0.15/me-webapps/UIMYSQL/WEB-INF/classes/../../admin00/ftl/code");
        // delFolder("D:\\WEBSerser\\apache-tomcat-8.0.15\\me-webapps\\UIMYSQL\\admin00\\ftl\\code");
        // delFolder("D:/WEBSerser/apache-tomcat-8.0.15/me-webapps/UIMYSQL/WEB-INF/classes/../../admin00/ftl/code");
        System.out.println("deleted");
    }

    public static void delFolder(String folderPath)
    {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path)
    {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        String[] fileList = file.list();
        File temp = null;
        for (String fname : fileList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + fname);
            } else {
                temp = new File(path + File.separator + fname);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + fname);
                delFolder(path + "/" + fname);
                flag = true;
            }
        }
        return flag;
    }
}
