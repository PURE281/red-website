package com.jbb.library_common.utils.log;

/**
 * 默认打印机.
 */
public class DefaultPrinter implements Printer {

    @Override
    public void printConsole(LogLevel level, String tag, String message, StackTraceElement element) {
        PrinterUtils.printConsole(level, tag, PrinterUtils.decorateMsgForConsole(message, element));
    }

    @Override
    public void printFile(LogLevel level, String tag, String message, StackTraceElement element) {
        synchronized (Printer.class) {
            PrinterUtils.printFile(PrinterUtils.decorateMsgForFile(level, message, element));
        }
    }
}