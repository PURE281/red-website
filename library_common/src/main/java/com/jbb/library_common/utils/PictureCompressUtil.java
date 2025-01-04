/**
 * 工程: Coyote<p>
 * 标题: PictureCompressUtil.java<p>
 * 包:   com.niuniucaip.lotto.mainact.socialsquare.personspace.other<p>
 * 描述: TODO<p>
 * 作者: nn<p>
 * 时间: 2015年4月21日 下午5:41:22<p>
 * 版权: Copyright 2015 Shenzhen NiuNiucaip Tech Co.,Ltd.<p>
 * All rights reserved.<p>
 */

package com.jbb.library_common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.jbb.library_common.utils.log.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 类: PictureCompressUtil<p>
 * 描述: 图片压缩<p>
 * 作者: nn<p>
 * 时间: 2015年4月21日 下午5:41:22<p><p>
 */
public class PictureCompressUtil {

    /**
     * 图片压缩质量
     **/
    private final static int compressQuality = 30;

    /**
     * 方法: getBitMap <p>
     * 描述: TODO<p>
     * 参数: @param context
     * 参数: @param filePath
     * 参数: @return<p>
     * 返回: Bitmap<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年5月4日 下午5:52:49<p>
     */
    public static Bitmap getBitMap(String filePath) {
        Options options = new Options();
        int inSamppleSize = calculateInSampleSize(options, filePath);
        options.inSampleSize = inSamppleSize >= 3 ? inSamppleSize * 2 : 2;
        LogUtil.e("options.inSampleSize:" + options.inSampleSize + "   inSamppleSize:" + inSamppleSize);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inInputShareable = true;
        options.inPurgeable = true;
        Bitmap iconBitMap = null;
        try {
            try {
                iconBitMap = BitmapFactory.decodeStream(new FileInputStream(filePath), null, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            iconBitMap = correctionPicDegree(filePath, iconBitMap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return iconBitMap;
    }

    /**
     * 方法: compressImage <p>
     * 描述: 压缩图片  返回压缩后的图片地址<p>
     * 参数: @return<p>
     * 返回: String<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年4月21日 下午5:50:24<p>
     */
    public static String compressImage(String filePath) {
        Options options = new Options();
        int inSamppleSize = calculateInSampleSize(options, filePath);
        options.inSampleSize = inSamppleSize >= 3 ? ((int) (inSamppleSize * 1.5)) : inSamppleSize;
        LogUtil.e(" compressImage  options.inSampleSize:" + options.inSampleSize);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        bitmap = correctionPicDegree(filePath, bitmap);
        File outputFile = CommUtil.createTempFile();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            out.write(compressImage(bitmap).toByteArray());
            return outputFile.getPath();
        } catch (Exception e) {
            LogUtil.e("exception compress pic");
            e.printStackTrace();
        } finally {
            if (options != null) {
                options = null;
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
            bitmap.recycle();
            bitmap = null;
        }
        return null;
    }

    /**
     * 方法: calculateInSampleSize <p>
     * 描述: 计算缩放比例<p>
     * 参数: @return<p>
     * 返回: int<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年4月21日 下午6:17:12<p>
     */
    private static int calculateInSampleSize(Options options, String filePath) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        LogUtil.e("choose pic width:" + width + "    height:" + height);
        int inSampleSize = 1;
        int reqHeight = 800;
        int reqWidth = 480;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * 方法: correctionPicDegree <p>
     * 描述: 调整图片角度<p>
     * 参数: @param filePath
     * 参数: @param bitmap
     * 参数: @return<p>
     * 返回: Bitmap<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年4月21日 下午6:07:32<p>
     */
    private static Bitmap correctionPicDegree(String filePath, Bitmap bitmap) {
        int degree = readPictureDegree(filePath);
        if (degree != 0) {//旋转照片角度
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return bitmap;
            }
        }
        return bitmap;
    }

    /**
     * 方法: readPictureDegree <p>
     * 描述: 读取图片的角度<p>
     * 参数: @param filePath
     * 参数: @return<p>
     * 返回: int<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年4月21日 下午6:04:14<p>
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 方法: compressImage <p>
     * 描述: 压缩图片<p>
     * 参数: @param image
     * 参数: @return<p>
     * 返回: Bitmap<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年5月5日 下午8:38:44<p>
     */
    public static ByteArrayOutputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            if (options <= 0) {
                compressImage(BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null));
            }
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        LogUtil.e("compressImage", "options:" + options);
        //		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        //		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return baos;
    }

}
