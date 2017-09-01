package util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author bigG
 * @date 2017年4月28日 下午1:37:45 
 */
public class IO {

	public static byte[] readAllByte(InputStream inStream) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(inStream);
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
				bos.close();
			} catch (IOException e) {
				in = null;
				bos = null;
			}
		}
	}
	
	public static byte[] readAllByte(File file) throws IOException {
		if (file == null || !file.exists()) {
			throw new FileNotFoundException();
		}
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		return readAllByte(in);
	}
	
	public static void writeByte2File(File file, byte[] bytes) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				fos = null;
			}
		}
	}

}
