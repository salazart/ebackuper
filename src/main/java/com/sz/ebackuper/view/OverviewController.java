package com.sz.ebackuper.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.sz.ebackuper.services.BackupSpliter;
import com.sz.ebackuper.services.MessageReader;
import com.sz.ebackuper.services.ResultWriter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class OverviewController {
	private static final String FILE_NOT_FOUND = "Файл бекапу не обраний. Оберіть файл.";
	private static final String WORK_COMPLETE = "Роботу закінчено.";
	@FXML
	private TextField textPath;
	
	@FXML
	private Button viewPath;
	
	@FXML
	private TextField spliterator;
	
	@FXML
	private Button startButton;
	
	@FXML
    private void initialize() {
		textPath.setText(System.getProperty("user.dir"));
		
		spliterator.setText("From MAILER-DAEMON");
	}
	
	@FXML
    private void startProcess() {
		if(textPath.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);
		} else {
			BackupSpliter backupSpliter = new BackupSpliter(new File(textPath.getText()), spliterator.getText());
            try {
				List<String> tempFiles = backupSpliter.getFiles();
				System.out.println("Count messages: " + tempFiles.size());
				
				ResultWriter resultWriter = new ResultWriter();
				
				tempFiles.stream()
					.map(tempFile -> MessageReader.readMessage(tempFile))
					.forEach(row -> resultWriter.addRow(row));
				
				resultWriter.saveResult();

				tempFiles.forEach(fileName -> {
					try {
						Files.delete(Paths.get(fileName));
					} catch (IOException e) {
						System.err.println(e);
					}
				});
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
            JOptionPane.showMessageDialog(null, WORK_COMPLETE);
		}
	}
	
	@FXML
    private void viewFolder() {
		String folder = textPath.getText().isEmpty() ? System.getProperty("user.dir") : textPath.getText();
		JFileChooser fileChooser = new JFileChooser(folder);
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            textPath.setText(file.getAbsolutePath());
        } else {
        	textPath.setText("");
        }
	}
}
