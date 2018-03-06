package com.zinc.libimage.model;

import java.io.Serializable;

/**
 *
 * @date 创建时间：2017/12/5
 * @author Jiang zinc
 * @description 本地多媒体资源实体
 *
 */

public class LocalMedia implements Serializable {
    private String path;
    private long duration;
    private long lastUpdateAt;

    public LocalMedia(String path, long lastUpdateAt, long duration) {
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
    }

    public LocalMedia(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
