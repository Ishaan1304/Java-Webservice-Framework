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
public class TMWebRock extends HttpServlet {
private static final Gson gson = new Gson();
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = null;
        try
	{	
		System.out.println("Started");
		StringBuilder fullUrlPath = new StringBuilder(request.getRequestURL().toString());
		System.out.println(fullUrlPath.toString());	
            	pw = response.getWriter();
            	ServletContext context = getServletContext();
            	String attributeName = "webRockModel";
            	WebRockModel webRockModel = (WebRockModel) context.getAttribute(attributeName);
            	if (webRockModel == null) 
		{
                	System.out.println("Model loading problem......");
                	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                	return;
            	}
            	Map < String, Service > map = webRockModel.getHashMap();
            	System.out.println("------------------------------------------------------------");
            	String pathInfo = request.getPathInfo();
            	System.out.println("Path Info: " + pathInfo);
            	System.out.println("------------------------------------------------------------");
            	String jsonParam = request.getParameter("jsonParam");
		System.out.println("Request get parameter----------------"+jsonParam);
            	String decodedJsonString = null;
            	if (jsonParam != null) 
		{
                	try 
			{
                		decodedJsonString = URLDecoder.decode(jsonParam, StandardCharsets.UTF_8);
                    		JsonObject jsonData = new JsonParser().parse(decodedJsonString).getAsJsonObject();
                	}catch (Exception e)
			{
                    		e.printStackTrace();
                    		// Handle the decoding exception as needed
                	}
            	}
            	System.out.println(decodedJsonString);
            	System.out.println("------------------------------------------------------------");
            	Object result = null;
            	if (map.containsKey(pathInfo) == false)
		{
                	System.out.println("request method not found......");
                	response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                	return;
            	}
            	Service service = null;
            	if (map.containsKey(pathInfo)) 
		{
                	service = map.get(pathInfo);
                	if (service.getIsGetAllowed() == false) 
			{
                    		System.out.println("GET method not allowed ......");
                    		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    		return;
                	}
                	Object instance = service.getServiceClass().newInstance();
                	Method method = service.getServiceMethod();
                	ServiceResponse serviceResponse = null;
                	if (method.getParameterCount() == 0)
			{
                    		System.out.println("------------------------------------------------------------");
                    		serviceResponse = new ServiceResponse();
                    		serviceResponse.setIsSuccess(true);
                    		serviceResponse.setResult(method.invoke(instance));
                	} 
			else
			{
                    		System.out.println("------------------------------------------------------------");
                    		Class < ? > [] parameterTypes = method.getParameterTypes();
                    		Object[] arguments = new Object[parameterTypes.length];
                    		JsonObject jsonObject = gson.fromJson(decodedJsonString, JsonObject.class);
                    		if (jsonObject != null) 
				{
                        		if (parameterTypes.length != jsonObject.size()) {
                            		System.out.println("Invalid number of arguments provided");
                            		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                            		return;
                        	}
                        	int i = 0;
                        	for (Map.Entry < String, JsonElement > entry: jsonObject.entrySet())
				{
                            		String paramName = entry.getKey();
                            		JsonElement paramValue = entry.getValue();
                            		if (paramValue.isJsonObject())
					{
                                		// Handle JSON objects
                                		arguments[i] = gson.fromJson(paramValue, parameterTypes[i]);
                            		}
					else if (paramValue.isJsonPrimitive())
					{
                                		// Handle JSON primitives based on parameter type
                                		JsonPrimitive primitive = paramValue.getAsJsonPrimitive();
                                		if (parameterTypes[i] == int.class)
						{
                                    			arguments[i] = primitive.getAsInt();
                                		} 
						else if (parameterTypes[i] == double.class) 
						{
                                    			arguments[i] = primitive.getAsDouble();
                                		} 
						else if (parameterTypes[i] == boolean.class) 
						{
                                    			arguments[i] = primitive.getAsBoolean();
                                		} 
						else 
						{
                                    			// Handle other primitive types if needed
                                		}
                            		}
                            		i++;
                        	}


                    	}
                    	serviceResponse = new ServiceResponse();
                    	serviceResponse.setIsSuccess(true);
                    	serviceResponse.setResult(method.invoke(instance, arguments));
                    	//result = method.invoke(instance, arguments);
                }
                String forwardTo = service.getForwardTo();
                System.out.println("Forwarding to ---------->" + forwardTo);
                if (forwardTo != null)
		{
		    
		    
		    fullUrlPath = new StringBuilder(request.getRequestURL().toString());
		    System.out.println(fullUrlPath.toString());
		    System.out.println("!_!_!_!_!_!_!_!_!_!_");
                    
		    response.setContentType("text/html");
		    request.setAttribute("serviceResponse", serviceResponse);
		    RequestDispatcher dispatcher = request.getRequestDispatcher(forwardTo);
		    dispatcher.forward(request, response);
		    return;
                }
                pw.println(new Gson().toJson(serviceResponse));
                pw.flush();
            } 
	    else
	    {
                System.out.println("Class not found for the path: " + pathInfo);
                response.getWriter().write("Class not found for the path: " + pathInfo);
                return;
            }

        }catch (Exception e) {
            System.out.println(e + " " + request.getRequestURI());
            System.out.println(e.getCause());
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setIsSuccess(false);
            serviceResponse.setException(e.getCause().toString());
            pw.println(new Gson().toJson(serviceResponse));
            pw.flush();
            //e.printStackTrace();
            //response.getWriter().write("Error processing data");
        }
}





    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = null;


        try {
            pw = response.getWriter();
	    StringBuilder fullUrlPath = new StringBuilder(request.getRequestURL().toString());
	    System.out.println(fullUrlPath.toString());
            ServletContext context = getServletContext();
            String attributeName = "webRockModel";
            WebRockModel webRockModel = (WebRockModel) context.getAttribute(attributeName);
            Map < String, Service > map = webRockModel.getHashMap();
            for (Map.Entry < String, Service > entry: map.entrySet()) {
                System.out.println("*********");
                Service s = entry.getValue();
                System.out.println(s.getPath());
                System.out.println(s.getServiceClass());
                System.out.println(s.getServiceMethod());
                System.out.println(s.getIsGetAllowed());
                System.out.println(s.getIsPostAllowed());
                System.out.println(s.getForwardTo());
                System.out.println("*********");
            }

            System.out.println("------------------------------------------------------------");
            String pathInfo = request.getPathInfo();
            System.out.println("Path Info: " + pathInfo);
            System.out.println("------------------------------------------------------------");


            // Extracting data from the request body for POST requests
            System.out.println("------------------------------------------------------------");
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            // Printing the request body
            System.out.println("Request Body: " + requestBody.toString());

            if (map.containsKey(pathInfo) == false) {
                System.out.println("request method not found......");
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }

            System.out.println("------------------------------------------------------------");
            Object result = null;
            Service service = null;
            if (map.containsKey(pathInfo)) {
                service = map.get(pathInfo);
                if (service.getIsPostAllowed() == false) {
                    System.out.println("POST method not allowed ......");
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    return;
                }

                Object instance = service.getServiceClass().newInstance();
                Method method = service.getServiceMethod();
                ServiceResponse serviceResponse = null;
                if (method.getParameterCount() == 0) {
                    serviceResponse = new ServiceResponse();
                    serviceResponse.setIsSuccess(true);
                    serviceResponse.setResult(method.invoke(instance));
                } else {
                    Class < ? > [] parameterTypes = method.getParameterTypes();

                    Object[] arguments = new Object[parameterTypes.length];
                    JsonObject jsonObject = gson.fromJson(requestBody.toString(), JsonObject.class);
                    if (jsonObject != null) {
                        if (parameterTypes.length != jsonObject.size()) {
                            System.out.println("Invalid number of arguments provided");
                            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                            return;
                        }
                        int i = 0;
                        for (Map.Entry < String, JsonElement > entry: jsonObject.entrySet()) {
                            String paramName = entry.getKey();
                            JsonElement paramValue = entry.getValue();

                            if (paramValue.isJsonObject()) {
                                // Handle JSON objects
                                arguments[i] = gson.fromJson(paramValue, parameterTypes[i]);
                            } else if (paramValue.isJsonPrimitive()) {
                                // Handle JSON primitives based on parameter type
                                JsonPrimitive primitive = paramValue.getAsJsonPrimitive();
                                if (parameterTypes[i] == int.class) {
                                    arguments[i] = primitive.getAsInt();
                                } else if (parameterTypes[i] == double.class) {
                                    arguments[i] = primitive.getAsDouble();
                                } else if (parameterTypes[i] == boolean.class) {
                                    arguments[i] = primitive.getAsBoolean();
                                } else {
                                    // Handle other primitive types if needed
                                }
                            }
                            i++;
                        }


                    }
                    serviceResponse = new ServiceResponse();
                    serviceResponse.setIsSuccess(true);
                    serviceResponse.setResult(method.invoke(instance, arguments));
                    //result = method.invoke(instance, arguments);

                }


		String forwardTo = service.getForwardTo();
                System.out.println("Forwarding to ---------->" + forwardTo);
                if (forwardTo != null)
		{
		    
		    
		    fullUrlPath = new StringBuilder(request.getRequestURL().toString());
		    System.out.println(fullUrlPath.toString());
		    System.out.println("!_!_!_!_!_!_!_!_!_!_");
                    
		    response.setContentType("text/html");
		    request.setAttribute("serviceResponse", serviceResponse);
		    RequestDispatcher dispatcher = request.getRequestDispatcher(forwardTo);
		    dispatcher.forward(request, response);
		    return;
                }

                pw.println(new Gson().toJson(serviceResponse));
                pw.flush();

            } else {
                System.out.println("Class not found for the path : " + pathInfo);
            }






        } catch (IOException e) {
            System.out.println(e + " " + request.getRequestURI());
            System.out.println(e.getCause());
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setIsSuccess(false);
            serviceResponse.setException(e.getCause().toString());
            pw.println(new Gson().toJson(serviceResponse));
            pw.flush();
        } catch (Exception e) {
            System.out.println(e + " " + request.getRequestURI());
            System.out.println(e.getCause());
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setIsSuccess(false);
            serviceResponse.setException(e.getCause().toString());
            pw.println(new Gson().toJson(serviceResponse));
            pw.flush();
        }
    }
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}





