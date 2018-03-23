
package com.fh.util.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.fh.controller.base.BaseController;

public class SimpleMailSender
    extends BaseController
{
    public boolean sendTextMail(MailSenderInfo mailInfo)
        throws Exception
    {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        logBefore(logger, "generate session");

        Message mailMessage = new MimeMessage(sendMailSession);
        Address from = new InternetAddress(mailInfo.getFromAddress());
        mailMessage.setFrom(from);
        Address to = new InternetAddress(mailInfo.getToAddress());
        mailMessage.setRecipient(Message.RecipientType.TO, to);
        mailMessage.setSubject(mailInfo.getSubject());
        mailMessage.setSentDate(new Date());
        String mailContent = mailInfo.getContent();
        mailMessage.setText(mailContent);
        Transport.send(mailMessage);
        logBefore(logger, "send successful！");
        return true;
    }

    public boolean sendHtmlMail(MailSenderInfo mailInfo)
        throws Exception
    {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        Message mailMessage = new MimeMessage(sendMailSession);
        Address from = new InternetAddress(mailInfo.getFromAddress());
        mailMessage.setFrom(from);
        Address to = new InternetAddress(mailInfo.getToAddress());
        mailMessage.setRecipient(Message.RecipientType.TO, to);
        mailMessage.setSubject(mailInfo.getSubject());
        mailMessage.setSentDate(new Date());
        Multipart mainPart = new MimeMultipart();
        BodyPart html = new MimeBodyPart();
        html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
        mainPart.addBodyPart(html);
        mailMessage.setContent(mainPart);
        Transport.send(mailMessage);
        return true;
    }

    public boolean sendMail(String title, String content, String type, String tomail)
        throws Exception
    {

        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.qq.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("itfather@1b23.com");
        mailInfo.setPassword("tttt");
        mailInfo.setFromAddress("itfather@1b23.com");
        mailInfo.setToAddress(tomail);
        mailInfo.setSubject(title);
        mailInfo.setContent(content);
        SimpleMailSender sms = new SimpleMailSender();
        if ("1".equals(type)) {
            return sms.sendTextMail(mailInfo);
        } else if ("2".equals(type)) {
            return sms.sendHtmlMail(mailInfo);
        }
        return false;
    }

    public static void sendEmail(String SMTP, String PORT, String EMAIL, String PAW, String toEMAIL, String TITLE, String CONTENT, String TYPE)
        throws Exception
    {
        MailSenderInfo mailInfo = new MailSenderInfo();

        mailInfo.setMailServerHost(SMTP);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(EMAIL);
        mailInfo.setPassword(PAW);
        mailInfo.setFromAddress(EMAIL);
        mailInfo.setToAddress(toEMAIL);
        mailInfo.setSubject(TITLE);
        mailInfo.setContent(CONTENT);
        SimpleMailSender sms = new SimpleMailSender();
        if ("1".equals(TYPE)) {
            sms.sendTextMail(mailInfo);
        } else {
            sms.sendHtmlMail(mailInfo);
        }

    }

    public static void main(String[] args)
    {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.qq.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("itfather@1b23.com");
        mailInfo.setPassword("tttt");
        mailInfo.setFromAddress("itfather@1b23.com");
        mailInfo.setToAddress("313596790@qq.com");
        mailInfo.setSubject("设置邮箱标题");
        mailInfo.setContent("设置邮箱内容");
        SimpleMailSender sms = new SimpleMailSender();
    }
}
