package com.delivame.delivame.deliveryman.models;

public class BusinessType {

   private String title_ar;
   private String title_en;
   private String subtitle_ar;
   private String subtitle_en;
   private String icon;
   private String place_name;

   public BusinessType() {
   }

   public String getTitle_ar() {
      return title_ar;
   }

   public void setTitle_ar(String title_ar) {
      this.title_ar = title_ar;
   }

   public String getTitle_en() {
      return title_en;
   }

   public void setTitle_en(String title_en) {
      this.title_en = title_en;
   }

   public String getSubtitle_ar() {
      return subtitle_ar;
   }

   public void setSubtitle_ar(String subtitle_ar) {
      this.subtitle_ar = subtitle_ar;
   }

   public String getSubtitle_en() {
      return subtitle_en;
   }

   public void setSubtitle_en(String subtitle_en) {
      this.subtitle_en = subtitle_en;
   }

   public String getIcon() {
      return icon;
   }

   public void setIcon(String icon) {
      this.icon = icon;
   }

   public String getPlace_name() {
      return place_name;
   }

   public void setPlace_name(String place_name) {
      this.place_name = place_name;
   }
}
