///**
// * 工程: Coyote <p>
// * 标题: ScreenShotUtil.java <p>
// * 包:   com.niuniucaip.lotto.util.other <p>
// * 描述: TODO <p>
// * 作者: nn <p>
// * 时间: 2015年1月22日 上午10:25:23 <p>
// * 版权: Copyright 2015 Shenzhen NiuNiucaip Tech Co.,Ltd.
// * All rights reserved.
// */
//
//package com.jbb.library_common.utils;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.net.Uri;
//import android.os.Environment;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.view.View;
//import android.webkit.WebView;
//import android.widget.ScrollView;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 类: ScreenShotUtil <p>
// * 描述: 屏幕截图 <p>
// * 作者: nn <p>
// * 时间: 2015年1月22日 上午10:25:23 <p>
// */
//public class ScreenShotUtil {
//    private Activity activity;
//
//
//    /**
//     * 方法: saveSharePic <p>
//     * 描述: 保存图片<p>
//     * 参数: bitmap <p>
//     * 返回: void <p>
//     * 异常  <p>
//     * 作者: nn <p>
//     * 时间: 2015年1月22日 下午4:25:43
//     */
//    public void saveSharePic(Bitmap bitmap) {
//        File file;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            file = new File(Environment.getExternalStorageDirectory().getPath() + "/nn");
//        } else {
//            file = new File(activity.getFilesDir().getAbsolutePath());
//        }
//
//        FileOutputStream fos = null;
//        try {
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            file = new File(file.getPath() + "/nnshare.png");
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//
//            fos = new FileOutputStream(file);
//            if (null != fos) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.flush();
//                fos.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    /***
//     * 生成普通二维码
//     * @param str
//     * @param width
//     * @param height
//     * @return
//     */
//    public static Bitmap createQRCode(String str, int width, int height, String colorStr) {
//        try {
//
//            //解决中文乱码问题
//            String encodeContent = new String(str.getBytes("UTF-8"), "ISO-8859-1");
//
//            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
//            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//            hints.put(EncodeHintType.MARGIN, 0);
//
//            BitMatrix matrix = new QRCodeWriter().encode(encodeContent, BarcodeFormat.QR_CODE, width, height, hints);
//            matrix = deleteWhite(matrix);//删除白边
//            width = matrix.getWidth();
//            height = matrix.getHeight();
//            int[] pixels = new int[width * height];
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    if (matrix.get(x, y)) {
//                        pixels[y * width + x] = Color.BLACK;
//                    } else {
//                        pixels[y * width + x] = Color.WHITE;
//                    }
//                }
//            }
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//            bitmap.setPixels(pixels ,0, width, 0, 0, width, height);
//            return addBorder(bitmap,colorStr);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    /**
//     *  给二维码添加边框
//     * @param qrBitmap
//     * @param colorStr  "#ffffff"
//     * @return
//     */
//    private static Bitmap addBorder(Bitmap qrBitmap, String colorStr) {
//        if (qrBitmap == null)
//            return null;
//        if(TextUtils.isEmpty(colorStr))
//            colorStr = "#ffffff";
//
//        //获取图片的宽高
//        int srcWidth = qrBitmap.getWidth();
//        int srcHeight = qrBitmap.getHeight();
//
//        if (srcWidth == 0 || srcHeight == 0) {
//            return null;
//        }
//
//        int strokeWidth = 20;//边框宽度
//        Bitmap bitmap = null;
//        Canvas canvas = null;
//        try {
//            //白色边框
//            Paint paint = new Paint();
//            Rect rect = null;
//            //空bitmap
//            bitmap = Bitmap.createBitmap(srcWidth + 2 * strokeWidth, srcHeight + 2 * strokeWidth, Config.ARGB_8888);
//            canvas = new Canvas(bitmap);
//            //白色边框
//            paint.setColor(Color.parseColor(colorStr));
//            paint.setStyle(Paint.Style.FILL);
//            rect = new Rect(0, 0, srcWidth + 2 * strokeWidth, srcHeight + 2 * strokeWidth);
//            canvas.drawRect(rect, paint);
//            //二维码
//            canvas.drawBitmap(qrBitmap, strokeWidth, strokeWidth, null);
//
//            canvas.save(Canvas.ALL_SAVE_FLAG);
//            canvas.restore();
//        } catch (Exception e) {
//            bitmap = qrBitmap;
//        }
//        return bitmap;
//    }
//
//
//
//
//
//    private static BitMatrix deleteWhite(BitMatrix matrix) {
//        int[] rec = matrix.getEnclosingRectangle();
//        int resWidth = rec[2] + 1;
//        int resHeight = rec[3] + 1;
//
//        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
//        resMatrix.clear();
//        for (int i = 0; i < resWidth; i++) {
//            for (int j = 0; j < resHeight; j++) {
//                if (matrix.get(i + rec[0], j + rec[1]))
//                    resMatrix.set(i, j);
//            }
//        }
//        return resMatrix;
//    }
//
//
//    /**
//     * 生成带Logo的二维码图片
//     *
//     * @param content         二维码内容
//     * @param widthPix        二维码图片宽度px
//     * @param blackCodeBorder 二维码边框颜色是否为黑色
//     * @param logoResId       logo图片资源Id
//     * @return
//     */
//    public Bitmap createQRCodeLogo(String content, int widthPix, boolean blackCodeBorder, int logoResId) throws Exception {
//        if (content == null || content.equals("")) {
//            return null;
//        }
//
//        //解决中文乱码问题
//        String encodeContent = new String(content.getBytes("UTF-8"), "ISO-8859-1");
//        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
//
//
//        //添加logo图片需设置为高容错率
//        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
//        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//        hints.put(EncodeHintType.MARGIN, 0);
//
//        BitMatrix matrix = new QRCodeWriter().encode(encodeContent, BarcodeFormat.QR_CODE, widthPix, widthPix, hints);
//        matrix = deleteWhite(matrix);//删除白边
//        int width = matrix.getWidth();
//        int height = matrix.getHeight();
//        int[] pixels = new int[width * height];
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = Color.BLACK;
//                } else {
//                    pixels[y * width + x] = Color.WHITE;
//                }
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//
//        return addLogo(bitmap, logoResId, blackCodeBorder);
//    }
//
//    /**
//     * 给二维码图片添加Logo
//     *
//     * @param qrBitmap
//     * @param logoResId
//     * @return
//     */
//    private Bitmap addLogo(Bitmap qrBitmap, int logoResId, boolean blackCodeBorder) {
//        if (qrBitmap == null)
//            return null;
//        Bitmap logo = BitmapFactory.decodeResource(activity.getResources(), logoResId);
//        //获取图片的宽高
//        int srcWidth = qrBitmap.getWidth();
//        int srcHeight = qrBitmap.getHeight();
//        int logoWidth = logo.getWidth();
//        int logoHeight = logo.getHeight();
//
//        if (srcWidth == 0 || srcHeight == 0) {
//            return null;
//        }
//
//        if (logoWidth == 0 || logoHeight == 0) {
//            return qrBitmap;
//        }
//
//        //logo大小为二维码整体大小的1/5
//        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
//
//        int strokeWidth = 4;//边框宽度
//        Bitmap bitmap = null;
//        Canvas canvas = null;
//        try {
//
//
//            //白色边框
//            Paint paint = new Paint();
//            Rect rect = null;
//            if (blackCodeBorder) {
//                bitmap = Bitmap.createBitmap(srcWidth + 4 * strokeWidth, srcHeight + 4 * strokeWidth, Config.ARGB_8888);
//                canvas = new Canvas(bitmap);
//
//                //黑色边框
//                paint.setColor(Color.parseColor("#000000"));
//                paint.setStrokeWidth(strokeWidth * 2);
//                paint.setStyle(Paint.Style.STROKE);
//                rect = new Rect(0, 0, srcWidth + 4 * strokeWidth, srcHeight + 4 * strokeWidth);
//                canvas.drawRect(rect, paint);
//                //二维码
//                canvas.drawBitmap(qrBitmap, strokeWidth * 2, strokeWidth * 2, null);
//                //logo
//                canvas.scale(scaleFactor, scaleFactor, (srcWidth + 4 * strokeWidth) / 2, (srcHeight + 4 * strokeWidth) / 2);
//                canvas.drawBitmap(logo, (4 * strokeWidth + srcWidth - logoWidth) / 2, (4 * strokeWidth + srcHeight - logoHeight) / 2, null);
//            } else {
//                bitmap = Bitmap.createBitmap(srcWidth + 2 * strokeWidth, srcHeight + 2 * strokeWidth, Config.ARGB_8888);
//                canvas = new Canvas(bitmap);
//                //白色边框
//                paint.setColor(Color.parseColor("#ffffff"));
//                paint.setStyle(Paint.Style.FILL);
//                rect = new Rect(0, 0, srcWidth + 2 * strokeWidth, srcHeight + 2 * strokeWidth);
//                canvas.drawRect(rect, paint);
//                //二维码
//                canvas.drawBitmap(qrBitmap, strokeWidth, strokeWidth, null);
//
//                //logo
//                canvas.scale(scaleFactor, scaleFactor, (srcWidth + 2 * strokeWidth) / 2, (srcHeight + 2 * strokeWidth) / 2);
//                canvas.drawBitmap(logo, (2 * strokeWidth + srcWidth - logoWidth) / 2, (2 * strokeWidth + srcHeight - logoHeight) / 2, null);
//            }
//
//            canvas.save(Canvas.ALL_SAVE_FLAG);
//            canvas.restore();
//        } catch (Exception e) {
//            bitmap = qrBitmap;
//        }
//        return bitmap;
//    }
//
//}
