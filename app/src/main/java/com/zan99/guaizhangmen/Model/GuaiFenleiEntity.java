package com.zan99.guaizhangmen.Model;

/**
 * Created by 99zan on 2018/1/15.
 */

public class GuaiFenleiEntity {

    private String img;
    private String name;
    private String ficationId;

    public GuaiFenleiEntity() {
    }

    public GuaiFenleiEntity(String img, String name, String ficationId) {
        this.img = img;
        this.name = name;
        this.ficationId = ficationId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFicationId() {
        return ficationId;
    }

    public void setFicationId(String ficationId) {
        this.ficationId = ficationId;
    }
}
