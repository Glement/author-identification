package com.maximsachok.authoridentification.dto;


import javax.validation.constraints.NotBlank;

public class ProjectDto{
   @NotBlank
   private String nameEn;
   @NotBlank
   private String descEn;
   @NotBlank
   private String keywords;

   public String getNameEn() {
      return nameEn;
   }

   public void setNameEn(String nameEn) {
      this.nameEn = nameEn;
   }

   public String getDescEn() {
      return descEn;
   }

   public void setDescEn(String descEn) {
      this.descEn = descEn;
   }

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }
}
