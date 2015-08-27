package com.lib.bean.lib;

public class BookSearchBean extends ResultBean
{
  private Book[] books;
  private int count;

  public Book[] getBooks()
  {
    return this.books;
  }

  public int getCount()
  {
    return this.count;
  }

  public void setBooks(Book[] paramArrayOfBook)
  {
    this.books = paramArrayOfBook;
  }

  public void setCount(int paramInt)
  {
    this.count = paramInt;
  }
}