package com.jbb.library_common.utils.log;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jbb.library_common.utils.DateFormateUtil;


/**
 * 打印机工具类.
 */
public class PrinterUtils {
    /**
     * logcat里日志的最大长度.
     */
    private static final int MAX_LOG_LENGTH = 4000;

    /**
     * 控制台打印的内容格式.
     */
    private static final String PRINT_CONSOLE_FORMAT = "[(%1$s:%2$d)#%3$s]" + Printer.LINE_SEPARATOR + "%4$s";
    /**
     * 文件中保存的内容格式.
     */
    private static final String PRINT_FILE_FORMAT = "[%1$s %2$s %3$s:%4$d]" + Printer.LINE_SEPARATOR + "%5$s"
            + Printer.LINE_SEPARATOR + Printer.LINE_SEPARATOR;

    private PrinterUtils() {
    }

    /**
     * 日志打印输出到控制台.
     *
     * @param level   级别
     * @param tag     标签
     * @param message 信息
     */
    public static void printConsole(@NonNull LogLevel level, @NonNull String tag, @NonNull String message) {
        log(level, tag, message);
    }

    /**
     * 日志打印输出到文件.
     *
     * @param message 信息
     */
    public static void printFile(@NonNull String message) {
        String dirPath = LogFileUtils.genDirPath();
        String fileName = LogFileUtils.genFileName();

        LogFileUtils.write(dirPath, fileName, message, false);
    }

    /**
     * 装饰打印到控制台的信息.
     *
     * @param message 信息
     * @param element 对战元素
     * @return 装饰后的信息
     */
    public static String decorateMsgForConsole(String message, @NonNull StackTraceElement element) {
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        String fileName = element.getFileName();
        return String.format(PRINT_CONSOLE_FORMAT, fileName, lineNumber, methodName, message);
    }

    /**
     * 装饰打印到文件的信息.
     *
     * @param level   级别
     * @param message 信息
     * @param element 堆栈元素
     * @return 装饰后的信息
     */
    public static String decorateMsgForFile(
            @NonNull LogLevel level, String message, @NonNull StackTraceElement element) {
        String time = DateFormateUtil.getCurrentTime();
        String fileName = element.getFileName();
        int lineNum = element.getLineNumber();
        return String.format(PRINT_FILE_FORMAT, time, level.getValue(), fileName, lineNum, message);
    }

    /**
     * 获取系统换行符.
     *
     * @return 系统换行符
     */
    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 使用LogCat输出日志，字符长度超过4000则自动换行.
     *
     * @param level   级别
     * @param tag     标签
     * @param message 信息
     */
    public static void log(@NonNull LogLevel level, @NonNull String tag, @NonNull String message) {
        int subNum = message.length() / MAX_LOG_LENGTH;
        if (subNum > 0) {
            int index = 0;
            for (int i = 0; i < subNum; i++) {
                int lastIndex = index + MAX_LOG_LENGTH;
                String sub = message.substring(index, lastIndex);
                logSub(level, tag, sub);
                index = lastIndex;
            }
            logSub(level, tag, message.substring(index, message.length()));
        } else {
            logSub(level, tag, message);
        }
    }

    /**
     * 使用LogCat输出日志.
     *
     * @param level 级别
     * @param tag   标签
     * @param sub   信息
     */
    private static void logSub(@NonNull LogLevel level, @NonNull String tag, @NonNull String sub) {
        switch (level) {
            case VERBOSE:
                Log.v(tag, sub);
                break;
            case DEBUG:
                Log.d(tag, sub);
                break;
            case INFO:
                Log.i(tag, sub);
                break;
            case WARN:
                Log.w(tag, sub);
                break;
            case ERROR:
                Log.e(tag, sub);
                break;
            case WTF:
                Log.wtf(tag, sub);
                break;
            case JSON:
                Log.w(tag, sub);
            default:
                break;
        }
    }
}