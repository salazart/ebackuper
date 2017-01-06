package com.sz.ebackuper.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class EmailParser {
	private static final String SPLITER = "From -";
	private static final String FILE_NOT_SELECTED = "Файл не вибраний.";
	private static final String WORK_COMPLETE = "Роботу закінчено.";
	
	public static void main(String[] args) {
		
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            BackupSpliter backupSpliter = new BackupSpliter(file, SPLITER);
            try {
				List<String> tempFiles = backupSpliter.getFiles();
				System.out.println("Count messages: " + tempFiles.size());
				
				tempFiles.forEach(fileName -> new File(fileName).delete());
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            JOptionPane.showMessageDialog(null, WORK_COMPLETE);
        } else {
        	JOptionPane.showMessageDialog(null, FILE_NOT_SELECTED);
        }
		
//		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
//		fileChooser.setMultiSelectionEnabled(true);
//		int returnVal = fileChooser.showOpenDialog(null);
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//            List<File> files = Arrays.asList(fileChooser.getSelectedFiles());
//            FileParser fileParser = new FileParser();
//            fileParser.readFiles(files);
//            
//            JOptionPane.showMessageDialog(null, WORK_COMPLETE);
//        } else {
//        	JOptionPane.showMessageDialog(null, FILE_NOT_SELECTED);
//        }
	}
	
}
