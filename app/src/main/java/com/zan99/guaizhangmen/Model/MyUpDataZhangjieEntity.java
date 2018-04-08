package com.zan99.guaizhangmen.Model;

/**
 * Created by 99zan on 2018/1/15.
 */

public class MyUpDataZhangjieEntity {

    private String chapterName;
    private String duration;

    public MyUpDataZhangjieEntity() {
    }

    public MyUpDataZhangjieEntity(String chapterName, String duration) {
        this.chapterName = chapterName;
        this.duration = duration;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
