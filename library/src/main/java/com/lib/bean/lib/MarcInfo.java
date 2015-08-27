package com.lib.bean.lib;

import java.io.Serializable;

public class MarcInfo
  implements Serializable
{
  private String author;
  private String doc_type_name;
  private String isbn;
  private String marc_rec_no;
  private String pub_year;
  private String publisher;
  private String title;

  public String getAuthor()
  {
    return this.author;
  }

  public String getDoc_type_name()
  {
    return this.doc_type_name;
  }

  public String getIsbn()
  {
    return this.isbn;
  }

  public String getMarc_rec_no()
  {
    return this.marc_rec_no;
  }

  public String getPub_year()
  {
    return this.pub_year;
  }

  public String getPublisher()
  {
    return this.publisher;
  }

  public String getTitle()
  {
    return this.title;
  }

  public void setAuthor(String paramString)
  {
    this.author = paramString;
  }

  public void setDoc_type_name(String paramString)
  {
    this.doc_type_name = paramString;
  }

  public void setIsbn(String paramString)
  {
    this.isbn = paramString;
  }

  public void setMarc_rec_no(String paramString)
  {
    this.marc_rec_no = paramString;
  }

  public void setPub_year(String paramString)
  {
    this.pub_year = paramString;
  }

  public void setPublisher(String paramString)
  {
    this.publisher = paramString;
  }

  public void setTitle(String paramString)
  {
    this.title = paramString;
  }
}