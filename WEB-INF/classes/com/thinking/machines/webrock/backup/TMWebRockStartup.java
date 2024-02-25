package com.thinking.machines.webrock;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.model.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

public class TMWebRockStartup extends HttpServlet
{
private WebRockModel webRockModel;
private LinkedList<String> classesName; 



public void init()
{
System.out.println("------------------------------------");
ServletConfig servletConfig=getServletConfig();
String servicePackagePrefix=servletConfig.getInitParameter("SERVICE_PACKAGE_PREFIX");
String pathToUserPackagePrefix=getServletContext().getRealPath("/WEB-INF/classes/")+servicePackagePrefix;
System.out.println(pathToUserPackagePrefix);
System.out.println("------------------------------------");
getListOfclasses(pathToUserPackagePrefix,servicePackagePrefix);
for(String s : classesName)
{
System.out.println(s);
}
System.out.println("------------------------------------");
populateWebRockModel();
//adding webRockeModel in application/Scope
ServletContext servletContext=getServletContext();
servletContext.setAttribute("webRockModel",webRockModel);
System.out.println("Successfully setted webRock Model at application scope");

Map<String,Service> map=webRockModel.getHashMap();
for(Map.Entry<String,Service> entry : map.entrySet())
{
System.out.println("*********");
Service s=entry.getValue();
System.out.println(s.getPath());
System.out.println(s.getServiceClass());
System.out.println(s.getServiceMethod());
System.out.println("get : "+s.getIsGetAllowed());
System.out.println("post : "+s.getIsPostAllowed());
System.out.println(s.getForwardTo());	
System.out.println("*********");
}



}//init function ends


private void getListOfclasses(String path,String prefixName)
{
this.classesName=new LinkedList<String>();
final File folder = new File(path);
Queue<File> Q=new LinkedList<File>();
File f;
for (final File fileEntry : folder.listFiles()) 
{
if (fileEntry.isDirectory()) 
{
Q.add(fileEntry);
}
else 
{
if(fileEntry.getName().endsWith(".class")) 
{
String packName,rawPath;
rawPath=fileEntry.getPath();
packName=rawPath.substring(rawPath.indexOf(prefixName+"\\")+6,rawPath.indexOf(".class"));
packName=packName.replace("\\",".");
packName=prefixName+"."+packName;
this.classesName.add(packName);
}
}
}

while(!Q.isEmpty())
{
f=Q.peek(); Q.poll();
for (final File fileEntry : f.listFiles()) 
{
if (fileEntry.isDirectory()) 
{
Q.add(fileEntry);
}
else 
{
if(fileEntry.getName().endsWith(".class")) 
{
String packName,rawPath;
rawPath=fileEntry.getPath();
packName=rawPath.substring(rawPath.indexOf(prefixName+"\\")+6,rawPath.indexOf(".class"));
packName=packName.replace("\\",".");
packName=prefixName+"."+packName;
this.classesName.add(packName);
}
}
}
}
}//function ends






private void populateWebRockModel()
{
this.webRockModel=new WebRockModel();
try
{
for(String className : classesName)
{
	try {
                // Load the class dynamically
                Class<?> clazz = Class.forName(className);
                // Check if @Path annotation is present on the class
                if (clazz.isAnnotationPresent(Path.class)) {
                    Path classPathAnnotation = clazz.getAnnotation(Path.class);
                    String classAnnotationValue = classPathAnnotation.value();
		    
                    // Scan methods for @Path annotation
                    for (Method method : clazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Path.class)) {
				  Service myService=new Service();
			          Path methodPathAnnotation = method.getAnnotation(Path.class);
                                  String methodAnnotationValue = methodPathAnnotation.value();
			          if (method.isAnnotationPresent(Get.class)) {
                                  myService.setIsGetAllowed(true);
                                  }
                    	          if (method.isAnnotationPresent(Post.class)) {
                        	  myService.setIsPostAllowed(true);
                    	          }
                            	  if (clazz.isAnnotationPresent(Get.class)) {
                        		myService.setIsGetAllowed(true);
                    		  }
                    		  if (clazz.isAnnotationPresent(Post.class)) {
                          	        myService.setIsPostAllowed(true);
                    	   	  }
                            // Print the concatenated result
			    String requiredPath=classAnnotationValue+methodAnnotationValue;
                            System.out.println(classAnnotationValue+methodAnnotationValue);
			    System.out.println("#########################################");
			    myService.setPath(requiredPath);
			    myService.setServiceClass(clazz);
			    myService.setServiceMethod(method);
			    if (method.isAnnotationPresent(ForwardTo.class)) {
                            ForwardTo forwardToAnnotation = method.getAnnotation(ForwardTo.class);
                            String forwardToValue = forwardToAnnotation.value();
                            myService.setForwardTo(forwardToValue);
                            }
			    webRockModel.add(requiredPath,myService);
			    
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                // Handle class not found exception
                e.printStackTrace();
            }
}//for ends

}catch(Exception e)//outer try ends
{
e.printStackTrace();
}



}//function ends
}//class ends