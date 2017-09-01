package util;


/**
 * @author bigG
 * @date 2017年6月7日 下午4:01:29
 */
public class MyClassLoader extends ClassLoader {

	public Class<?> loadClass(String name, byte[] b, int off, int len) throws ClassNotFoundException {
		return super.defineClass(name, b, off, len);
	}
}