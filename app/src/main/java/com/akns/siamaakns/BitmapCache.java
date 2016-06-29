package com.akns.siamaakns;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Affan Mohammad on 27/06/2016.
 */
public class BitmapCache extends LruCache<String, Bitmap> implements ImageCache {

    private static BitmapCache bitmapCache;
    private static final int DEF_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;


    public BitmapCache() {
        this(DEF_CACHE_SIZE);
    }

    public BitmapCache(int maxSize) {
        super(maxSize);
    }

    public static BitmapCache getBitmapCache() {
        if (bitmapCache == null) bitmapCache = new BitmapCache();
        return bitmapCache;
    }

    @Override
    public Bitmap getBitmap(String key) {
        return get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap value) {
        put(key, value);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value == null ? 0 : value.getRowBytes() * value.getHeight() / 1024;
    }
}