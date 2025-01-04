package com.jbb.library_common.download;

import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;

import java.io.File;

/**
 * 类: PluginDownloadService <p>
 * 描述: 插件下载服务 <p>
 * 作者: nn <p>
 * 时间: 2015年1月30日 下午2:05:17 <p>
 */
public class PluginDownloadService extends DownloadBaseIntentService {
    public PluginDownloadService() {
        super(PluginDownloadService.class.getSimpleName());
    }

    public PluginDownloadService(String name) {
        super(name);
    }

    @Override
    public void success() {
        LogUtil.d("PluginDownloadService download  finished:" + url);
        String fileName = FileUtil.getFileName(url);
        String filePath = FileUtil.getAppCachePath(this) + File.separator + "download/" + fileName;
//        String md5 = SharePreferensUtil.getString(KeyContacts.SP_KEY_APP_PLUGINMD5, KeyContacts.SP_FILE_DEFAULT);
        File file = new File(filePath);
//        if (TextUtils.isEmpty(md5) || CommUtil.getMd5ByFile(file).equalsIgnoreCase(md5)) {
//            SharePreferensUtil.putString(KeyContacts.SP_KEY_APP_PLUGIN, fileName, KeyContacts.SP_FILE_DEFAULT);
//            LogUtil.d("PluginDownloadService download  finished complete");
//        } else {
//            if (file.exists()) {
//                file.delete();
//            }
//            LogUtil.d("PluginDownloadService download  finished but infometion error delete file");
//        }
    }


}
