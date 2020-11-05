package com.veronikalebedyuk.dialogforbetter.classes;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class JavaMailWithAttachments extends AsyncTask<Void,Void,Void> {
    private Context context;
    private Session session;
    private String email, subject,message;
    private File file;

    public JavaMailWithAttachments(Context context, String email, String subject, String message, File file) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.file = file;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","465");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("dialog.forbetter@gmail.com", Utils.PASSWORD);
            }
        });
        MimeMessage mimeMessage=new MimeMessage(session);
        MimeMultipart multipart = new MimeMultipart();
        try {
            mimeMessage.setFrom(new InternetAddress(Utils.EMAIL));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email))) ;
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText("ваша статистика: ");

            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart messageAttachment = new MimeBodyPart();
            messageAttachment.attachFile(file);
            multipart.addBodyPart(messageAttachment);
            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
