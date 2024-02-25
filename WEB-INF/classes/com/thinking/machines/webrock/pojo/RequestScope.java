package com.thinking.machines.webrock.pojo;
import javax.servlet.http.*;
public class RequestScope implements java.io.Serializable
{
private HttpServletRequest httpServletRequest;
public RequestScope(HttpServletRequest httpServletRequest)
{
this.httpServletRequest=httpServletRequest;
}
public void setAttribute(String key,Object value)
{
this.httpServletRequest.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.httpServletRequest.getAttribute(key);
}
}