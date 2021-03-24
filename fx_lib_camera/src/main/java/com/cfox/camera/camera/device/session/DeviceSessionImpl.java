package com.cfox.camera.camera.device.session;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.cfox.camera.EsException;
import com.cfox.camera.camera.info.CameraInfoHelper;
import com.cfox.camera.camera.info.CameraInfo;
import com.cfox.camera.camera.device.EsCameraDevice;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.surface.SurfaceManager;
import com.cfox.camera.surface.SurfaceProvider;
import com.cfox.camera.utils.EsError;
import com.cfox.camera.utils.Es;
import com.cfox.camera.utils.EsParams;
import com.cfox.camera.utils.WorkerHandlerManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class DeviceSessionImpl implements DeviceSession {
    private final EsCameraDevice mEsCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private String mCameraId;

    DeviceSessionImpl(EsCameraDevice cameraDevice) {
        mEsCameraDevice = cameraDevice;
    }

    @Override
    public Observable<EsParams> onOpenCamera(final EsParams esParams) {
        // TODO: 19-12-1 check camera id
        mCameraId = esParams.getString(Es.Key.CAMERA_ID);
        EsLog.d("onOpenCamera.. camera id:" + mCameraId);
        return mEsCameraDevice.openCameraDevice(esParams).map(new Function<EsParams, EsParams>() {
            @Override
            public EsParams apply(@NonNull EsParams esParams) {
                mCameraDevice = (CameraDevice) esParams.getObj(Es.Key.CAMERA_DEVICE);
                return esParams;
            }
        });
    }

    @Override
    public CaptureRequest.Builder onCreateRequestBuilder(int templateType) throws CameraAccessException {
        return mCameraDevice.createCaptureRequest(templateType);
    }

    public Observable<EsParams> onCreateCaptureSession(final EsParams esParams) {
        final SurfaceManager surfaceManager = (SurfaceManager) esParams.getObj(Es.Key.SURFACE_MANAGER);
        // TODO: 19-11-29 check  mCaptureSession is null
        return Observable.create(new ObservableOnSubscribe<EsParams>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<EsParams> emitter) throws Exception {
                // 设置更多返回表面
                mCameraDevice.createCaptureSession(surfaceManager.getTotalSurface(), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        EsLog.d("onConfigured: create session success ....." + esParams);
                        mCaptureSession = session;
                        emitter.onNext(esParams);
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        emitter.onError(new EsException("Create Preview Session failed  ", EsError.ERROR_CODE_CREATE_PREVIEW_SESSION));

                    }
                }, WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_CAMERA));
            }
        });
    }

    @Override
    public Observable<EsParams> onRepeatingRequest(EsParams esParams) {
        final CaptureRequest.Builder requestBuilder = (CaptureRequest.Builder) esParams.getObj(Es.Key.REQUEST_BUILDER);
        final CameraCaptureSession.CaptureCallback captureCallback  = (CameraCaptureSession.CaptureCallback) esParams.getObj(Es.Key.SESSION_CALLBACK);
        return Observable.create(new ObservableOnSubscribe<EsParams>() {
            @Override
            public void subscribe(ObservableEmitter<EsParams> emitter) throws Exception {
                mCaptureSession.setRepeatingRequest(requestBuilder.build(), captureCallback,
                        WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_CAMERA));
            }
        });
    }

    @Override
    public Observable<EsParams> onClose() {
        EsLog.d("onClose: closeSession:  start " + mCameraId);
        return mEsCameraDevice.closeCameraDevice(mCameraId).doOnNext(new Consumer<EsParams>() {
            @Override
            public void accept(EsParams esParams) throws Exception {
                EsLog.d("onClose: closeSession: .......id:" + mCameraId);
                if (mCaptureSession != null) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
            }
        });
    }

    @Override
    public void capture(EsParams esParams) throws CameraAccessException {
        EsLog.d("capture ==>" + esParams);
        CaptureRequest.Builder requestBuilder = (CaptureRequest.Builder) esParams.getObj(Es.Key.REQUEST_BUILDER);
        CameraCaptureSession.CaptureCallback captureCallback = (CameraCaptureSession.CaptureCallback) esParams.getObj(Es.Key.CAPTURE_CALLBACK);
        mCaptureSession.capture(requestBuilder.build(), captureCallback, WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_CAMERA));
    }

    @Override
    public void stopRepeating() throws CameraAccessException {
        mCaptureSession.stopRepeating();
        mCaptureSession.abortCaptures();
    }
}
