/**
 * FileHelper.java
 * 文件操作工具包，实现文件读写、新建、复制等操作
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.canace.mybaby.db.model.ImageItem;

import android.util.Log;

public class FileHelper {
	private final static String TAG = "FileHelper";
	private static final int FILE_BUFFER_SIZE = 51200;

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return
	 */
	public static void copySingleFile(String oldPath, String newPath) {

		InputStream inputStream = readFile(oldPath);
		if (inputStream == null) {
			if (CommonsUtil.DEBUG) {
				Log.i(TAG, "复制单个文件操作出错");
			}
		} else {
			writeFile(newPath, inputStream);
		}

	}

	/**
	 * 返回CacheService缓存根目录
	 * 
	 * @return
	 */
	public static String getCacheRootDirectory() {
		return CommonsUtil.getRootFilePath()
				+ "/Android/data/com.canace.mybaby/cache/";
	}
	
	/**
	 * 获取头像缩略图文件地址
	 * @param imageItem
	 * @return
	 */
	public static String getThumbnailPath(ImageItem imageItem){
		String filename = String.valueOf(imageItem.getImagePath().hashCode());
		return getImagesCacheDirectory() + filename;
	}
	
	/**
	 * 获取缓存图片目录
	 * @return
	 */
	public static String getImagesCacheDirectory(){
		return getCacheRootDirectory() + "images/";
	}
	

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isFileExist(File file) {
		if (file == null || !file.exists()) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		if (!isFilePathValid(filePath)) {
			return false;
		}

		File file = new File(filePath);
		return isFileExist(file);
	}

	/**
	 * @param filePath
	 * @return isFilePathValid
	 */
	private static boolean isFilePathValid(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "param invalid, filePath: " + filePath);
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static InputStream readFile(String filePath) {
		if (!isFilePathValid(filePath)) {
			return null;
		}

		InputStream is = null;

		try {
			File f = new File(filePath);
			if (isFileExist(f)) {
				is = new FileInputStream(f);
			} else {
				return null;
			}
		} catch (Exception ex) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "Exception, ex: " + ex.toString());
			}
			return null;
		}
		return is;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean createDirectory(String filePath) {
		if (!isFilePathValid(filePath)) {
			return false;
		}

		File file = new File(filePath);

		if (isFileExist(file)) {
			return true;
		}

		return file.mkdirs();

	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean deleteDirectory(String filePath) {
		if (!isFilePathValid(filePath)) {
			return false;
		}

		File file = new File(filePath);

		if (!isFileExist(file)) {
			return false;
		}

		if (file.isDirectory()) {
			File[] list = file.listFiles();

			for (int i = 0; i < list.length; i++) {
				if (CommonsUtil.DEBUG) {
					Log.e(TAG, "delete filePath: " + list[i].getAbsolutePath());
				}
				if (list[i].isDirectory()) {
					deleteDirectory(list[i].getAbsolutePath());
				} else {
					list[i].delete();
				}
			}
		}

		file.delete();
		return true;
	}

	/**
	 * 写文件操作：如果文件已存在，先删除旧文件，再新建文件并写入新内容
	 * 
	 * @param filePath
	 * @param inputStream
	 * @return
	 */
	public static boolean writeFile(String filePath, InputStream inputStream) {

		if (!isFilePathValid(filePath)) {
			return false;
		}

		try {
			File file = new File(filePath);
			if (isFileExist(file)) {
				deleteDirectory(filePath);
			}

			String pth = filePath.substring(0, filePath.lastIndexOf("/"));
			boolean ret = createDirectory(pth);
			if (!ret) {
				if (CommonsUtil.DEBUG) {
					Log.e(TAG, "createDirectory fail path = " + pth);
				}
				return false;
			}

			boolean ret1 = file.createNewFile();
			if (!ret1) {
				if (CommonsUtil.DEBUG) {
					Log.e(TAG, "createNewFile fail filePath = " + filePath);
				}
				return false;
			}

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int c = inputStream.read(buf);
			while (-1 != c) {
				fileOutputStream.write(buf, 0, c);
				c = inputStream.read(buf);
			}

			fileOutputStream.flush();
			fileOutputStream.close();

			return true;
		} catch (Exception e) {
			if (CommonsUtil.DEBUG) {
				e.printStackTrace();
			}
		}

		return false;

	}

	/**
	 * 写文件操作：默认以覆盖模式写入
	 * 
	 * @param filePath
	 * @param fileContent
	 * @return
	 */
	public static boolean writeFile(String filePath, String fileContent) {
		return writeFile(filePath, fileContent, false);
	}

	/**
	 * 写文件操作，可指定是否以追加文件末尾方式写入
	 * 
	 * @param filePath
	 * @param fileContent
	 * @param append
	 * @return
	 */
	public static boolean writeFile(String filePath, String fileContent,
			boolean append) {
		if (!isFilePathValid(filePath) || !isFileContentValid(fileContent)) {
			return false;
		}

		try {
			File file = new File(filePath);
			if (!isFileExist(file)) {
				if (!file.createNewFile()) {
					return false;
				}
			}

			BufferedWriter output = new BufferedWriter(new FileWriter(file,
					append));
			output.write(fileContent);
			output.flush();
			output.close();
		} catch (IOException ioe) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "writeFile ioe: " + ioe.toString());
			}
			return false;
		}

		return true;
	}

	/**
	 * @param fileContent
	 * @return
	 */
	private static boolean isFileContentValid(String fileContent) {
		if (fileContent == null || fileContent.length() < 0) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "Invalid param.  fileContent: " + fileContent);
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		if (!isFilePathValid(filePath)) {
			return 0;
		}

		File file = new File(filePath);
		if (!isFileExist(file)) {
			return 0;
		}

		return file.length();
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileModifyTime(String filePath) {
		if (!isFilePathValid(filePath)) {
			return 0;
		}

		File file = new File(filePath);
		if (!isFileExist(file)) {
			return 0;
		}

		return file.lastModified();
	}

	/**
	 * 
	 * @param filePath
	 * @param modifyTime
	 * @return
	 */
	public static boolean setFileModifyTime(String filePath, long modifyTime) {
		if (!isFilePathValid(filePath)) {
			return false;
		}

		File file = new File(filePath);
		if (!isFileExist(file)) {
			return false;
		}

		return file.setLastModified(modifyTime);
	}

	/**
	 * 
	 * @param zipFileName
	 * @param crc
	 * @return
	 */
	public static boolean readZipFile(String zipFileName, StringBuffer crc) {
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(
					zipFileName));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				long size = entry.getSize();
				crc.append(entry.getCrc() + ", size: " + size);
			}
			zis.close();
		} catch (Exception ex) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "Exception: " + ex.toString());
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param zipFileName
	 * @return
	 */
	public static byte[] readGZipFile(String zipFileName) {
		if (isFileExist(zipFileName)) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "zipFileName: " + zipFileName);
			}
			try {
				FileInputStream fin = new FileInputStream(zipFileName);
				int size;
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((size = fin.read(buffer, 0, buffer.length)) != -1) {
					baos.write(buffer, 0, size);
				}
				fin.close();
				return baos.toByteArray();
			} catch (Exception ex) {
				// Log.e( TAG, "read zipRecorder file error");
			}
		}
		return null;
	}

	/**
	 * 
	 * @param baseDirName
	 * @param fileName
	 * @param targerFileName
	 * @return
	 * @throws IOException
	 */
	public static boolean zipFile(String baseDirName, String fileName,
			String targerFileName) throws IOException {
		if (baseDirName == null || "".equals(baseDirName)) {
			return false;
		}
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			return false;
		}

		String baseDirPath = baseDir.getAbsolutePath();
		File targerFile = new File(targerFileName);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				targerFile));
		File file = new File(baseDir, fileName);

		boolean zipResult = false;
		if (file.isFile()) {
			zipResult = fileToZip(baseDirPath, file, out);
		} else {
			zipResult = dirToZip(baseDirPath, file, out);
		}
		out.close();
		return zipResult;
	}

	/**
	 * 
	 * @param fileName
	 * @param unZipDir
	 * @return
	 * @throws Exception
	 */
	public static boolean unZipFile(String fileName, String unZipDir)
			throws Exception {
		File f = new File(unZipDir);

		if (!f.exists()) {
			f.mkdirs();
		}

		BufferedInputStream is = null;
		ZipEntry entry;
		ZipFile zipfile = new ZipFile(fileName);
		Enumeration<?> enumeration = zipfile.entries();
		byte data[] = new byte[FILE_BUFFER_SIZE];

		while (enumeration.hasMoreElements()) {
			entry = (ZipEntry) enumeration.nextElement();

			if (entry.isDirectory()) {
				File f1 = new File(unZipDir + "/" + entry.getName());
				if (!f1.exists()) {
					f1.mkdirs();
				}
			} else {
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				String name = unZipDir + "/" + entry.getName();
				RandomAccessFile m_randFile = null;
				File file = new File(name);
				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();
				m_randFile = new RandomAccessFile(file, "rw");
				int begin = 0;

				while ((count = is.read(data, 0, FILE_BUFFER_SIZE)) != -1) {
					try {
						m_randFile.seek(begin);
					} catch (Exception ex) {
						if (CommonsUtil.DEBUG) {
							Log.e(TAG, "exception, ex: " + ex.toString());
						}
					}

					m_randFile.write(data, 0, count);
					begin = begin + count;
				}

				file.delete();
				m_randFile.close();
				is.close();
			}
		}

		return true;
	}

	/**
	 * 
	 * @param baseDirPath
	 * @param file
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private static boolean fileToZip(String baseDirPath, File file,
			ZipOutputStream out) throws IOException {
		FileInputStream in = null;
		ZipEntry entry = null;

		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		int bytes_read;
		try {
			in = new FileInputStream(file);
			entry = new ZipEntry(getEntryName(baseDirPath, file));
			out.putNextEntry(entry);

			while ((bytes_read = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes_read);
			}
			out.closeEntry();
			in.close();
		} catch (IOException e) {
			if (CommonsUtil.DEBUG) {
				Log.e(TAG, "Exception, ex: " + e.toString());
			}
			return false;
		} finally {
			if (out != null) {
				out.closeEntry();
			}

			if (in != null) {
				in.close();
			}
		}
		return true;
	}

	/**
	 * 
	 * @param baseDirPath
	 * @param dir
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private static boolean dirToZip(String baseDirPath, File dir,
			ZipOutputStream out) throws IOException {
		if (!dir.isDirectory()) {
			return false;
		}

		File[] files = dir.listFiles();
		if (files.length == 0) {
			ZipEntry entry = new ZipEntry(getEntryName(baseDirPath, dir));

			try {
				out.putNextEntry(entry);
				out.closeEntry();
			} catch (IOException e) {
				if (CommonsUtil.DEBUG) {
					Log.e(TAG, "Exception, ex: " + e.toString());
				}
			}
		}

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				fileToZip(baseDirPath, files[i], out);
			} else {
				dirToZip(baseDirPath, files[i], out);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param baseDirPath
	 * @param file
	 * @return
	 */
	private static String getEntryName(String baseDirPath, File file) {
		if (!baseDirPath.endsWith(File.separator)) {
			baseDirPath = baseDirPath + File.separator;
		}

		String filePath = file.getAbsolutePath();
		if (file.isDirectory()) {
			filePath = filePath + "/";
		}

		int index = filePath.indexOf(baseDirPath);
		return filePath.substring(index + baseDirPath.length());
	}
}