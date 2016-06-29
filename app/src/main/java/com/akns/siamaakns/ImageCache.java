package com.akns.siamaakns;

import android.graphics.Bitmap;

/**
 * Created by Affan Mohammad on 27/06/2016.
 */
public interface ImageCache {
    public Bitmap getBitmap(String key);
    public void putBitmap(String key, Bitmap value);
}
