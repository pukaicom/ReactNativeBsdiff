package pk.com.reactnativebsidffbundle;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pk.bsdiff.utils.PatchUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>使用common.js与对应的业务包 合成 业务bundle</p>
 * Created by pukai on 16/12/6.
 */
public class BsdiffPatchUtil {
    public final static String COMMONJS = "common.js";
    public final static String PACTH_PATH = "patchs";
    public final static String BUSSINESS_PATH = "bussness";
    public final static String patchExtensions = ".patch";
    public final static String bundleExtensions = ".bundle";
    public final static String BASE_SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + RNBsdiffApplication.sContext.getPackageName() + "/rn";
    private String TAG = "BsdiffPatchUtil";

    /**
     * 通过传递的moduleName 从 module.json 文件中找到对应的js文件名称，与common.js合成新的业务bundle;
     * example   familyAddree:10001 -> patch/10001.patch+common.js -> 10001.bundle(RN setJSBundleFile 的文件)
     *
     * @param module
     * @return 成功 返回Bundle文件所在的文件路径
     * 失败 返回""
     */
    private String patch(RNModule module) {
        String modlueName = module.getModuleName();
        String md5 = module.getMd5();
        if (null == containsModule(modlueName)) {
            Log.e(TAG, modlueName + "  not exists!");
        }
        if (!isFileExists(BASE_SD_PATH + File.separator + COMMONJS)) {
            Log.e(TAG, "com" + "  not exists!");
            return "";
        }
        if (!isFileExists(getSDCardPatchFilePath(modlueName))) {
            Log.e(TAG, modlueName + "  not exists!");
            return "";
        }
        File file = new File(BASE_SD_PATH + File.separator + BUSSINESS_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        /**
         * 判断sd卡上是否存在业务的bundle 如果不存在或者 md5 与最新的md5不同则合成新的业务bundle
         *
         */
        if (!isFileExists(getOutputFilePath(modlueName)) || !SignUtils.checkMd5(new File(getOutputFilePath(modlueName)), md5)) {
            if (PatchUtils.patch(BASE_SD_PATH + File.separator + COMMONJS, getOutputFilePath(modlueName), getSDCardPatchFilePath(modlueName)) == 0) {
                /**
                 * 验证合成后的md5 是否与 最新的md5相同 检验合成是否成功
                 */
                if (SignUtils.checkMd5(getOutputFilePath(modlueName), md5)) {
                    return getOutputFilePath(modlueName);
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private boolean patchAll() {
        for (RNModule module : listModules) {
            if (null == patch(module)) {
                return false;
            }
        }
        return true;
    }

    private Runnable copyFileAndPatchAllRunnable = new Runnable() {
        @Override
        public void run() {
            init();
            patchAll();
        }
    };
    private Runnable patchAllRunnable = new Runnable() {
        @Override
        public void run() {
            patchAll();
        }
    };

    private class PatchRunnable implements Runnable {
        private String moduleName;

        public PatchRunnable(String newModuleName) {
            moduleName = newModuleName;
        }

        @Override
        public void run() {
            if (listModules == null) {
                return;
            }
            for (RNModule module : listModules) {
                if (moduleName.equals(module.getModuleName())) {
                    patch(module);
                }
            }
        }
    }

    /**
     * 复制asset下的全部文件并且合成所有的业务bundle
     */
    public void copyFileAndPatchAllBundle() {
        new Thread(copyFileAndPatchAllRunnable).start();
    }

    /**
     * 合成所有的bundle
     */
    public void patchAllBundle() {
        new Thread(patchAllRunnable).start();
    }

    /**
     * 合成指定moduleName的业务bundle
     *
     * @param moduleName
     */
    public void patchBundle(String moduleName) {
        new Thread(new PatchRunnable(moduleName)).start();
    }

    private String getSDCardPatchFilePath(String moduleName) {
        RNModule module = containsModule(moduleName);
        if (null != module) {
            return BASE_SD_PATH + File.separator + PACTH_PATH + File.separator + module.getModuleId() + patchExtensions;
        } else {
            return "";
        }
    }


    public String getOutputFilePath(String moduleName) {
        RNModule module = containsModule(moduleName);
        if (null != module) {
            return BASE_SD_PATH + File.separator + BUSSINESS_PATH + File.separator + module.getModuleId() + bundleExtensions;
        } else {
            return "";
        }
    }

    private RNModule containsModule(String str) {
        for (RNModule modlue : listModules) {
            if (str.equals(modlue.getModuleName()) || str.equals(modlue.getModuleId())) {
                return modlue;
            }
        }
        return null;
    }

    private boolean isFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    private static BsdiffPatchUtil bsdiffPatchUtil;

    public static BsdiffPatchUtil getInstance() {
        if (bsdiffPatchUtil == null) {
            bsdiffPatchUtil = new BsdiffPatchUtil();
        }
        return bsdiffPatchUtil;
    }

    private List<RNModule> listModules = new ArrayList<>();

    private BsdiffPatchUtil() {
    }

    private void init() {
        File file = new File(BASE_SD_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        /**
         *  将assets/rn 文件复制到sd卡 并且读取其中module.text的内容存放到moduleap里
         */
        if (copyFilesFassets(RNBsdiffApplication.sContext, "rn", BASE_SD_PATH)) {
            try {
                AssetManager assetManager = RNBsdiffApplication.sContext.getAssets();
                //File moduleFile = new File(BASE_SD_PATH + File.separator + "module.json");
                //FileInputStream inputStream = new FileInputStream(moduleFile);
                InputStream inputStream = assetManager.open("module.json");
                String content = readTextFromSDcard(inputStream);
                listModules = initModules(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 存文件流中读取内容
     *
     * @param is
     * @return
     * @throws Exception
     */
    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    /**
     * <p>将json字符串转换成 list</p>
     *
     * @param str
     * @return
     */
    private List<RNModule> initModules(String str) {
        List<RNModule> list = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            try {
                Gson gson = new Gson();
                list = gson.fromJson(str, new TypeToken<List<RNModule>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * <p>从assets目录中复制整个文件夹内容</p>
     *
     * @param context Context 使用CopyFiles类的Context
     * @param oldPath String  原文件路径  如：rn
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public boolean copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                /**
                 *  如果文件已经存在 且MD5 相同 跳过复制
                 */
                if (isFileExists(newPath) && SignUtils.getMd5ByFile(context.getAssets().open(oldPath)).equals(SignUtils.getMd5ByFile(new File(newPath)))) {
                    Log.e("file", "exist    " + newPath);
                } else {
                    InputStream is = context.getAssets().open(oldPath);
                    FileOutputStream fos = new FileOutputStream(new File(newPath));
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
