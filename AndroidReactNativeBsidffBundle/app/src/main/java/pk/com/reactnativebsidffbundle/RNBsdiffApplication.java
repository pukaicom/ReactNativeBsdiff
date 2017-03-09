package pk.com.reactnativebsidffbundle;

import android.app.Application;
import android.content.Context;

/**
 * Created by pukai on 17/3/9.
 */
public class RNBsdiffApplication extends Application {
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        BsdiffPatchUtil.getInstance().copyFileAndPatchAllBundle();
    }
}
