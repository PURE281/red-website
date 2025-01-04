
package com.jbb.library_common.utils.log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON打印机.
 */
public class JsonPrinter implements Printer {

    /** JSON的缩进量. */
    private static final int JSON_INDENT = 4;

    @Override
    public void printConsole(LogLevel level, String tag, String message, StackTraceElement element) {
        String json;
        try {
            if (message.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(message);
                json = jsonObject.toString(JSON_INDENT);
            } else if (message.startsWith("{")) {
                JSONArray jsonArray = new JSONArray(message);
                json = jsonArray.toString(JSON_INDENT);
            } else {
                json = message;
            }
        } catch (JSONException e) {
            json = message;
        }
        PrinterUtils.printConsole(level, tag, PrinterUtils.decorateMsgForConsole(json, element));
    }

    @Override
    public void printFile(LogLevel level, String tag, String message, StackTraceElement element) {
        synchronized(Printer.class) {
            PrinterUtils.printFile(PrinterUtils.decorateMsgForFile(level, message, element));
        }
    }
}