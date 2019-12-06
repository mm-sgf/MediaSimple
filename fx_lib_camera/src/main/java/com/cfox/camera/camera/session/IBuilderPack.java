package com.cfox.camera.camera.session;

import android.hardware.camera2.CaptureRequest;

import com.cfox.camera.utils.EsRequest;

public interface IBuilderPack {
    void clear();

    void configBuilder(EsRequest request);
    void repeatingRequestBuilder(EsRequest request , CaptureRequest.Builder builder);
    void previewBuilder(CaptureRequest.Builder builder);
    void preCaptureBuilder(CaptureRequest.Builder builder);
    void captureBuilder(CaptureRequest.Builder builder);
    void previewCaptureBuilder(CaptureRequest.Builder builder);


}
