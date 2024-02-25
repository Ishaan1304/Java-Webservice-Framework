package com.thinking.machines.webrock.pojo;
import java.io.*;
import java.lang.reflect.*;
public class RequestParameterService implements java.io.Serializable
{
private String name;
private Class parameterType;
private boolean isApplicationScope;
private boolean isSessionScope;
private boolean isApplicationDirectory;
private boolean isRequestScope;
private boolean isJson;
private boolean isPrimitive;

public RequestParameterService()
{
this.name=null;
this.parameterType=null;
this.isApplicationScope=false;
this.isSessionScope=false;
this.isApplicationDirectory=false;
this.isRequestScope=false;
this.isJson=false;
this.isPrimitive=false;
}

public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}

public void setParameterType(Class parameterType)
{
this.parameterType=parameterType;
}
public Class getParameterType()
{
return this.parameterType;
}

public void setIsApplicationScope(boolean isApplicationScope)
{
this.isApplicationScope=isApplicationScope;
}
public boolean getIsApplicationScope()
{
return this.isApplicationScope;
}

public void setIsSessionScope(boolean isSessionScope)
{
this.isSessionScope=isSessionScope;
}
public boolean getIsSessionScope()
{
return this.isSessionScope;
}

public void setIsApplicationDirectory(boolean isApplicationDirectory)
{
this.isApplicationDirectory=isApplicationDirectory;
}
public boolean getIsApplicationDirectory()
{
return this.isApplicationDirectory;
}

public void setIsRequestScope(boolean isRequestScope)
{
this.isRequestScope=isRequestScope;
}
public boolean getIsRequestScope()
{
return this.isRequestScope;
}

public void setIsJson(boolean isJson)
{
this.isJson=isJson;
}
public boolean getIsJson()
{
return this.isJson;
}

public void setIsPrimitive(boolean isPrimitive)
{
this.isPrimitive=isPrimitive;
}
public boolean getIsPrimitive()
{
return this.isPrimitive;
}


}//class ends
