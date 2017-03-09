package pk.com.reactnativebsidffbundle;

import android.app.Activity;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pukai on 2016-10-27.
 */
public class HomeAddressReactModle extends ReactContextBaseJavaModule {
    private Activity context;
    private ReactApplicationContext reactApplicationContext;

    public HomeAddressReactModle(final ReactApplicationContext reactContext, Activity context) {
        super(reactContext);
        this.context = context;
        reactApplicationContext = reactContext;
    }

    @Override
    public String getName() {
        return "NativeMethod";
    }


    /**
     * 在js文件调用该方法的方式 NativeModules.NativeMethod.js2Native
     *
     * @param content 普通类型的参数
     */
    @ReactMethod
    public void showWarnToast(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    public void showSuccessToast(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    public void showLoading() {
    }

    @ReactMethod
    public void hideLoading() {
    }

    /**
     * 可以添加多个方法
     */
    @ReactMethod
    public void getAddressList(final Callback successCallback, final Callback failedCallback) {
        List<HomeAddress> list = new ArrayList<>();
        list.add(new HomeAddress("0", "测试地址0", true));
        list.add(new HomeAddress("1", "测试地址1", false));
        list.add(new HomeAddress("2", "测试地址2", false));
        WritableArray writableArray = new WritableNativeArray();
        for (HomeAddress address : list) {
            if (address != null) {
                writableArray.pushMap(address.getWritableMap());
            }
        }
        successCallback.invoke(writableArray);
        //failedCallback.invoke();

    }


    @ReactMethod
    public void setDefaultAddress(final String addrId, final Callback successCallback, final Callback failedCallback) {
        successCallback.invoke(addrId);
        // failedCallback.invoke();
    }
}
