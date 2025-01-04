/*
 * Copyright JiongBull 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jbb.library_common.utils.log;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jbb.library_common.utils.DateFormateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件工具类.
 */
public class LogFileUtils {

    /** 读写文件的线程池，单线程模型. */
    private static ExecutorService sExecutorService;

    static {
        sExecutorService = Executors.newSingleThreadExecutor();
    }

    private LogFileUtils() {
    }

    /**
     * 判断文件或目录是否存在.
     *
     * @param filePath 路径
     * @return true - 存在，false - 不存在
     */
    public static boolean isExist(@NonNull String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 创建目录，若目录已存在则不处理.
     *
     * @param dirPath 目录路径
     * @return true - 目录存在（创建成功或已存在），false - 目录不存在
     */
    public static boolean createDir(@NonNull String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return file.exists();
    }

    /**
     * 把文本写入文件中.
     *
     * @param dirPath    目录路径
     * @param fileName   文件名
     * @param content    待写内容
     * @param isOverride 写入模式，true - 覆盖，false - 追加
     */
    public static void write(@NonNull final String dirPath, @NonNull final String fileName, @NonNull final String content, final boolean isOverride) {
        sExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                String filePath = dirPath + File.separator + fileName;
                FileOutputStream fos = null;
                try {
                    if (createDir(dirPath)) {
                        File file = new File(filePath);
                        boolean isExist = file.exists();
                        fos = new FileOutputStream(file, !(!isExist || isOverride));
                        fos.write(content.getBytes(LogUtil.getSettings().getCharset()));
                    }
                } catch (IOException e) {
                    Log.e("LogFileUtils", e.getMessage());
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            Log.e("LogFileUtils", e.getMessage());
                        }
                    }
                }
            }
        });
    }


    /**
     * 通过全限定类名来获取类名。
     *
     * @param className 全限定类名
     * @return 类名
     */
    public static String getSimpleClassName(@NonNull String className) {
        int lastIndex = className.lastIndexOf(".");
        int index = lastIndex + 1;
        if (lastIndex > 0 && index < className.length()) {
            return className.substring(index);
        }
        return className;
    }

    /**
     * 生成日志目录路径.
     *
     * @return 日志目录路径
     */
    public static String genDirPath() {
        String dir = LogUtil.getSettings().getLogDir();
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir;
    }

    /**
     * 生成日志文件名.
     *
     * @return 日志文件名
     */
    public static String genFileName() {
        String logPrefix = LogUtil.getSettings().getLogPrefix();
        logPrefix = TextUtils.isEmpty(logPrefix) ? "" : logPrefix + "_";
        String curDate = DateFormateUtil.getCurrentDate();
        String fileName = logPrefix + curDate + ".log";
        return fileName;
    }


}