package util;

/*
 * author: 		Chendy 
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Properties;

public final class UnionUtil {

	/*
	 * 16进制的字符串转换成压缩BCD码
	 */
	public static final boolean HexStr2CBCD(byte[] in, byte[] out, int len) {
		byte[] asciiCode = { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

		// System.out.println("len==="+len);
		// System.out.println("len1==="+in.length);
		if (len > in.length) {
			return false;
		}

		if (len % 2 != 0) {
			return false;
		}

		byte[] temp = new byte[len];

		for (int i = 0; i < len; i++) {
			if (in[i] >= 0x30 && in[i] <= 0x39)
				temp[i] = (byte) (in[i] - 0x30);
			else if (in[i] >= 0x41 && in[i] <= 0x46)
				temp[i] = asciiCode[in[i] - 0x41];
			else if (in[i] >= 0x61 && in[i] <= 0x66)
				temp[i] = asciiCode[in[i] - 0x61];
			else
				return false;
		}

		for (int i = 0; i < len / 2; i++) {
			out[i] = (byte) (temp[2 * i] * 16 + temp[2 * i + 1]);
		}

		return true;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param len
	 * @return
	 */
	public static final boolean CBCD2HexStr(byte[] in, byte[] out, int len) {
		byte[] asciiCode = { 0x41, 0x42, 0x43, 0x44, 0x45, 0x46 };

		// System.out.println("len="+len);
		// System.out.println(new String(in));
		if (len > in.length) {
			return false;
		}

		byte[] temp = new byte[2 * len];

		for (int i = 0; i < len; i++) {
			temp[2 * i] = (byte) ((in[i] & 0xf0) / 16);
			temp[2 * i + 1] = (byte) (in[i] & 0x0f);
		}

		for (int i = 0; i < 2 * len; i++) {
			if (temp[i] <= 9 && temp[i] >= 0) {
				out[i] = (byte) (temp[i] + 0x30);
			} else {
				out[i] = asciiCode[temp[i] - 0x0a];
			}
		}

		return true;
	}

	public static final String LeftAddZero(String s, int TotalLen) {
		String sTemp = "";
		int l = s.length();
		if (l >= TotalLen)
			return s;
		else {
			int j = TotalLen - l;

			for (int i = 0; i < j; i++) {
				sTemp += "0";
			}
			sTemp += s;
		}
		return sTemp;
	}

	/*
	 * 获取环境变量
	 */
	public static final Properties getEnvVars() throws IOException {
		Process process = null;
		Properties envVars = new Properties();
		Runtime runtime = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("windows 9") > -1) {
			process = runtime.exec("command.com /c set");
		} else if (OS.indexOf("nt") > -1 || OS.indexOf("windows 2000") > -1
				|| OS.indexOf("windows xp") > -1) {
			process = runtime.exec("cmd.exe /c set");
		} else if (OS.indexOf("unix") > -1 || OS.indexOf("linux") > -1) {
			process = runtime.exec("/bin/env");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int idx = line.indexOf('=');
			String key = line.substring(0, idx);
			String value = line.substring(idx + 1);
			envVars.setProperty(key.toUpperCase(), value);
		}
		process.destroy();
		return envVars;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static final void printHexString(String hint, byte[] b) {
		System.out.print(hint);

		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
	}

	/**
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static final String Bytes2HexStringOld(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}
	
	public static final String Bytes2HexString(byte[] b) {
		StringBuffer ret = new StringBuffer();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			hex.delete(0,hex.length());
			hex.append(Integer.toHexString(b[i] & 0xFF));
			if (hex.length() == 1) {
				hex.insert(0,'0');
			}
			ret.append(hex.toString().toUpperCase());
		}
		return ret.toString();
	}
	
	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static final byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static final byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = null;
			try {
				tmp=src.getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static final String HexString2BytesStr(String str) {
		String retStr = null;
		byte[] byteStr = UnionUtil.HexString2Bytes(str);
		try {
			retStr = new String(byteStr, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retStr;
	}

	public static final byte[] BytesCopy(byte[] bytes, int start, int len)
			throws Exception {
		if (bytes == null || bytes.length < start + len)
			throw new Exception("BytesSub bytes为空，或长度不够！");
		byte[] subBytes = new byte[len];
		System.arraycopy(bytes, start, subBytes, 0, len);
		return subBytes;
	}

	public static final byte[] BytesCopy(byte[] bytes, int start)
			throws Exception {
		if (bytes == null || bytes.length < start)
			throw new Exception("BytesSub bytes为空，或长度不够！");

		byte[] subBytes = new byte[bytes.length - start];

		System.arraycopy(bytes, start, subBytes, 0, bytes.length - start);
//System.out.println(subBytes);
		return subBytes;
	}

	public static final byte[] AllRightZreoTo8Multiple(byte[] bytes)
			throws Exception {
		if (bytes.length % 8 == 0)
			return bytes;

		int len = bytes.length + 8 - bytes.length % 8;

		byte[] newbytes = new byte[len];

		for (int i = 0; i < len; i++)
			newbytes[i] = 0;

		System.arraycopy(bytes, 0, newbytes, 0, bytes.length);
		return newbytes;
	}

	public static final String AllRightZreoTo16Multiple(String str)
			throws Exception {
		if ((str.length() % 16 == 0) && (str.length() / 16 > 0))
			return str;

		int len = 16 - str.length() % 16;

		for (int i = 0; i < len; i++)
			str = str + '0';

		return str;
	}

	/**
	 * 二行制转字符串
	 */

	public static final String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	/**
	 * 字符串转二行制
	 */
	public static byte[] hex2byte(String hex) throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] arr = hex.toCharArray();
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			String swap = "" + arr[i++] + arr[i];
			int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = new Integer(byteint).byteValue();
		}
		return b;
	}

	public static final char abcd_to_asc(byte abyte) {
		char buf;
		if (abyte <= 9)
			buf = (char) (abyte + '0');
		else
			buf = (char) (abyte + 'a' - 10);
		return (buf);
	}

	public static final String BcdToAsc(byte[] bcdstr) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bcdstr.length; i++) {
			byte b = (byte) ((bcdstr[i] & 0xf0) >> 4);
			char c = abcd_to_asc(b);
			sb.append(c);

			b = (byte) (bcdstr[i] & 0x0f);
			c = abcd_to_asc(b);
			sb.append(c);
		}
		return sb.toString();
	}

	public static final byte[] AscToBcd(String source) {

		if (source == null)
			return null;
		int len = source.length();
		len = len / 2;
		byte[] dest = new byte[len];

		for (int i = 0; i < len; i++) {
			char c1 = source.charAt(i * 2);
			char c2 = source.charAt(i * 2 + 1);
			byte b1, b2;
			if ((c1 >= '0') && (c1 <= '9'))
				b1 = (byte) (c1 - '0');
			else if ((c1 >= 'a') && (c1 <= 'z'))
				b1 = (byte) (c1 - 'a' + 0x0a);
			else
				b1 = (byte) (c1 - 'A' + 0x0a);

			if ((c2 >= '0') && (c2 <= '9'))
				b2 = (byte) (c2 - '0');
			else if ((c2 >= 'a') && (c2 <= 'z'))
				b2 = (byte) (c2 - 'a' + 0x0a);
			else
				b2 = (byte) (c2 - 'A' + 0x0a);

			dest[i] = (byte) ((b1 << 4) | b2);
		}
		return dest;
	}

	public static final byte[] arraycat(byte[] buf1, byte[] buf2) {
		byte[] bufret = null;

		int len1 = 0;
		int len2 = 0;

		if (buf1 != null)
			len1 = buf1.length;
		if (buf2 != null)
			len2 = buf2.length;

		if (len1 + len2 > 0)
			bufret = new byte[len1 + len2];
		if (len1 > 0)
			System.arraycopy(buf1, 0, bufret, 0, len1);
		if (len2 > 0)
			System.arraycopy(buf2, 0, bufret, len1, len2);
		return bufret;
	}

	// 单字节异或
	public static byte XorOper(byte buf1, byte buf2) {
		byte ch = 0x00;
		ch = (byte) (buf1 ^ buf2);
		return ch;
	}

	// 将两组len数的前len字节异或
	public static byte[] XorOper(byte[] buf1, byte[] buf2, int len) {
		byte[] ch = null;
		int i = 0;
		if (buf1 == null || buf2 == null)
			return null;
		if (buf1.length != buf2.length || len < buf1.length)
			return null;
		ch = new byte[len];
		for (i = 0; i < len; i++) {
			ch[i] = XorOper(buf1[i], buf2[i]);
		}
		return ch;
	}

	// 将两组扩展的hexAsc异或
	public static String XorOperAscHex(String buf1, String buf2) {
		if (buf1 == null || buf2 == null)
			return null;
		byte[] a1 = hex2byte(buf1);
		byte[] a2 = hex2byte(buf2);
		byte[] a3 = XorOper(a1, a2, a1.length);
		return byte2hex(a3);
	}

	private static int keyXorFun(byte[] buf) {
		int i = 0;
		for (i = 0; i < buf.length; i++) {
			buf[i] = keybitXorFun(buf[i]);
		}
		return 0;
	}

	private static byte keybitXorFun(byte buf) {
		int i = 0;
		byte ch = 0x00;
		byte val = 0x00;
		ch = (byte) (buf & (byte) 0xFE);

		for (i = 7; i > 0; i--) {
			val = (byte) (val ^ (0x01 & (ch >> i)));
		}
		val = (byte) (val ^ 1);

		val = (byte) (ch | val);

		return val;
	}

	// 密钥奇校验
	public static String gentRandKeyOdd(String str) {
		byte[] bt = UnionUtil.hex2byte(str);
		UnionUtil.keyXorFun(bt);
		String odd = UnionUtil.byte2hex(bt);
		return odd;
	}

	// PKCS 5补位
	public static byte [] FiltPKCS5(byte[] buf) {
		int len = 0;
		if (buf == null)
			return null;

		len = 8 - buf.length % 8;
		byte[] newbuf = new byte[buf.length + len];
		System.arraycopy(buf, 0, newbuf, 0, buf.length);
		
		byte bt = (byte) (len & 0xff);

		for (int i = 0; i < len; i++)
			newbuf[buf.length + i] = bt;
		
		return newbuf;
	}
	
	public static byte [] UnFiltPKCS5(byte[] buf) {
		if (buf == null)
			return null;
		
		if (buf.length % 8 != 0)
			return null;
		
		int	n = buf[buf.length - 1];
		if ((n <= 0) || (n > 8))
			return null;
		
		byte bt = buf[buf.length - 1];
		for (int i = buf.length - 1; i >= buf.length - n; i--) {
			if (buf[i] != bt)
				return null;
		}
			
		byte [] newbyte = new byte[buf.length - n];
		System.arraycopy(buf, 0, newbyte, 0, buf.length - n);
		return newbyte;
	}
	
	public static byte[] fillBigInter(BigInteger bigInt, int strLen) {
		String hexStr = bigInt.toString(16);
		if (hexStr.length() == strLen){
			return hex2byte(hexStr);
		}else if (hexStr.length() < strLen){
			hexStr = LeftAddZero(hexStr, strLen);
			return hex2byte(hexStr);
		}else{
			throw new IllegalArgumentException("Param `bigInt` is too Long than `strLen`");
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		String	str = "ahfasfjkafk123000000000lasdfjlalfaslfjfj测试数ahfasfjkafk123000000000lasdfjlalfaslfjfj测试数据!@#!据!@#!";
//		String	str1 = UnionUtil.Bytes2HexStringOld(str.getBytes("ISO-8859-1"));
//		String	str2 = UnionUtil.Bytes2HexString(str.getBytes("ISO-8859-1"));
//		
//		//System.out.println("str1=" + str1);
//		//System.out.println("str2=" + str2);
//		
//		long lstart = System.currentTimeMillis();
//		for (int i=0; i<10000; i++)
//		{
//			str1 = UnionUtil.Bytes2HexStringOld(str.getBytes("ISO-8859-1"));
//			//str2 = UnionUtil.Bytes2HexString(str.getBytes("ISO-8859-1"));
//		}
//		long lend = System.currentTimeMillis();
//		
//		//System.out.println("time is " + (lend - lstart));
//	}
}
