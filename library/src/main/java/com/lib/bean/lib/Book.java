package com.lib.bean.lib;

public class Book
  implements MarcRecNoProvider
{
  private String author;
  private String call_no;
  private String doc_type_name;
  private boolean isInMyLove;
  private String isbn;
  private int lend_num;
  private String marc_rec_no;
  private int num;
  private String pub_year;
  private String publisher;
  private String title;
  private int total_num;

  public String getAuthor()
  {
    return this.author;
  }

  public String getCall_no()
  {
    return this.call_no;
  }

  public String getDoc_type_name()
  {
    return this.doc_type_name;
  }

  public String getIsbn()
  {
    return this.isbn;
  }

  public int getLend_num()
  {
    return this.lend_num;
  }

  public String getMarcRecNo()
  {
    return this.marc_rec_no;
  }

  public String getMarc_rec_no()
  {
    return this.marc_rec_no;
  }

  public int getNum()
  {
    return this.num;
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

  public int getTotal_num()
  {
    return this.total_num;
  }

  public boolean isInMyLove()
  {
    return this.isInMyLove;
  }

  public void setAuthor(String paramString)
  {
    this.author = paramString;
  }

  public void setCall_no(String paramString)
  {
    this.call_no = paramString;
  }

  public void setDoc_type_name(String paramString)
  {
    this.doc_type_name = paramString;
  }

  public void setInMyLove(boolean paramBoolean)
  {
    this.isInMyLove = paramBoolean;
  }

  public void setIsbn(String paramString)
  {
    this.isbn = paramString;
  }

  public void setLend_num(int paramInt)
  {
    this.lend_num = paramInt;
  }

  public void setMarc_rec_no(String paramString)
  {
    this.marc_rec_no = paramString;
  }

  public void setNum(int paramInt)
  {
    this.num = paramInt;
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

  public void setTotal_num(int paramInt)
  {
    this.total_num = paramInt;
  }

  public MarcInfo toMarcInfo()
  {
    MarcInfo localMarcInfo = new MarcInfo();
    localMarcInfo.setAuthor(this.author);
    localMarcInfo.setMarc_rec_no(this.marc_rec_no);
    localMarcInfo.setDoc_type_name(this.doc_type_name);
    localMarcInfo.setIsbn(this.isbn);
    localMarcInfo.setPub_year(this.pub_year);
    localMarcInfo.setPublisher(this.publisher);
    localMarcInfo.setTitle(this.title);
    return localMarcInfo;
  }
}