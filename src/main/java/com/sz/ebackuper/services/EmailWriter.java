package com.sz.ebackuper.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EmailWriter {
	private PrintWriter out;

	public EmailWriter(String fileName) throws IOException {
		out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
	}

	public void addLine(String line) {
		if (out != null && line != null) {
			out.println(line);
		}
	}

	public void save() {
		if (out != null) {
			out.flush();
			out.close();
		}
	}
}
