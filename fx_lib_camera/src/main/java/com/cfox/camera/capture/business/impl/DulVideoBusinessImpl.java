package com.cfox.camera.capture.business.impl;

import android.util.Size;

import com.cfox.camera.IConfigWrapper;
import com.cfox.camera.capture.business.BaseBusiness;

public class DulVideoBusinessImpl extends BaseBusiness {
    public DulVideoBusinessImpl(IConfigWrapper configWrapper) {
        super(configWrapper);
    }

    @Override
    public Size getPreviewSize(Size size, Size[] supportSizes) {
        return getConfig().getPhotoPreviewSize(size, supportSizes);
    }
}