package com.lib.bean.lib;

public class ResultBean
{
  public String msg;
  public String success;

  public ResultBean()
  {
  }

  public ResultBean(String paramString1, String paramString2)
  {
    this.success = paramString1;
    this.msg = paramString2;
  }

  public String getMsg()
  {
    return this.msg;
  }

  public String getSuccess()
  {
    return this.success;
  }

  public boolean isSuccess()
  {
    return "1".equals(this.success);
  }

  public void setMsg(String paramString)
  {
    this.msg = paramString;
  }

  public void setSuccess(String paramString)
  {
    this.success = paramString;
  }
}