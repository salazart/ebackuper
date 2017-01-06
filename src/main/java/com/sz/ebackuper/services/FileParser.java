package com.sz.ebackuper.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileParser {

	private static final String MESSAGE = "message";
	private static final String FROM_MAILER_DAEMON = "From MAILER-DAEMON";
	private final static String MULTIPART = "multipart";
	private static final String OUT_FILE_NAME = "out.xlsx";
	public void readFiles(List<File> files) {
		int i = 0;
		for (File file : files) {
			System.out.print("Reading file " + file + "...");
			i = readFile(file, i);
			System.out.println("OK");
		}
		
		XSSFWorkbook workBook = new XSSFWorkbook();
		XSSFSheet sheet = workBook.createSheet("new");

		for (int j = 0; j < i; j++) {
			String fileName = MESSAGE + j;
			System.out.println("File:\t" + fileName);
			List<String> row = readMessage(fileName);
			
			Row tempRow = sheet.createRow(j);
			for (int k = 0; k < row.size(); k++) {
				Cell cell = tempRow.createCell(k);
				cell.setCellValue(row.get(k));
			}
			
			new File(fileName).delete();
		}
		
		try (OutputStream out = new FileOutputStream(OUT_FILE_NAME)) {
			workBook.write(out);
			out.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Помилка!\n" + e.getMessage());
		}
	}

	private int readFile(File file, int i) {
		String fileName = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file));) {

			String line = null;
			PrintWriter out = null;
			boolean flag = false;
			while ((line = br.readLine()) != null) {
				if (line.contains(FROM_MAILER_DAEMON)) {
					flag = false;
					fileName = MESSAGE + i++;
					System.out.println(fileName);
					
					if(out != null){
						out.flush();
						out.close();
					}
					out = new PrintWriter(
							new BufferedWriter(
									new FileWriter(
											new File(fileName), true)));
					out.println(line);
				} else if (line.contains("filename")) {
					flag = true;
					continue;
				} else if(flag) {
					continue;
				} else {
					out.println(line);
				}
			}
			if(out != null){
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Помилка!\n" + e.getMessage());
		}
		return i;
	}
	
	private List<String> readMessage(String fileName) {
		List<String> row = new ArrayList<>();
		try (InputStream is = new FileInputStream(fileName)){
			Session s = Session.getDefaultInstance(new Properties());
			MimeMessage message = new MimeMessage(s, is);
			
			row.add(message.getMessageID());
			
			Date date = message.getSentDate();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
			row.add(simpleDateFormat.format(date));
			row.add(message.getFrom() != null ? convertText(message.getFrom()[0].toString()) : "");
			row.add(message.getAllRecipients() != null ? convertText(message.getAllRecipients()[0].toString()) : "");
			row.add(message.getSubject());
			
			if(message.getContentType().contains(MULTIPART)){
				MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
				for (int i = 0; i < mimeMultipart.getCount(); i++) {
					BodyPart bodyPart = mimeMultipart.getBodyPart(i);
					if(bodyPart.getContentType().contains("text")){
						row.add(bodyPart.getContent().toString());
					} else if (bodyPart.getContentType().contains("image")){
					} else if (bodyPart.getContentType().contains("application")){}
				}
			} else {
//				System.out.println("Content:\t" + message.getContent());
				row.add(message.getContent().toString());
			}
			
		} catch (MessagingException | IOException e) {
			System.err.println(e);
		}
		return row;
	}
	
	public String convertText(String text){	
		final String CP1251 = "cp1251";
		final String UTF8 = "utf-8";
		if(text.indexOf("\\\"") != -1){
			text = text.replaceAll("\"", "");
		}
		String convertText = null;
		Session s = Session.getInstance(new Properties());
		MimeMessage message2 = new MimeMessage(s);
		try {
			if(text.indexOf("windows-1251") != -1){
				message2.setSubject(text.toString(), CP1251);
			}else if(text.indexOf("UTF-8") != -1){
				message2.setSubject(text.toString(), UTF8);
			}else{
				message2.setSubject(text.toString(), UTF8);
			}
			convertText = message2.getSubject();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return convertText;
	}
}
