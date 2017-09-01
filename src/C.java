import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Logger;

/**
 * @author bigG
 * @date 2017年4月27日 下午8:07:36
 */
public class C {

	public static void main(String[] args) {
		a(args);
	}

	private static ArrayList<File> fs = new ArrayList<File>();
	private static int loopDepth = 0;
	private static void a(String[] args) {
		String str = args[0];
		Logger logger = null;
		String logPath = g("logPath", str);
		logger = new Logger(logPath, "c:/dmsc.tmp");
		String desPath = g("desPath", str);
		if (!desPath.contains("DMS") && !desPath.contains("dms") && !desPath.contains("DEC_DB")) {
			logger.error("A11: " + desPath);
			return;
		}
		logger.info("B1: " + Arrays.toString(new String[] { desPath, logPath }));
		File desFile = new File(desPath);
		if (!desFile.exists()) {
			logger.error("A0: " + desFile);
			return;
		}
		deleteDir(desFile, logger);
		// 客户端读完数据后，仍会占用文件一分钟左右
		reDel(fs, logger);
		logger.info("B3");
		logger.close();
	}

	private static String g(String a, String b) {
		String r = null;
		try {
			Matcher matcher = Pattern.compile(a + ".*?'.*?:.*?'(.*?)'").matcher(b);
			if (matcher.find()) {
				r = matcher.group(1).replace(" ", "");
			}
		} catch (Exception e) {
		}
		return r;
	}

	private static void deleteDir(File path, Logger logger) {
		if (path.isFile()) {
			if (!path.delete()) {
				logger.error("A6" + path);
				fs.add(path);
			}
			return;
		}
		logger.info("B8" + path);
		for (File file : path.listFiles()) {
			deleteDir(file, logger);
		}
		if (path.delete()) {
			logger.info("B9" + path);
		} else {
			logger.error("A7" + path);
			fs.add(path);
		}
	}

	private static void reDel(ArrayList<File> files, Logger logger) {

		loopDepth++;
		if (files.size() == 0 || loopDepth > 60 * 2) {// 限制循环不超过2分钟
			return;
		}
		logger.info("B13 " + files.size() + ", B14 " + loopDepth);
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<File> remain = new ArrayList<File>();
		for (File file : files) {
			if (file.delete()) {
				logger.info("B15" + file);
			} else {
				remain.add(file);
			}
		}
		reDel(remain, logger);
	}
}
