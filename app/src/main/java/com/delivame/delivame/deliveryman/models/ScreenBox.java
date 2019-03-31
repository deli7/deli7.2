package com.delivame.delivame.deliveryman.models;

import android.os.Bundle;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class ScreenBox {
    private String name;
    private Boolean isFavorite;
    private String subtitle;
    private int thumbnail;
    private Object clazz;
    private Bundle params;
    private String iconUrl;

    public ScreenBox() {
    }

    public ScreenBox(String name, String subtitle, int thumbnail, Object clazz, Bundle params) {
        this.name = name;
        this.subtitle = subtitle;
        this.thumbnail = thumbnail;
        this.clazz = clazz;
        this.params = params;
    }

    public ScreenBox(String name, String subtitle, String iconUrl, Object clazz, Bundle params) {
        this.name = name;
        this.subtitle = subtitle;
        this.iconUrl = iconUrl;
        this.clazz = clazz;
        this.params = params;
    }

    public Object getClazz() {
        return clazz;
    }

    public void setClazz(Object clazz) {
        this.clazz = clazz;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
    public boolean isFavorite(){
      return isFavorite;
    }

    public void setFavorite(boolean favorite){
      isFavorite = favorite;
    }

    public Bundle getParams() {
        return params;
    }

    public void setParams(Bundle params) {
        this.params = params;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
