package com.zhiyun.estore.common.utils.mail;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 简单邮件（不带附件的邮件）发送器
 */
public class SimpleMailSender {

    /**
	 * 以文本格式发送邮件　　 
	 * @param mailInfo 待发送的邮件的信息　　 
	 */
    public boolean sendTextMail(MailSenderInfo mailInfo) {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        try {
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
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
	 *以HTML格式发送邮件　　 
	 * @param mailInfo 待发送的邮件信息　　 
	 */
    public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        try {
            Message mailMessage = new MimeMessage(sendMailSession);
            Address from = new InternetAddress(mailInfo.getFromAddress());
            mailMessage.setFrom(from);
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            mailMessage.setSubject(mailInfo.getSubject());
            mailMessage.setSentDate(new Date());
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            html.setContent(mailInfo.getContent(), "text/html;charset=utf-8");
            mainPart.addBodyPart(html);
            mailMessage.setContent(mainPart);
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
