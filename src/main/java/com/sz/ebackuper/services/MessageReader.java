package com.sz.ebackuper.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class MessageReader {
	private static final String HTML_CONTENT = "text/html";
	private static final String TEXT_CONTENT = "text/plain";
	private static final String ATTACHMENT_FOLDER = "/tmp/";
	private static final String MULTIPART_CONTENT = "multipart";
	private static final String DATE_FORMAT = "HH:mm:ss dd.MM.yyyy";

	public static List<String> readMessage(String fileName) {
		List<String> row = new ArrayList<>();
		try (InputStream is = new FileInputStream(fileName)) {
			Session s = Session.getDefaultInstance(new Properties());
			MimeMessage message = new MimeMessage(s, is);

			row.add(message.getMessageID());
			row.add(getDateFormat(message.getSentDate()));
			
			row.add(message.getFrom() != null ? MimeUtility.decodeText(message.getFrom()[0].toString()) : "");
			row.add(message.getAllRecipients() != null ? MimeUtility.decodeText(message.getAllRecipients()[0].toString()) : "");
			
			String messageContent = "";
            
            String folder = System.getProperty("user.dir") + ATTACHMENT_FOLDER;
            Files.createDirectories(Paths.get(folder));
            StringJoiner fileNameJoiner = new StringJoiner(", ");
            
            if (message.getContentType().contains(MULTIPART_CONTENT)) {
                Multipart multiPart = (Multipart) message.getContent();
                for (int partCount = 0; partCount < multiPart.getCount(); partCount++) {
                    MimeBodyPart bodyPart = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    	String decodeFileName = MimeUtility.decodeText(bodyPart.getFileName());
    					bodyPart.saveFile(folder + decodeFileName);
    					fileNameJoiner.add(decodeFileName);
                    } else {
                        messageContent = bodyPart.getContent().toString();
                    }
                }
            } else if (message.getContentType().contains(TEXT_CONTENT)
                    || message.getContentType().contains(HTML_CONTENT)) {
                messageContent = getMessageContent(message, messageContent);
            }
            
            row.add(fileNameJoiner.toString());
            row.add(message.getSubject());
			row.add(messageContent);
            
		} catch (MessagingException | IOException e) {
			System.err.println(e);
		}
		return row;
	}

	private static String getDateFormat(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		return simpleDateFormat.format(date);
	}

	private static String getMessageContent(MimeMessage message, String messageContent)
			throws IOException, MessagingException {
		if (message.getContent() != null) {
		    messageContent = message.getContent().toString();
		}
		return messageContent;
	}
}
