package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author bigG
 * @date 2017年4月27日 下午4:45:22
 */
@SuppressWarnings("deprecation")
public class Logger {

	private static FileWriter writer;

	public Logger(String logPath, String defaultPath) {
		try {
			getWriter(logPath);
		} catch (Exception e) {
			try {
				getWriter(defaultPath);
			} catch (Exception e1) {
			}
		}
	}

	private void getWriter(String logPath) throws IOException {
		File file = new File(logPath);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) parentFile.mkdirs();
		} else if (file.isFile() && file.length() >= 1024 * 1024 * 50) {
			writer = new FileWriter(file, false);
			return;
		}
		writer = new FileWriter(file, true);
	}

	public void info(String string) {
		if (writer == null) return;
		try {
			writer.write("[Info] [" + new Date().toLocaleString() + "]:" + string + "\r\n");
			writer.flush();
		} catch (IOException e) {
		}
	}

	public void error(String string) {
		if (writer == null) return;
		try {
			writer.write("[Error] [" + new Date().toLocaleString() + "]:" + string + "\r\n");
			writer.flush();
		} catch (IOException e) {
		}
	}

	public void close() {
		if (writer == null) return;
		try {
			writer.write("====================================================\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
	}
}
