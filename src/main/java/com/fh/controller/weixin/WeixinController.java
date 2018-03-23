
package com.fh.controller.weixin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.marker.weixin.DefaultSession;
import org.marker.weixin.HandleMessageAdapter;
import org.marker.weixin.MySecurity;
import org.marker.weixin.msg.Data4Item;
import org.marker.weixin.msg.Msg4Event;
import org.marker.weixin.msg.Msg4Image;
import org.marker.weixin.msg.Msg4ImageText;
import org.marker.weixin.msg.Msg4Link;
import org.marker.weixin.msg.Msg4Location;
import org.marker.weixin.msg.Msg4Text;
import org.marker.weixin.msg.Msg4Video;
import org.marker.weixin.msg.Msg4Voice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fh.controller.base.BaseController;

import com.fh.service.weixin.command.CommandService;
import com.fh.service.weixin.imgmsg.ImgmsgService;
import com.fh.service.weixin.textmsg.TextmsgService;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Tools;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

@Controller
@RequestMapping(value = "/weixin")
public class WeixinController
    extends BaseController
{

    @Resource(name = "textmsgService")
    private TextmsgService _textmsgService;
    @Resource(name = "commandService")
    private CommandService _commandService;
    @Resource(name = "imgmsgService")
    private ImgmsgService _imgmsgService;

    /**
     * @param out
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/index")
    public void index(PrintWriter out, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        logBefore(logger, "weixin API");
        PageData pd = getPageData();
        try {
            String signature = pd.getString("signature");
            String timestamp = pd.getString("timestamp");
            String nonce = pd.getString("nonce");
            String echostr = pd.getString("echostr");

            if (null != signature && null != timestamp && null != nonce && null != echostr) {
                logBefore(logger, "weixin log in");
                List<String> list = new ArrayList<String>(3)
                {
                    private static final long serialVersionUID = 2621444383666420433L;

                    public String toString()
                    {
                        return get(0) + get(1) + get(2);
                    }
                };
                list.add(Tools.readTxtFile(Const.WEIXIN));
                list.add(timestamp);
                list.add(nonce);
                Collections.sort(list);
                String tmpStr = new MySecurity().encode(list.toString(), MySecurity.SHA_1);

                if (signature.equals(tmpStr)) {
                    out.write(echostr);
                } else {
                    out.write("");
                }
                out.flush();
                out.close();
            } else {
                logBefore(logger, "message handling");
                response.reset();
                sendMsg(request, response);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * @param request
     * @param response
     * @throws Exception
     */
    public void sendMsg(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        InputStream is = request.getInputStream();
        OutputStream os = response.getOutputStream();

        final DefaultSession session = DefaultSession.newInstance();
        session.addOnHandleMessageListener(new HandleMessageAdapter()
        {
            @Override
            public void onEventMsg(Msg4Event msg)
            {
                /** msg.getEvent()
                 * unsubscribe, subscribe
                 */
                if ("subscribe".equals(msg.getEvent())) {
                    returnMSg(msg, null, "subscribe");
                }
            }

            @Override
            public void onTextMsg(Msg4Text msg)
            {
                returnMSg(null, msg, msg.getContent().trim());
            }

            @Override
            public void onImageMsg(Msg4Image msg)
            {
                super.onImageMsg(msg);
            }

            @Override
            public void onLocationMsg(Msg4Location msg)
            {
                super.onLocationMsg(msg);
            }

            @Override
            public void onLinkMsg(Msg4Link msg)
            {
                super.onLinkMsg(msg);
            }

            @Override
            public void onVideoMsg(Msg4Video msg)
            {
                super.onVideoMsg(msg);
            }

            @Override
            public void onVoiceMsg(Msg4Voice msg)
            {
                super.onVoiceMsg(msg);
            }

            @Override
            public void onErrorMsg(int errorCode)
            {
                super.onErrorMsg(errorCode);
            }

            /**
             * @param emsg
             * @param tmsg
             * @param getmsg
             */
            public void returnMSg(Msg4Event emsg, Msg4Text tmsg, String getmsg)
            {
                PageData msgpd;
                PageData pd = new PageData();
                String toUserName, fromUserName, createTime;
                if (null == emsg) {
                    toUserName = tmsg.getToUserName();
                    fromUserName = tmsg.getFromUserName();
                    createTime = tmsg.getCreateTime();
                } else {
                    toUserName = emsg.getToUserName();
                    fromUserName = emsg.getFromUserName();
                    createTime = emsg.getCreateTime();
                }
                pd.put("KEYWORD", getmsg);
                try {
                    msgpd = _textmsgService.findByKw(pd);
                    if (null != msgpd) {
                        Msg4Text rmsg = new Msg4Text();
                        rmsg.setFromUserName(toUserName);
                        rmsg.setToUserName(fromUserName);
                        rmsg.setContent(msgpd.getString("CONTENT"));
                        session.callback(rmsg);
                    } else {
                        msgpd = _imgmsgService.findByKw(pd);
                        if (null != msgpd) {
                            Msg4ImageText mit = new Msg4ImageText();
                            mit.setFromUserName(toUserName);
                            mit.setToUserName(fromUserName);
                            mit.setCreateTime(createTime);
                            if (null != msgpd.getString("TITLE1") && null != msgpd.getString("IMGURL1")) {
                                Data4Item d1 = new Data4Item(msgpd.getString("TITLE1"), msgpd.getString("DESCRIPTION1"), msgpd.getString("IMGURL1"),
                                    msgpd.getString("TOURL1"));
                                mit.addItem(d1);

                                if (null != msgpd.getString("TITLE2") && null != msgpd.getString("IMGURL2")
                                        && !"".equals(msgpd.getString("TITLE2").trim()) && !"".equals(msgpd.getString("IMGURL2").trim())) {
                                    Data4Item d2 = new Data4Item(msgpd.getString("TITLE2"), msgpd.getString("DESCRIPTION2"),
                                        msgpd.getString("IMGURL2"), msgpd.getString("TOURL2"));
                                    mit.addItem(d2);
                                }
                                if (null != msgpd.getString("TITLE3") && null != msgpd.getString("IMGURL3")
                                        && !"".equals(msgpd.getString("TITLE3").trim()) && !"".equals(msgpd.getString("IMGURL3").trim())) {
                                    Data4Item d3 = new Data4Item(msgpd.getString("TITLE3"), msgpd.getString("DESCRIPTION3"),
                                        msgpd.getString("IMGURL3"), msgpd.getString("TOURL3"));
                                    mit.addItem(d3);
                                }
                                if (null != msgpd.getString("TITLE4") && null != msgpd.getString("IMGURL4")
                                        && !"".equals(msgpd.getString("TITLE4").trim()) && !"".equals(msgpd.getString("IMGURL4").trim())) {
                                    Data4Item d4 = new Data4Item(msgpd.getString("TITLE4"), msgpd.getString("DESCRIPTION4"),
                                        msgpd.getString("IMGURL4"), msgpd.getString("TOURL4"));
                                    mit.addItem(d4);
                                }
                                if (null != msgpd.getString("TITLE5") && null != msgpd.getString("IMGURL5")
                                        && !"".equals(msgpd.getString("TITLE5").trim()) && !"".equals(msgpd.getString("IMGURL5").trim())) {
                                    Data4Item d5 = new Data4Item(msgpd.getString("TITLE5"), msgpd.getString("DESCRIPTION5"),
                                        msgpd.getString("IMGURL5"), msgpd.getString("TOURL5"));
                                    mit.addItem(d5);
                                }
                                if (null != msgpd.getString("TITLE6") && null != msgpd.getString("IMGURL6")
                                        && !"".equals(msgpd.getString("TITLE6").trim()) && !"".equals(msgpd.getString("IMGURL6").trim())) {
                                    Data4Item d6 = new Data4Item(msgpd.getString("TITLE6"), msgpd.getString("DESCRIPTION6"),
                                        msgpd.getString("IMGURL6"), msgpd.getString("TOURL6"));
                                    mit.addItem(d6);
                                }
                                if (null != msgpd.getString("TITLE7") && null != msgpd.getString("IMGURL7")
                                        && !"".equals(msgpd.getString("TITLE7").trim()) && !"".equals(msgpd.getString("IMGURL7").trim())) {
                                    Data4Item d7 = new Data4Item(msgpd.getString("TITLE7"), msgpd.getString("DESCRIPTION7"),
                                        msgpd.getString("IMGURL7"), msgpd.getString("TOURL7"));
                                    mit.addItem(d7);
                                }
                                if (null != msgpd.getString("TITLE8") && null != msgpd.getString("IMGURL8")
                                        && !"".equals(msgpd.getString("TITLE8").trim()) && !"".equals(msgpd.getString("IMGURL8").trim())) {
                                    Data4Item d8 = new Data4Item(msgpd.getString("TITLE8"), msgpd.getString("DESCRIPTION8"),
                                        msgpd.getString("IMGURL8"), msgpd.getString("TOURL8"));
                                    mit.addItem(d8);
                                }
                            }
                            session.callback(mit);
                        } else {
                            msgpd = _commandService.findByKw(pd);
                            if (null != msgpd) {
                                Runtime runtime = Runtime.getRuntime();
                                runtime.exec(msgpd.getString("COMMANDCODE"));
                            } else {
                                Msg4Text rmsg = new Msg4Text();
                                rmsg.setFromUserName(toUserName);
                                rmsg.setToUserName(fromUserName);
                                rmsg.setContent("无匹配结果");
                                session.callback(rmsg);
                            }
                        }
                    }
                } catch (Exception e1) {
                    logBefore(logger, "matching error");
                }
            }

        });
        /*必须调用这两个方法   如果不调用close方法，将会出现响应数据串到其它Servlet中。*/
        session.process(is, os);	// 处理微信消息
        session.close();			// 关闭Session
    }

    public final static String gz_url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=";

    @RequestMapping(value = "/getGz")
    public void getGz(PrintWriter out)
    {
        logBefore(logger, "获取关注列表");
        try {
            String access_token = readTxtFile("e:/access_token.txt");

            System.out.println(access_token + "============");

            String requestUrl = gz_url.replace("ACCESS_TOKEN", access_token);

            System.out.println(requestUrl + "============");

            JSONObject jsonObject = httpRequst(requestUrl, "GET", null);
            System.out.println(jsonObject);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    public String readTxtFile(String filePath)
    {
        try {
            String encoding = "utf-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    // System.out.println(lineTxt);
                    return lineTxt;
                }
                read.close();
                bufferedReader.close();
            } else {
                System.out.println("cannot local the file");
            }
        } catch (Exception e) {
            System.out.println("file loading error");
            e.printStackTrace();
        }
        return "";
    }

    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?"
            + "grant_type=client_credential&appid=APPID&secret=APPSECRET";

    @RequestMapping(value = "/getAt")
    public void getAt(PrintWriter out)
    {
        logBefore(logger, "get access_token");
        try {
            String appid = "wx9f43c8daa1c13934";
            String appsecret = "2c7f6552a5a845b49d47f65dd90beb50";

            String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
            JSONObject jsonObject = httpRequst(requestUrl, "GET", null);
            PrintWriter pw;
            try {
                pw = new PrintWriter(new FileWriter("e:/access_token.txt"));
                pw.print(jsonObject.getString("access_token"));
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    public JSONObject httpRequst(String requestUrl, String requetMethod, String outputStr)
    {
        JSONObject jsonobject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            TrustManager[] tm = { new MyX509TrustManager()};
            SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
            sslcontext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslcontext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection)url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod(requetMethod);
            if ("GET".equalsIgnoreCase(requetMethod))
                httpUrlConn.connect();
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonobject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {} catch (Exception e) {}
        return jsonobject;
    }
}

class MyX509TrustManager
    implements X509TrustManager
{
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {}

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {}

    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }
}
