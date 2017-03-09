package pk.com.reactnativebsidffbundle;

import com.facebook.react.ReactPackage;

import javax.annotation.Nullable;

public class MainActivity extends BaseReactActivity {


    public static final String JS_MAIN_MODULE_NAME = "FamilyAddressModule";
    public static final String JS_MAIN_BUNDLE_NAME = "familyAddress.android";
    public static final String JS_BUNDLE_LOCAL_FILE = "familyAddress.android.bundle";


    @Override
    protected String getJsModuleName() {
        return JS_MAIN_MODULE_NAME;
    }

    @Override
    protected ReactPackage getPackages() {
        return new HomeAddressReactPackage(this);
    }

    @Override
    protected String getMainBundleName() {
        return JS_MAIN_BUNDLE_NAME;
    }

    @Nullable
    @Override
    protected String getJSBundleFile() {
        return BsdiffPatchUtil.getInstance().getOutputFilePath("familyAddress");
    }

    @Nullable
    @Override
    protected String getBundleAssetName() {
        return JS_BUNDLE_LOCAL_FILE;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
