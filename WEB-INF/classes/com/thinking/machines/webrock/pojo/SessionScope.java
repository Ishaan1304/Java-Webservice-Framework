package com.thinking.machines.webrock.pojo;
import javax.servlet.http.*;
public class SessionScope implements java.io.Serializable
{
private HttpSession httpSession;

public SessionScope(HttpSession httpSession)
{
this.httpSession=httpSession;
}

public void setAttribute(String key,Object value)
{
this.httpSession.setAttribute(key,value);
}

public Object getAttribute(String key)
{
return this.httpSession.getAttribute(key);
}

}//class ends