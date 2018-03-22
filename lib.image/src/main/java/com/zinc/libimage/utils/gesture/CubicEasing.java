package com.zinc.libimage.utils.gesture;

public final class CubicEasing {

    /**
     * 规定以慢速结束的过渡效果
     * @param time
     * @param start
     * @param end
     * @param duration
     * @return
     */
    public static float easeOut(float time, float start, float end, float duration) {
        return end * ((time = time / duration - 1.0f) * time * time + 1.0f) + start;
    }

    /**
     * 规定以慢速开始的过渡效果
     * @param time
     * @param start
     * @param end
     * @param duration
     * @return
     */
    public static float easeIn(float time, float start, float end, float duration) {
        return end * (time /= duration) * time * time + start;
    }

    /**
     * 以慢速开始和结束的过渡效果
     * @param time
     * @param start
     * @param end
     * @param duration
     * @return
     */
    public static float easeInOut(float time, float start, float end, float duration) {
        return (time /= duration / 2.0f) < 1.0f ? end / 2.0f * time * time * time + start : end / 2.0f * ((time -= 2.0f) * time * time + 2.0f) + start;
    }

}
