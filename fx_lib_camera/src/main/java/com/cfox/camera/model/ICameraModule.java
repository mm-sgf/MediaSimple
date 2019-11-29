package com.cfox.camera.model;

import android.util.Range;

import com.cfox.camera.utils.FxRequest;
import com.cfox.camera.utils.FxResult;

import io.reactivex.Observable;

public interface ICameraModule {

    Observable<FxResult> startPreview(FxRequest request);

    void initModule(CameraModule.ModuleFlag moduleFlag);
    Observable<FxResult> sendCameraConfig(FxRequest request);
    Observable<FxResult> capture(FxRequest request);
    Observable<FxResult> stop();
    Range<Integer> getEvRange();
}
