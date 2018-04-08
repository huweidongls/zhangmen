package com.zan99.guaizhangmen.Model;

/**
 * Created by 99zan on 2018/1/19.
 */

public class MyUpdataMediaPlayerEntity {

    String member_id;
    String bookId;
    String chaptreName;
    String chapterImg;
    String createTime;
    String chapterId;
    String duration;
    String zhangjiePath;

    public MyUpdataMediaPlayerEntity() {
    }

    public MyUpdataMediaPlayerEntity(String member_id, String bookId, String chaptreName, String chapterImg, String createTime, String chapterId, String duration, String zhangjiePath) {
        this.member_id = member_id;
        this.bookId = bookId;
        this.chaptreName = chaptreName;
        this.chapterImg = chapterImg;
        this.createTime = createTime;
        this.chapterId = chapterId;
        this.duration = duration;
        this.zhangjiePath = zhangjiePath;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getChaptreName() {
        return chaptreName;
    }

    public void setChaptreName(String chaptreName) {
        this.chaptreName = chaptreName;
    }

    public String getChapterImg() {
        return chapterImg;
    }

    public void setChapterImg(String chapterImg) {
        this.chapterImg = chapterImg;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getZhangjiePath() {
        return zhangjiePath;
    }

    public void setZhangjiePath(String zhangjiePath) {
        this.zhangjiePath = zhangjiePath;
    }
}
