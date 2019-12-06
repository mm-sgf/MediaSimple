package debug;

import com.cfox.camera.EsCamera;
import com.cfox.lib_common.base.BaseApplication;

public class CameraApplication extends BaseApplication {
    private static final String TAG = "CameraApplication";

    EsCamera mFxCamera;
    @Override
    public void onCreate() {
        super.onCreate();
        mFxCamera = EsCamera.init(this);
//        mFxCamera.setConfig(new IConfig() {
//            @Override
//            public Size getPreviewSize(Size size, Size[] supportSizes) {
//                Log.d(TAG, "getPreviewSize: ......"+ size);
//                return size;
//            }
//
//            @Override
//            public Size getPictureSize(Size size, Size[] supportSizes) {
//                Log.d(TAG, "getPictureSize: ........." + size);
//                return size;
//            }
//        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mFxCamera.release();
    }
}
