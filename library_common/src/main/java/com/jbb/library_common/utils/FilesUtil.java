package com.jbb.library_common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FilesUtil {
    public FilesUtil() {
    }

    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        return !file.isFile() ? null : readFile(file, charsetName);
    }

    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder sb = new StringBuilder("");
        BufferedReader reader = null;

        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);

            String line;
            for (reader = new BufferedReader(is); (line = reader.readLine()) != null; sb.append(line)) {
                if (!sb.toString().equals("")) {
                    sb.append("\r\n");
                }
            }

            reader.close();
            StringBuilder var6 = sb;
            return var6;
        } catch (IOException var15) {
            throw new RuntimeException("IOException occurred. ", var15);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }
    }

    public static boolean writeFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        } else {
            FileWriter fileWriter = null;

            boolean var4;
            try {
                fileWriter = new FileWriter(filePath, append);
                fileWriter.write(content);
                var4 = true;
            } catch (IOException var13) {
                throw new RuntimeException("IOException occurred. ", var13);
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException var12) {
                        var12.printStackTrace();
                    }
                }

            }

            return var4;
        }
    }

    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    public static boolean writeFile(File file, InputStream stream, boolean append) {
        FileOutputStream o = null;

        try {
            o = new FileOutputStream(file, append);
            byte[] data = new byte[1024];

            int length;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }

            o.flush();
            boolean var6 = true;
            return var6;
        } catch (FileNotFoundException var16) {
            throw new RuntimeException("FileNotFoundException occurred. ", var16);
        } catch (IOException var17) {
            throw new RuntimeException("IOException occurred. ", var17);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }
    }

    public static void moveFile(String sourceFilePath, String destFilePath) {
        if (!TextUtils.isEmpty(sourceFilePath) && !TextUtils.isEmpty(destFilePath)) {
            moveFile(new File(sourceFilePath), new File(destFilePath));
        } else {
            throw new RuntimeException("Both sourceFilePath and destFilePath cannot be null.");
        }
    }

    public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }

    }

    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException var4) {
            throw new RuntimeException("FileNotFoundException occurred. ", var4);
        }

        return writeFile((String) destFilePath, (InputStream) inputStream);
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            } else if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }

            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }

        return new FileOutputStream(file, append);
    }

    public static void cleanDirectory(File directory) throws IOException {
        String message;
        if (!directory.exists()) {
            message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        } else if (!directory.isDirectory()) {
            message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        } else {
            File[] files = directory.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                File[] var3 = files;
                int var4 = files.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    File file = var3[var5];

                    try {
                        forceDelete(file);
                    } catch (IOException var8) {
                        exception = var8;
                    }
                }

                if (null != exception) {
                    throw exception;
                }
            }
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            cleanDirectory(directory);
            if (!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }

    }

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;

        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            FileChannel in = fi.getChannel();
            FileChannel out = fo.getChannel();
            in.transferTo(0L, in.size(), out);
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            try {
                if (fo != null) {
                    fo.close();
                }

                if (fi != null) {
                    fi.close();
                }
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

    }

    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024L) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576L) {
            fileSizeString = df.format((double) fileLen / 1024.0D) + "K";
        } else if (fileLen < 1073741824L) {
            fileSizeString = df.format((double) fileLen / 1048576.0D) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1.073741824E9D) + "G";
        }

        return fileSizeString;
    }

    public static boolean deleteFile(File file) throws IOException {
        return file != null && file.delete();
    }

    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
        }

        return filename;
    }

    public static String getFileOutputString(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }

            bufferedReader.close();
            return sb.toString();
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static void closeIO(Closeable... closeables) {
        if (null != closeables && closeables.length > 0) {
            Closeable[] var1 = closeables;
            int var2 = closeables.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                Closeable cb = var1[var3];

                try {
                    if (null != cb) {
                        cb.close();
                    }
                } catch (IOException var6) {
                    var6.printStackTrace();
                }
            }

        }
    }

    public static boolean deleteFile(String filename) {
        return (new File(filename)).delete();
    }

    public static boolean isFileExist(String filePath) {
        return (new File(filePath)).exists();
    }

    public static void copyFileFast(FileInputStream is, FileOutputStream os) throws IOException {
        FileChannel in = is.getChannel();
        FileChannel out = os.getChannel();
        in.transferTo(0L, in.size(), out);
    }

    public static void shareFile(Context context, String title, String filePath) {
        Intent intent = new Intent("android.intent.action.SEND");
        Uri uri = Uri.parse("file://" + filePath);
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.STREAM", uri);
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void zip(InputStream is, OutputStream os) {
        GZIPOutputStream gzip = null;

        try {
            gzip = new GZIPOutputStream(os);
            byte[] buf = new byte[1024];

            int len;
            while ((len = is.read(buf)) != -1) {
                gzip.write(buf, 0, len);
                gzip.flush();
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        } finally {
            closeIO(is);
            closeIO(gzip);
        }

    }

    public static void unzip(InputStream is, OutputStream os) {
        GZIPInputStream gzip = null;

        try {
            gzip = new GZIPInputStream(is);
            byte[] buf = new byte[1024];

            int len;
            while ((len = gzip.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        } finally {
            closeIO(gzip);
            closeIO(os);
        }

    }

    public static String formatFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static void Stream2File(InputStream is, String fileName) {
        byte[] b = new byte[1024];
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(new File(fileName));

            int len;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException var9) {
            var9.printStackTrace();
        } finally {
            closeIO(is);
            closeIO(os);
        }

    }

    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName != null && folderName.length() != 0 && folderName.trim().length() != 0) {
            File folder = new File(folderName);
            if (folder.exists()) {
                if (recreate) {
                    deleteFile(folderName);
                    return folder.mkdirs();
                } else {
                    return true;
                }
            } else {
                return folder.mkdirs();
            }
        } else {
            return false;
        }
    }

    public static String getFolderName(String filePath) {
        if (filePath != null && filePath.length() != 0 && filePath.trim().length() != 0) {
            int filePos = filePath.lastIndexOf(File.separator);
            return filePos == -1 ? "" : filePath.substring(0, filePos);
        } else {
            return filePath;
        }
    }

    public static boolean deleteFiles(String folder) {
        if (folder != null && folder.length() != 0 && folder.trim().length() != 0) {
            File file = new File(folder);
            if (!file.exists()) {
                return true;
            } else if (file.isFile()) {
                return file.delete();
            } else if (!file.isDirectory()) {
                return false;
            } else {
                File[] var2 = file.listFiles();
                int var3 = var2.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    File f = var2[var4];
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteFile(f.getAbsolutePath());
                    }
                }

                return file.delete();
            }
        } else {
            return true;
        }
    }

    @SuppressLint("WrongConstant")
    public static void openImage(Context mContext, String imagePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(imagePath));
        intent.setDataAndType(uri, "image/*");
        mContext.startActivity(intent);
    }

    @SuppressLint("WrongConstant")
    public static void openVideo(Context mContext, String videoPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(67108864);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(videoPath));
        intent.setDataAndType(uri, "video/*");
        mContext.startActivity(intent);
    }

    public static void openURL(Context mContext, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        mContext.startActivity(intent);
    }
}
