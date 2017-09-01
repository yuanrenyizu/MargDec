import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.IO;
import util.Logger;

/**
 * @author bigG
 * @date 2017年4月26日 下午12:54:25
 */
public class S {

	public int a(String filePath, final String desPath, Logger logger) {
		return a(filePath, desPath, new ArrayList<String>(), logger);
	}
	public int a(String filePath, final String desPath, List<String> suffixs, Logger logger) {
		
		File srcPath = new File(filePath);
		
		if (!srcPath.exists() || !srcPath.isDirectory() || srcPath.list().length == 0) {
			logger.error("A8" + filePath + " " + srcPath.exists() + srcPath.isDirectory() + srcPath.isFile() + srcPath.list().length);
			return 0;
		}
		
		logger.info("B10 " + srcPath.list().length);
		File des = new File(desPath);
		if (!des.exists()) {
			logger.info("B11" + desPath);
			if(!des.mkdirs()){
				logger.error("A10" + desPath);
				return 0;
			}
		}

		final int a5 = 0x10;
		int count = 0;
		for (File file : srcPath.listFiles()) {
			String name = file.getName();
			if (!f(file, name, suffixs)) {
				continue;
			}
			try {
				processFile(file, desPath, a5);
				count++;
			} catch (Exception e) {
				logger.error("A9: " + name + e);
			}
		}
		logger.info("B12: " + count);

		return count;
	}

	//按文件后缀过滤
	private static boolean f(File file, String name, List<String> suffixs) {
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
	
	private void processFile(File file, String desPath, int a5) throws IOException {
		String name = file.getName();
		String decFileName = name.replace(".",	"_") + ".DBF";
		// 读文件
		byte[] allBytes = IO.readAllByte(file);

		// 解密
		byte[] decrypt3 = decrypt(allBytes, a5);

		// 写入文件
		IO.writeByte2File(new File(desPath, decFileName), decrypt3);
	}

	private static final byte[] a2 = { (byte) 0x000000D4, (byte) 0x9D, (byte) 0x7C, (byte) 0x45, (byte) 0x0C, (byte) 0xF1, (byte) 0xC6,
			(byte) 0x9F, (byte) 0x6C, (byte) 0x4F, (byte) 0x1B, (byte) 0xEC, (byte) 0xC1, (byte) 0x9E, (byte) 0x73, (byte) 0x51 };
	private static final byte[] byte_10008AA0 = { (byte) 0x04, (byte) 0x0D, (byte) 0x0C, (byte) 0x05, (byte) 0x0C, (byte) 0x01, (byte) 0x06,
			(byte) 0x0F, (byte) 0x0C, (byte) 0x0F, (byte) 0x0B, (byte) 0x0C, (byte) 0x01, (byte) 0x0E, (byte) 0x03, (byte) 0x01 };
	private static final byte[] byte_10007A6C = { (byte) 0x0D, (byte) 0x09, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x0F, (byte) 0x0C,
			(byte) 0x09, (byte) 0x06, (byte) 0x04, (byte) 0x01, (byte) 0x0E, (byte) 0x0C, (byte) 0x09, (byte) 0x07, (byte) 0x05 };
	private static final byte[] byte_10007A80 = { (byte) 0x01, (byte) 0x06, (byte) 0x03, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x02,
			(byte) 0x00, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x02, (byte) 0x05, (byte) 0x07, (byte) 0x02, (byte) 0x06 };

	private byte[] decrypt(byte[] allInts, int a5) {

		byte[] result = new byte[allInts.length-16];
		
		a5 = a5 & 0xFF;
		byte v6 = (byte) (a5 & 0xF);
		for (int i = 16; i < allInts.length; i++) {
			int v20 = allInts[i];
			v20 = (v20 & 0xFF);
			byte v19 = a2[v6];
			switch (byte_10007A80[v6]) {
			case 0x0:
				v20 += v19;
				break;
			case 0x1:
				v20 -= v19;
				break;
			case 0x2:
				v20 ^= v19;
				break;
			case 0x3:
				v20 = __ROR1__(v20, 4);
				break;
			case 0x4:
				v20 = __ROL1__(v20, byte_10008AA0[v6]);
				break;
			case 0x5:
				v20 = __ROR1__(v20, byte_10008AA0[v6]);
				break;
			case 0x6:
				v20 = __ROL1__(v20, byte_10007A6C[v6]);
				break;
			case 0x7:
				v20 = __ROR1__(v20, byte_10007A6C[v6]);
				break;
			default:
				break;
			}
			result[i-16] = (byte) ((v20 - a5) & 0xFF);
			++v6;
			++a5;
			if (v6 == 16)
				v6 = 0;
		}
		return result;
	}
	private int __ROL1__(int bytee, int step) {
		if (step > 8)
			step = (step % 8);
		int result = (bytee << step | bytee >> (8 - step));
		result = result & 0xFF;
		return result;
	}

	private int __ROR1__(int bytee, int step) {
		if (step > 8)
			step = (step % 8);
		int result = (bytee >> step | bytee << (8 - step));
		result = result & 0xFF;
		return result;
	}
}
