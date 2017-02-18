package com.three38inc.apps.shellsapp;

import java.io.Serializable;

/**
 * Created by jobith on 19/01/17.
 */

public class FeedItem{
    private String name;
    private String nickName;
    private String thumbnail;
    private String caption;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) { this.nickName = nickName;}

    public String getCaption(){ return caption; }

    public void setCaption(String caption){ this.caption = caption; }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
