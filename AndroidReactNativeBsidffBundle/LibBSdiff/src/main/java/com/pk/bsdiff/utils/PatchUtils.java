package com.pk.bsdiff.utils;

/**
 * 类说明： 	APK Patch工具类
 * <p/>
 * Created by pukai on 16/12/6.
 */
public class PatchUtils {

    /**
     * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
     * <p/>
     * 返回：0，说明操作成功
     *
     * @param oldPath 示例:/sdcard/old.apk
     * @param newPath 示例:/sdcard/new.apk
     * @param patchPath  示例:/sdcard/xx.patch
     * @return
     */
    public static native int patch(String oldPath, String newPath,
                                   String patchPath);

    static {
        System.loadLibrary("ApkPatchLibrary");
    }
}