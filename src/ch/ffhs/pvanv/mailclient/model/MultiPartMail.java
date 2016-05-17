package ch.ffhs.pvanv.mailclient.model;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

public class MultiPartMail {

    Address[] fromAddress = null;
    String from = null;
    String subject = null;
    String sentDate = null;

    String contentType = null;
    String messageContent = null;
    ArrayList<MimeBodyPart> attachedFiles = null;

    // store attachment file name, separated by comma
    String attachFiles = "";

    /* Getter und Setter */
    public Address[] getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(Address[] fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(String attachFiles) {
        this.attachFiles = attachFiles;
    }

    /**
     * Die Email wird direkt ausgelesen und kann weiterverwendet werden
     */
    public MultiPartMail(Message message)
            throws MessagingException, IOException {
        fromAddress = message.getFrom();
        from = fromAddress[0].toString();
        subject = message.getSubject();
        sentDate = message.getSentDate().toString();

        contentType = message.getContentType();
        messageContent = "";
        attachedFiles = new ArrayList<MimeBodyPart>();

        // store attachment file name, separated by comma
        String attachFiles = "";

        if (contentType.contains("multipart")) {
            // content may contain attachments
            Multipart multiPart = (Multipart) message.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart
                        .getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    attachedFiles.add(part);
                } else {
                    // this part may be the message content
                    if (part.getContentType().contains("text/plain"))
                        messageContent = part.getContent().toString();
                }
            }

            if (attachFiles.length() > 1) {
                attachFiles = attachFiles.substring(0,
                        attachFiles.length() - 2);
            }
        } else if (contentType.contains("text/plain")) {
            Object content = message.getContent();
            if (content != null) {
                messageContent = content.toString();
            }
        }
    }

    /**
     * Anhänge auf Festplatte speichern
     */
    public void saveAttachmentsToDisc()
            throws MessagingException, IOException {
        for (MimeBodyPart part : attachedFiles) {
            String fileName = part.getFileName();
            attachFiles += fileName + ", ";
            part.saveFile(fileName);
            System.out.println("saved file: " + fileName);
        }
    }

}
