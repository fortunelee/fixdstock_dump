package com.orient.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPUtils  {
    public static final String GZIP_ENCODE_UTF_8 = "UTF-8"; 
    //public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
            byte[] ret = out.toByteArray();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public static byte[] compress(String str) throws IOException {  
        return compress(str, GZIP_ENCODE_UTF_8);  
    }
    
    public static byte[] uncompress(byte[] bytes, int bufferSize) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            
            byte[] ret = out.toByteArray();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } 
    }
    
    public static String uncompressToString(byte[] bytes, String encoding, int bufferSize) {  
        if (bytes == null || bytes.length == 0) {  
            return null;  
        }  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);  
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);  
            byte[] buffer = new byte[bufferSize];  
            int n;  
            while ((n = ungzip.read(buffer)) >= 0) {  
                out.write(buffer, 0, n);  
            }  
            return out.toString(encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return null;
    }
    
    public static String uncompressToString(byte[] bytes) {  
        return uncompressToString(bytes, GZIP_ENCODE_UTF_8, 1024 * 8);  
    } 
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////test//////////////////////////////////////////////
//    public static void main(String[] args) throws IOException {
//        String s = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//        System.out.println("字符串长度："+s.length());
//        String compressedStr = new String(compress(s), "UTF-8");
//        System.out.println("压缩后：："+ compressedStr.length() + ":" + compressedStr);
//        System.out.println("解压后："+uncompress(compress(s), 1024).length);
//        System.out.println("解压字符串后：："+uncompressToString(compress(s)).length());
//    }
    
    public static void main(String[] args) {
    	String json = "{\"BOND\":[\"cd12\",\"ce13\",\"cf14\",\"cg15\"]}";
    	
    	try {
			byte[] bytes = GZIPUtils.compress(json);
			
			byte[] old = GZIPUtils.uncompress(bytes, 8 * 1024);
			System.out.println(new String(old, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
	}
}