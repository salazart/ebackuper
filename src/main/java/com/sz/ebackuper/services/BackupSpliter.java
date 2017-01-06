package com.sz.ebackuper.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class BackupSpliter {
	private static final String TEMP_FILE_NAME = "temp_";
	private File file;
	private String spliter;
	private List<String> splitFiles;
	private int tempFileCounter;
	private EmailWriter emailWriter;
	 
	public BackupSpliter(File file, String spliter) {
		super();
		this.file = file;
		this.spliter = spliter;
		splitFiles = new ArrayList<>();
	}
	
	public List<String> getFiles() throws IOException{

		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			String line;
			while ((line = br.readLine()) != null) {
				readLine(line);
			}
			emailWriter.save();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Помилка! " + e.getMessage());
		}
		
		return splitFiles;
	}

	private void readLine(String line) throws IOException {
		if(line.contains(spliter)){
			tempFileCounter++;
			splitFiles.add(TEMP_FILE_NAME + tempFileCounter);
			System.out.println("Try create file:" + TEMP_FILE_NAME + tempFileCounter);
			
			if(emailWriter != null){
				emailWriter.save();
			}
			emailWriter = new EmailWriter(TEMP_FILE_NAME + tempFileCounter);
		}
		emailWriter.addLine(line);
	}
	
}
