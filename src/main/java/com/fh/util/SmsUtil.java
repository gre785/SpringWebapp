
package com.fh.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SmsUtil
{
    private static String URL = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

    public static void main(String[] args)
    {
        sendSms2("13511111111", "您的验证码是：1111。请不要把验证码泄露给其他人。");
    }

    public static void sendSms1(String mobile, String code)
    {

        String account = "", password = "";
        String strSMS1 = Tools.readTxtFile(Const.SMS1);
        if (null != strSMS1 && !"".equals(strSMS1)) {
            String strS1[] = strSMS1.split(",fh,");
            if (strS1.length == 2) {
                account = strS1[0];
                password = strS1[1];
            }
        }
        String PostData = "";
        try {
            PostData = "account=" + account + "&password=" + password + "&mobile=" + mobile + "&content=" + URLEncoder.encode(code, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("messge failed to sent");
        }
        String ret = SMS(PostData, "http://sms.106jiekou.com/utf8/sms.aspx");
        System.out.println(ret);
        /*  
        100			发送成功
        101			验证失败
        102			手机号码格式不正确
        103			会员级别不够
        104			内容未审核
        105			内容过多
        106			账户余额不足
        107			Ip受限
        108			手机号码发送太频繁，请换号或隔天再发
        109			帐号被锁定
        110			发送通道不正确
        111			当前时间段禁止短信发送
        120			系统升级
        */
    }

    public static String SMS(String postData, String postUrl)
    {
        try {
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("connect failed!");
                return "";
            }
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }

    public static void sendSms2(String mobile, String code)
    {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(URL);

        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");

        String content = new String(code);

        String account = "", password = "";
        String strSMS2 = Tools.readTxtFile(Const.SMS2);
        if (null != strSMS2 && !"".equals(strSMS2)) {
            String strS2[] = strSMS2.split(",fh,");
            if (strS2.length == 2) {
                account = strS2[0];
                password = strS2[1];
            }
        }

        NameValuePair[] data = { new NameValuePair("account", account), new NameValuePair("password", password), new NameValuePair("mobile", mobile),
                new NameValuePair("content", content),};
        method.setRequestBody(data);
        try {
            client.executeMethod(method);
            String SubmitResult = method.getResponseBodyAsString();
            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();
            code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");

            System.out.println(code);
            System.out.println(msg);
            System.out.println(smsid);
            if (code == "2") {
                System.out.println("message send successfully");
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void sendSmsAll(List<PageData> list)
    {
        for (PageData pd : list) {
            sendSms2(pd.get("code").toString(), pd.get("mobile").toString());
        }
    }
}
