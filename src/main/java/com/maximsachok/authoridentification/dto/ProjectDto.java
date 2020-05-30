package com.maximsachok.authoridentification.dto;


import javax.validation.constraints.NotBlank;

public class ProjectDto{
   private Long id;
   @NotBlank
   private String nameEn;
   @NotBlank
   private String descEn;
   @NotBlank
   private String keywords;



   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

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

   public String asString() {
      return nameEn+" "+keywords+" "+descEn;
   }
}
