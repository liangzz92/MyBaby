package com.canace.mybaby.cache.utils;

import com.canace.mybaby.cache.model.MediaItem;

public abstract class MediaFilter {
    public abstract boolean pass(MediaItem item);
}
