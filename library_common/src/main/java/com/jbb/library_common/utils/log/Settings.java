package com.jbb.library_common.utils.log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置.
 */
public class Settings {

    /**
     * DEBUG模式.
     */
    private boolean mIsDebug;
    /**
     * 字符集.
     */
    private String mCharset;
    /**
     * 时间格式.
     */
    private String mTimeFormat;

    /**
     * 日志保存的目录.
     */
    private String mLogDir;
    /**
     * 日志文件的前缀.
     */
    private String mLogPrefix;

    /**
     * 日志是否记录到文件中.
     */
    private boolean mWriteToFile;
    /**
     * 写入文件的日志级别.
     */
    private List<LogLevel> mLogLevelsForFile;
    /**
     * 封装的层级，V、D、I、W、E、WTF、JSON共用，请确保他们封装在同一层级中.
     */
    private int mPackagedLevel;

    public Settings() {
        mIsDebug = true;
        mCharset = "UTF-8";
        mTimeFormat = "yyyy-MM-dd HH:mm:ss";
        mLogDir = "500";
        mLogPrefix = "";
        mWriteToFile = false;
        mLogLevelsForFile = new ArrayList<LogLevel>();
        mLogLevelsForFile.add(LogLevel.ERROR);
        mLogLevelsForFile.add(LogLevel.WTF);
        mPackagedLevel = 0;
    }

    public String getCharset() {
        return mCharset;
    }

    public Settings setCharset(@NonNull String charset) {
        mCharset = charset;
        return this;
    }

    public String getTimeFormat() {
        return mTimeFormat;
    }

    public Settings setTimeFormat(@NonNull String timeFormat) {
        mTimeFormat = timeFormat;
        return this;
    }

    public String getLogDir() {
        return mLogDir;
    }

    public Settings setLogDir(@NonNull String logDir) {
        mLogDir = logDir;
        return this;
    }

    public String getLogPrefix() {
        return mLogPrefix;
    }

    public Settings setLogPrefix(@NonNull String logPrefix) {
        mLogPrefix = logPrefix;
        return this;
    }

    public boolean isWriteToFile() {
        return mWriteToFile;
    }

    public Settings writeToFile(boolean isWriteToFile) {
        mWriteToFile = isWriteToFile;
        return this;
    }

    public List<LogLevel> getLogLevelsForFile() {
        return mLogLevelsForFile;
    }

    public Settings setLogLevelsForFile(@NonNull List<LogLevel> logLevelsForFile) {
        mLogLevelsForFile = logLevelsForFile;
        return this;
    }

    public boolean isDebug() {
        return mIsDebug;
    }

    public Settings setDebug(boolean isDebug) {
        mIsDebug = isDebug;
        return this;
    }

    public int getPackagedLevel() {
        return mPackagedLevel;
    }

    public Settings setPackagedLevel(int packagedLevel) {
        mPackagedLevel = packagedLevel;
        return this;
    }
}