package com.thinking.machines.webrock;

import javax.servlet.*;
import javax.servlet.http.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.model.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import com.google.gson.*;

import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;

public class TMJsFileServer extends HttpServlet 
{
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
{
PrintWriter pw=null;
System.out.println("--------TMJsFileServer got called-------");
try
{
pw=response.getWriter();
response.setContentType("text/html");
String fileName=request.getParameter("name");
System.out.println(fileName);
String filePath ="C:\\tomcat9\\webapps\\TMWebRock\\WEB-INF\\js\\"+fileName;
RandomAccessFile randomAccessFile = new RandomAccessFile(filePath,"r");
String line;
while((line = randomAccessFile.readLine())!=null)
{
pw.println(line);               
}
pw.flush();
}catch(Exception e)
{
System.out.println(e);
}
}
}