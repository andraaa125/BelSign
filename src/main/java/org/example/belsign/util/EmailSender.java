package org.example.belsign.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailSender {

    public static void sendEmailWithAttachment(String toEmail, File pdfFile) throws Exception {
        final String fromEmail = "your_email@gmail.com";        // <-- Replace with your email
        final String password = "your_app_password";            // <-- Use App Password, not your main password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("QC Report");

        // Body
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText("Please find the attached QC report PDF.");

        // Attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(pdfFile);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("Email with PDF sent to " + toEmail);
    }
}
