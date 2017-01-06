package com.sz.ebackuper.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ResultWriter {
	private static final String OUT_FILE_NAME = "out.xlsx";
	private static final String SHEET_NAME = "new";
	private XSSFWorkbook workBook;
	private XSSFSheet sheet;
	private int rowCounter;
	
	public ResultWriter() {
		workBook = new XSSFWorkbook();
		sheet = workBook.createSheet(SHEET_NAME);
		createHeaders();
	}
	
	public void createHeaders(){
		List<String> headers = Arrays.asList( "ID", "DATE", "FROM", "TO", "FILES", "SUBJECT", "MESSAGE");
		addRow(headers);
	}
	
	public void addRow(List<String> row){
		Row tempRow = sheet.createRow(rowCounter++);
		for (int k = 0; k < row.size(); k++) {
			Cell cell = tempRow.createCell(k);
			cell.setCellValue(row.get(k));
		}
	}
	
	public void saveResult(){
		try (OutputStream out = new FileOutputStream(OUT_FILE_NAME)) {
			workBook.write(out);
			out.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Помилка!\n" + e.getMessage());
		}
	}

}
