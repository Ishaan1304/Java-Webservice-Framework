package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
import java.util.*;

public class Service implements java.io.Serializable
{
private String path;
private Class serviceClass;
private Method serviceMethod;
private boolean isGetAllowed;
private boolean isPostAllowed;
private String forwardTo;
private boolean runOnStart;
private int priority;
private boolean injectApplicationScope;
private boolean injectSessionScope;
private boolean injectRequestScope;
private boolean injectApplicationDirectory;
private List<AutoWiredService> autoWiredList;
private List<RequestParameterService> requestParameterList;
private boolean isSecured;
private String checkPost;
private String guard;
public Service()
{
this.path=null;
this.serviceClass=null;
this.serviceMethod=null;
this.isGetAllowed=false;
this.isPostAllowed=false;
this.forwardTo=null;
this.priority=-1;
this.runOnStart=false;
this.injectApplicationScope=false;
this.injectSessionScope=false;
this.injectRequestScope=false;
this.injectApplicationDirectory=false;
this.autoWiredList=null;
this.requestParameterList=null;
}

public void setPath(String path)
{
this.path=path;
}

public String getPath()
{
return this.path;
}

public void setServiceClass(Class serviceClass)
{
this.serviceClass=serviceClass;
}

public Class getServiceClass()
{
return this.serviceClass;
}

public void setServiceMethod(Method serviceMethod)
{
this.serviceMethod=serviceMethod;
}

public Method getServiceMethod()
{
return this.serviceMethod;
}

public void setIsGetAllowed(boolean isGetAllowed)
{
this.isGetAllowed=isGetAllowed;
}

public boolean getIsGetAllowed()
{
return this.isGetAllowed;
}

public void setIsPostAllowed(boolean isPostAllowed)
{
this.isPostAllowed=isPostAllowed;
}

public boolean getIsPostAllowed()
{
return this.isPostAllowed;
}

public void setForwardTo(String forwardTo)
{
this.forwardTo=forwardTo;
}

public String getForwardTo()
{
return this.forwardTo;
}

public void setPriority(int priority)
{
this.priority=priority;
}

public int getPriority()
{
return this.priority;
}

public void setRunOnStart(boolean runOnStart)
{
this.runOnStart=runOnStart;
}

public boolean getRunOnStart()
{
return this.runOnStart;
}



public void setInjectApplicationScope(boolean injectApplicationScope)
{
this.injectApplicationScope=injectApplicationScope;
}

public boolean getInjectApplicationScope()
{
return this.injectApplicationScope;
}

public void setInjectSessionScope(boolean injectSessionScope)
{
this.injectSessionScope=injectSessionScope;
}

public boolean getInjectSessionScope()
{
return this.injectSessionScope;
}

public void setInjectRequestScope(boolean injectRequestScope)
{
this.injectRequestScope=injectRequestScope;
}

public boolean getInjectRequestScope()
{
return this.injectRequestScope;
}

public void setInjectApplicationDirectory(boolean injectApplicationDirectory)
{
this.injectApplicationDirectory=injectApplicationDirectory;
}

public boolean getInjectApplicationDirectory()
{
return this.injectApplicationDirectory;
}

public void setAutoWiredList(List<AutoWiredService> autoWiredList)
{
this.autoWiredList=autoWiredList;
}

public List<AutoWiredService> getAutoWiredList()
{
return this.autoWiredList;
}

public void setRequestParameterList(LinkedList<RequestParameterService> requestParameterList)
{
this.requestParameterList=requestParameterList;
}

public List<RequestParameterService> getRequestParameterList()
{
return this.requestParameterList;
}

public void setIsSecured(boolean isSecured)
{
this.isSecured=isSecured;
}

public boolean getIsSecured()
{
return this.isSecured;
}

public void setCheckPost(String checkPost)
{
this.checkPost=checkPost;
}

public String getCheckPost()
{
return this.checkPost;
}

public void setGuard(String guard)
{
this.guard=guard;
}

public String getGuard()
{
return this.guard;
}

}//class ends