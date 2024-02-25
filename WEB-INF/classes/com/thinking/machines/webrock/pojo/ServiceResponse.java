package com.thinking.machines.webrock.pojo;
public class ServiceResponse implements java.io.Serializable
{
Object result;
Object exception;
boolean isSuccess;
public ServiceResponse()
{
this.result=null;
this.exception=null;
this.isSuccess=false;
}

public void setResult(Object result)
{
this.result=result;
}

public Object getResult()
{
return this.result;
}

public void setException(Object exception)
{
this.exception=exception;
}

public Object getException()
{
return this.exception;
}

public void setIsSuccess(boolean isSuccess)
{
this.isSuccess=isSuccess;
}

public boolean getIsSuccess()
{
return this.isSuccess;
}

}//class ends