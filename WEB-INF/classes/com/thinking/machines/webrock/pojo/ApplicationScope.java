package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
public class ApplicationScope implements java.io.Serializable
{
private ServletContext servletContext;
public ApplicationScope(ServletContext servletContext)
{
this.servletContext=servletContext;
}
public void setAttribute(String key,Object value)
{
this.servletContext.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.servletContext.getAttribute(key);
}
}