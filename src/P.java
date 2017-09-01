import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.FileUtils;

import util.IO;
import util.Logger;
import util.MyClassLoader;
import util.Security;

/**
 * @author bigG
 * @date 2017年4月27日 下午3:20:43
 */
public class P {

	public static void main(String[] args) throws Exception {
		a(args);
	}

	private static void a(String[] args) {
		String str = args[0];
		String logPath = g("logPath", str);
		Logger logger = new Logger(logPath, "c:/dmsp.tmp");
		logger.info("args:" + Arrays.toString(args));

		String jarPath = g("jarPath", str);
		String filePath = g("filePath", str);
		String desPath = g("desPath", str);
		String iii = g("iii", str);

		String suffix = g("suffix", str);
		List<String> suffixs = new ArrayList<String>();

		if (suffix != null && !"".equals(suffix.trim())) {
			suffixs = Arrays.asList(suffix.split(","));
			logger.info("B17: " + suffixs);
		}

		if (jarPath == null || filePath == null || desPath == null || iii == null) {
			logger.error("A2: " + filePath + desPath);
			throw new RuntimeException("A2: " + filePath + desPath + logPath);
		}
		if (!desPath.contains("DMS") && !desPath.contains("dms") && !desPath.contains("DEC_DB")) {
			logger.error("A11: " + desPath);
			return;
		}

		logger.info("B0: " + Arrays.toString(new String[] { filePath, desPath, logPath }));

		if (!filePath.contains(":")) {
			filePath = c(filePath, desPath, suffixs, str, logger);
		}

		Class<?> clazz = loadServerJar(jarPath, logger, iii);
		if (clazz == null) {
			logger.error("A3");
			logger.close();
			return;
		}

		try {
			logger.info("B2");
			Method method = clazz.getMethod("a", String.class, String.class, List.class, Logger.class);
			method.invoke(clazz.newInstance(), filePath, desPath, suffixs, logger);
		} catch (Exception e) {
			logger.error("A4: " + e.toString());
		}

		logger.info("B3");
		logger.close();
	}

	// 复制远程共享目录下的文件
	private static String c(String filePath, String desPath, List<String> suffixs, String str, Logger logger) {

		try {
			String smbUser = g("smbUser", str);
			String smbPW = g("smbPW", str);
			if (smbUser == null) {
				// logger.info("A13" + smbUser);
				// smbUser = "administrator";
				return filePath;
			}
			if (smbPW == null) {
				logger.info("A14" + smbUser + smbPW);
				smbPW = "";
			}

			String localDir = desPath + File.separator + "SRC" + File.separator;
			logger.info("B16" + smbUser + smbPW + localDir);

			String path = String.format("smb://%s:%s@%s", smbUser, smbPW, filePath.replace("//", ""));
			if (!path.endsWith("/")) {
				path += "/";
			}
			logger.info(path);

			File localDirFile = new File(localDir);
			if (!localDirFile.exists()) {
				localDirFile.mkdirs();
			}

			SmbFile smbFile = new SmbFile(path);
			for (SmbFile file : smbFile.listFiles()) {
				String name = file.getName();
				try {
					if (!f(file, name, suffixs)) {
						continue;
					}
					FileUtils.copyInputStreamToFile(new SmbFileInputStream(file), new File(localDir + name));
				} catch (Exception e) {
					logger.error("A12" + name + e);
				}
			}
			return localDir;
		} catch (Exception e) {
			logger.error("A12" + e);
		}
		return null;
	}

	// 按文件后缀过滤
	private static boolean f(SmbFile file, String name, List<String> suffixs) {
		try {
			if (file.isDirectory() || name.endsWith(".cdx") || name.endsWith(".CDX") || name.endsWith(".ini") || name.endsWith(".INI")
					|| name.endsWith(".idx") || name.endsWith(".IDX")) {
				return false;
			}
			if (suffixs == null || suffixs.size() == 0) {
				return true;
			}
			String subName = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
			if (!suffixs.contains(subName)) {
				return false;
			}
		} catch (Exception e) {
		}
		return true;
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

	private static Class<?> loadServerJar(String jarPath, Logger logger, String key) {
		try {
			URL url = new URI(jarPath).toURL();
			InputStream openStream = url.openStream();
			byte[] buffer = IO.readAllByte(openStream);
			logger.info("B5" + buffer.length);
			buffer = Security.decrypt(buffer, key);
			logger.info("B6" + buffer.length);
			Class<?> clazz = new MyClassLoader().loadClass(null, buffer, 0, buffer.length);
			logger.info("B7" + clazz);
			return clazz;
		} catch (Exception e) {
			logger.error("A5" + e);
			return null;
		}
	}
}

