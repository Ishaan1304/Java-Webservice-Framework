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
    try {
      System.out.println("Started");
      StringBuilder fullUrlPath = new StringBuilder(request.getRequestURL().toString());
      System.out.println(fullUrlPath.toString());
      pw = response.getWriter();
      ServletContext context = getServletContext();
      String attributeName = "webRockModel";
      WebRockModel webRockModel = (WebRockModel) context.getAttribute(attributeName);
      if (webRockModel == null) {
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
      System.out.println("Request get parameter----------------" + jsonParam);
      String decodedJsonString = null;
      if (jsonParam != null) {
        try {
          decodedJsonString = URLDecoder.decode(jsonParam, StandardCharsets.UTF_8);
          JsonObject jsonData = new JsonParser().parse(decodedJsonString).getAsJsonObject();
        } catch (Exception e) {
          e.printStackTrace();
          // Handle the decoding exception as needed
        }
      }
      System.out.println(decodedJsonString);
      System.out.println("------------------------------------------------------------");
      Object result = null;
      if (map.containsKey(pathInfo) == false) {
        System.out.println("request method not found......");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return;
      }
      Service service = null;
      if (map.containsKey(pathInfo)) {
        service = map.get(pathInfo);
        if (service.getIsGetAllowed() == false) {
          System.out.println("GET method not allowed ......");
          response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
          return;
        }

        Class clazz = service.getServiceClass();
        Object instance = service.getServiceClass().newInstance();
        Method method = service.getServiceMethod();
        ServiceResponse serviceResponse = null;

        //securedAccess imple starts
        if (service.getIsSecured()) {
          Class securedClass = Class.forName(service.getCheckPost());
          String securedMethodName = service.getGuard();
          Parameter secureMethodParameters = null;
          Object guardMethodParameters[] = null;
          Class parameterTypesInGuard[] = null;

          for (Method guardMethod: securedClass.getMethods()) {
            if (guardMethod.getName().equals(securedMethodName)) {
              parameterTypesInGuard = guardMethod.getParameterTypes();
              for (int i = 0; i < parameterTypesInGuard.length; i++) {
                guardMethodParameters = new Object[parameterTypesInGuard.length];
                if (parameterTypesInGuard[i].getSimpleName().equals("ApplicationScope")) {
                  guardMethodParameters[i] = new ApplicationScope(context);
                } else if (parameterTypesInGuard[i].getSimpleName().equals("ApplicationDirectory")) {
                  guardMethodParameters[i] = new ApplicationDirectory(new File(context.getRealPath("")));
                } else if (parameterTypesInGuard[i].getSimpleName().equals("SessionScope")) {
                  guardMethodParameters[i] = new SessionScope(request.getSession());
                } else if (parameterTypesInGuard[i].getSimpleName().equals("requestScope")) {
                  guardMethodParameters[i] = new RequestScope(request);
                } else {
                  //  ////////////  exception
                }

              } //loop on parameter
              try {
                guardMethod.invoke(securedClass.newInstance(), guardMethodParameters);
              } catch (InvocationTargetException invocationTargetException) {
                System.out.println("guard sent exception : " + invocationTargetException);
                System.out.println("with cause : " + invocationTargetException.getCause());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
              }
              break;
            } //condition
          } //loop on class

        } //condition ends
        //securedAccess imple ends

        if (service.getInjectSessionScope()) {
          Method setSessionScope = clazz.getMethod("setSessionScope", SessionScope.class);
          setSessionScope.invoke(instance, new SessionScope(request.getSession()));
        }

        if (service.getInjectApplicationScope()) {
          Method setApplicationScope = clazz.getMethod("setApplicationScope", ApplicationScope.class);
          setApplicationScope.invoke(instance, new ApplicationScope(getServletContext()));
        }

        if (service.getInjectRequestScope()) {
          Method setRequestScope = clazz.getMethod("setRequestScope", RequestScope.class);
          setRequestScope.invoke(instance, new RequestScope(request));
        }

        if (service.getInjectApplicationDirectory()) {
          Method setApplicationDirectory = clazz.getMethod("setApplicationDirectory", ApplicationDirectory.class);
          context = request.getServletContext();
          String relativePath = "/WEB-INF/classes";
          String absolutePath = context.getRealPath(relativePath);
          setApplicationDirectory.invoke(instance, new ApplicationDirectory(new File(absolutePath)));
        }

        //auto wired prop
        List < AutoWiredService > autoWiredList = service.getAutoWiredList();
        String autoWiredAttributeName;
        Class autoWiredAttributeType;
        Field autoWiredAttributeField;
        Object requestObject;
        Object sessionScopeObject;
        Object applicationScopeObject;
        Method autoWiredMethod;
        for (AutoWiredService autoWiredService: autoWiredList) {
          autoWiredAttributeName = autoWiredService.getName();
          autoWiredAttributeField = autoWiredService.getAutoWiredField();
          autoWiredAttributeType = autoWiredAttributeField.getType();

          System.out.println(autoWiredAttributeName);
          System.out.println(autoWiredAttributeType);
          System.out.println(autoWiredAttributeField);
          requestObject = request.getAttribute(autoWiredAttributeName);
          sessionScopeObject = request.getSession().getAttribute(autoWiredAttributeName);
          applicationScopeObject = getServletContext().getAttribute(autoWiredAttributeName);
          System.out.println("/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
          System.out.println(requestObject);
          System.out.println(sessionScopeObject);
          System.out.println(applicationScopeObject);
          if (requestObject != null && autoWiredAttributeType.isInstance(requestObject)) {
            autoWiredMethod = clazz.getMethod("set" + autoWiredAttributeField.getName(), autoWiredAttributeType);
            autoWiredMethod.invoke(instance, requestObject);
            System.out.println("Invoked set" + autoWiredAttributeField.getName() + " with request attribute");
          } else if (sessionScopeObject != null && autoWiredAttributeType.isInstance(sessionScopeObject)) {
            autoWiredMethod = clazz.getMethod("set" + autoWiredAttributeField.getName(), autoWiredAttributeType);
            autoWiredMethod.invoke(instance, sessionScopeObject);
            System.out.println("Invoked set" + autoWiredAttributeField.getName() + " with session attribute");
          } else if (applicationScopeObject != null && autoWiredAttributeType.isInstance(applicationScopeObject)) {
            autoWiredMethod = clazz.getMethod("set" + autoWiredAttributeField.getName(), autoWiredAttributeType);
            autoWiredMethod.invoke(instance, applicationScopeObject);
            System.out.println("Invoked set" + autoWiredAttributeField.getName() + " with application attribute");
          }
          System.out.println("____________AWP______________");
        } //list loop ends
        //auto wired ends

        //setting parameters starts
        if (service.getRequestParameterList().size() != 0) {
          System.out.println("Setting Request Parameter If control");
          if (method.getParameterCount() != service.getRequestParameterList().size()) {
            System.out.println("parameter count is not matching with annotation applied parameters.");
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
          }

          String parameterName;
          Class parameterType;
          String parameterTypeName;
          String parameter;
          Object requestParameters[] = new Object[service.getRequestParameterList().size()];
          int i = 0;
          for (RequestParameterService requestParameterService: service.getRequestParameterList()) {
            parameter = null;
            parameterName = requestParameterService.getName();
            parameterType = requestParameterService.getParameterType();
            parameter = request.getParameter(parameterName);
            parameterTypeName = parameterType.toString();

            System.out.println(parameterName);
            System.out.println(parameterType);
            System.out.println(parameter);
            System.out.println(parameterTypeName);

            if (requestParameterService.getIsJson()) {
              System.out.println("Yaha baat gayi hai");
              Gson gson = new Gson();
              BufferedReader bufferedReader = request.getReader();
              StringBuffer stringBuffer = new StringBuffer();
              String b;
              String rawString;
              while (true) {
                b = bufferedReader.readLine();
                if (b == null) break;
                stringBuffer.append(b);
              }
              rawString = stringBuffer.toString();
              System.out.println(rawString);
              requestParameters[i] = gson.fromJson(rawString, parameterType);
            } else if (requestParameterService.getIsApplicationScope()) {
              requestParameters[i] = new ApplicationScope(getServletContext());
            } else if (requestParameterService.getIsApplicationDirectory()) {
              requestParameters[i] = new ApplicationDirectory(new File(context.getRealPath("")));
            } else if (requestParameterService.getIsSessionScope()) {
              requestParameters[i] = new SessionScope(request.getSession());
            } else if (requestParameterService.getIsRequestScope()) {
              requestParameters[i] = new RequestScope(request);
            } else if (requestParameterService.getIsPrimitive()) {
              if (parameterTypeName.equals("int") || parameterTypeName.equals("Integer")) {
                try {
                  requestParameters[i] = Integer.parseInt(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("long") || parameterTypeName.equals("Long")) {
                try {
                  requestParameters[i] = Long.parseLong(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("short") || parameterTypeName.equals("Short")) {
                try {
                  requestParameters[i] = Short.parseShort(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("double") || parameterTypeName.equals("Double")) {
                try {
                  requestParameters[i] = Double.parseDouble(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("boolean") || parameterTypeName.equals("Boolean")) {
                requestParameters[i] = Boolean.parseBoolean(parameter);
              } else if (parameterTypeName.equals("float") || parameterTypeName.equals("Float")) {
                try {
                  requestParameters[i] = Float.parseFloat(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("byte") || parameterTypeName.equals("Byte")) {
                try {
                  requestParameters[i] = Byte.parseByte(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("char") || parameterTypeName.equals("Character")) {
                requestParameters[i] = parameter.charAt(0);
              } else {
                requestParameters[i] = parameter;
              }
            }
            i++;
          } //parameter loop ends

          //setting parameters ends
          //calling method

          System.out.println("Request parameter size " + requestParameters.length);
          serviceResponse = new ServiceResponse();
          serviceResponse.setIsSuccess(true);
          if (requestParameters.length != 0) {
            System.out.println("Request parameter size not zero---");

            serviceResponse.setResult(method.invoke(instance, requestParameters));
          } else {
            System.out.println("Request parameter size zero---");

            serviceResponse.setResult(method.invoke(instance));
          }
          String forwardTo = service.getForwardTo();
          System.out.println("Forwarding to ---------->" + forwardTo);
          if (forwardTo != null) {

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
          return;

        } else //if(service.getRequestParameterList()==null || service.getRequestParameterList().size()==0)
        {
          System.out.println("Another If control");

          if (method.getParameterCount() == 0) {
            System.out.println("------------------------------------------------------------");
            serviceResponse = new ServiceResponse();
            serviceResponse.setIsSuccess(true);
            serviceResponse.setResult(method.invoke(instance));
          } else {
            System.out.println("------------------------------------------------------------");
            Class < ? > [] parameterTypes = method.getParameterTypes();
            Object[] arguments = new Object[parameterTypes.length];
            JsonObject jsonObject = gson.fromJson(decodedJsonString, JsonObject.class);
            if (jsonObject != null) {
              if (parameterTypes.length != jsonObject.size()) {
                System.out.println("Invalid number of arguments provided");
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
              }
              int i1 = 0;
              for (Map.Entry < String, JsonElement > entry: jsonObject.entrySet()) {
                String paramName = entry.getKey();
                JsonElement paramValue = entry.getValue();
                if (paramValue.isJsonObject()) {
                  // Handle JSON objects
                  arguments[i1] = gson.fromJson(paramValue, parameterTypes[i1]);
                } else if (paramValue.isJsonPrimitive()) {
                  // Handle JSON primitives based on parameter type
                  JsonPrimitive primitive = paramValue.getAsJsonPrimitive();
                  if (parameterTypes[i1] == int.class) {
                    arguments[i1] = primitive.getAsInt();
                  } else if (parameterTypes[i1] == double.class) {
                    arguments[i1] = primitive.getAsDouble();
                  } else if (parameterTypes[i1] == boolean.class) {
                    arguments[i1] = primitive.getAsBoolean();
                  } else {
                    // Handle other primitive types if needed
                  }
                }
                i1++;
              }

            }
            serviceResponse = new ServiceResponse();
            serviceResponse.setIsSuccess(true);
            serviceResponse.setResult(method.invoke(instance, arguments));
            System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

          }

        } //map contains key end

        String forwardTo = service.getForwardTo();
        System.out.println("Forwarding to ---------->" + forwardTo);
        if (forwardTo != null) {
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
        System.out.println("Class not found for the path: " + pathInfo);
        response.getWriter().write("Class not found for the path: " + pathInfo);
        return;
      }

    } catch (Exception e) {
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
        Class clazz = service.getServiceClass();
        Object instance = service.getServiceClass().newInstance();
        Method method = service.getServiceMethod();
        ServiceResponse serviceResponse = null;

        //securedAccess imple starts
        if (service.getIsSecured()) {
          Class securedClass = Class.forName(service.getCheckPost());
          String securedMethodName = service.getGuard();
          Parameter secureMethodParameters = null;
          Object guardMethodParameters[] = null;
          Class parameterTypesInGuard[] = null;

          for (Method guardMethod: securedClass.getMethods()) {
            if (guardMethod.getName().equals(securedMethodName)) {
              parameterTypesInGuard = guardMethod.getParameterTypes();
              for (int i = 0; i < parameterTypesInGuard.length; i++) {
                guardMethodParameters = new Object[parameterTypesInGuard.length];
                if (parameterTypesInGuard[i].getSimpleName().equals("ApplicationScope")) {
                  guardMethodParameters[i] = new ApplicationScope(context);
                } else if (parameterTypesInGuard[i].getSimpleName().equals("ApplicationDirectory")) {
                  guardMethodParameters[i] = new ApplicationDirectory(new File(context.getRealPath("")));
                } else if (parameterTypesInGuard[i].getSimpleName().equals("SessionScope")) {
                  guardMethodParameters[i] = new SessionScope(request.getSession());
                } else if (parameterTypesInGuard[i].getSimpleName().equals("requestScope")) {
                  guardMethodParameters[i] = new RequestScope(request);
                } else {
                  //  ////////////  exception
                }

              } //loop on parameter
              try {
                guardMethod.invoke(securedClass.newInstance(), guardMethodParameters);
              } catch (InvocationTargetException invocationTargetException) {
                System.out.println("guard sent exception : " + invocationTargetException);
                System.out.println("with cause : " + invocationTargetException.getCause());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
              }
              break;
            } //condition
          } //loop on class

        } //condition ends
        //securedAccess imple ends

        if (service.getInjectSessionScope()) {
          Method setSessionScope = clazz.getMethod("setSessionScope", SessionScope.class);
          setSessionScope.invoke(instance, new SessionScope(request.getSession()));
        }

        if (service.getInjectApplicationScope()) {
          Method setApplicationScope = clazz.getMethod("setApplicationScope", ApplicationScope.class);
          setApplicationScope.invoke(instance, new ApplicationScope(getServletContext()));
        }

        if (service.getInjectRequestScope()) {
          Method setRequestScope = clazz.getMethod("setRequestScope", RequestScope.class);
          setRequestScope.invoke(instance, new RequestScope(request));
        }

        if (service.getInjectApplicationDirectory()) {
          Method setApplicationDirectory = clazz.getMethod("setApplicationDirectory", ApplicationDirectory.class);
          context = request.getServletContext();
          String relativePath = "/WEB-INF/classes";
          String absolutePath = context.getRealPath(relativePath);
          setApplicationDirectory.invoke(instance, new ApplicationDirectory(new File(absolutePath)));
        }

        //auto wired prop
        List < AutoWiredService > autoWiredList = service.getAutoWiredList();
        String autoWiredAttributeName;
        Class autoWiredAttributeType;
        Field autoWiredAttributeField;
        Object requestObject;
        Object sessionScopeObject;
        Object applicationScopeObject;
        Method autoWiredMethod;
        for (AutoWiredService autoWiredService: autoWiredList) {
          autoWiredAttributeName = autoWiredService.getName();
          autoWiredAttributeField = autoWiredService.getAutoWiredField();
          autoWiredAttributeType = autoWiredAttributeField.getType();

          System.out.println(autoWiredAttributeName);
          System.out.println(autoWiredAttributeType);
          System.out.println(autoWiredAttributeField);
          requestObject = request.getAttribute(autoWiredAttributeName);
          sessionScopeObject = request.getSession().getAttribute(autoWiredAttributeName);
          applicationScopeObject = getServletContext().getAttribute(autoWiredAttributeName);

          if (requestObject != null && autoWiredAttributeType.isInstance(requestObject)) {
            autoWiredMethod = clazz.getMethod("set" + autoWiredAttributeField.getName(), autoWiredAttributeType);
            autoWiredMethod.invoke(instance, requestObject);
          } else if (sessionScopeObject != null && autoWiredAttributeType.isInstance(sessionScopeObject)) {
            autoWiredMethod = clazz.getMethod("set" + autoWiredAttributeField.getName(), autoWiredAttributeType);
            autoWiredMethod.invoke(instance, sessionScopeObject);
          } else if (applicationScopeObject != null && autoWiredAttributeType.isInstance(applicationScopeObject)) {
            autoWiredMethod = clazz.getMethod("set" + autoWiredAttributeField.getName(), autoWiredAttributeType);
            autoWiredMethod.invoke(instance, applicationScopeObject);
          }
          System.out.println("____________AWP______________");
        } //list loop ends
        //auto wired ends

        //setting parameters starts
        if (service.getRequestParameterList().size() != 0) {
          if (method.getParameterCount() != service.getRequestParameterList().size()) {
            System.out.println("parameter count is not matching with annotation applied parameters.");
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
          }

          String parameterName;
          Class parameterType;
          String parameterTypeName;
          String parameter;
          Object requestParameters[] = new Object[service.getRequestParameterList().size()];
          int i = 0;
          for (RequestParameterService requestParameterService: service.getRequestParameterList()) {
            parameter = null;
            parameterName = requestParameterService.getName();
            parameterType = requestParameterService.getParameterType();
            parameter = request.getParameter(parameterName);
            parameterTypeName = parameterType.toString();

            System.out.println(parameterName);
            System.out.println(parameterType);
            System.out.println(parameter);
            System.out.println(parameterTypeName);

            if (requestParameterService.getIsJson()) {
              System.out.println("Yaha baat gayi hai");
              Gson gson = new Gson();
              BufferedReader bufferedReader = request.getReader();
              StringBuffer stringBuffer = new StringBuffer();
              String b;
              String rawString;
              while (true) {
                b = bufferedReader.readLine();
                if (b == null) break;
                stringBuffer.append(b);
              }
              rawString = stringBuffer.toString();
              System.out.println(rawString);
              requestParameters[i] = gson.fromJson(rawString, parameterType);
            } else if (requestParameterService.getIsApplicationScope()) {
              requestParameters[i] = new ApplicationScope(getServletContext());
            } else if (requestParameterService.getIsApplicationDirectory()) {
              requestParameters[i] = new ApplicationDirectory(new File(context.getRealPath("")));
            } else if (requestParameterService.getIsSessionScope()) {
              requestParameters[i] = new SessionScope(request.getSession());
            } else if (requestParameterService.getIsRequestScope()) {
              requestParameters[i] = new RequestScope(request);
            } else if (requestParameterService.getIsPrimitive()) {
              if (parameterTypeName.equals("int") || parameterTypeName.equals("Integer")) {
                try {
                  requestParameters[i] = Integer.parseInt(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("long") || parameterTypeName.equals("Long")) {
                try {
                  requestParameters[i] = Long.parseLong(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("short") || parameterTypeName.equals("Short")) {
                try {
                  requestParameters[i] = Short.parseShort(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("double") || parameterTypeName.equals("Double")) {
                try {
                  requestParameters[i] = Double.parseDouble(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("boolean") || parameterTypeName.equals("Boolean")) {
                requestParameters[i] = Boolean.parseBoolean(parameter);
              } else if (parameterTypeName.equals("float") || parameterTypeName.equals("Float")) {
                try {
                  requestParameters[i] = Float.parseFloat(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("byte") || parameterTypeName.equals("Byte")) {
                try {
                  requestParameters[i] = Byte.parseByte(parameter);
                } catch (NumberFormatException nef) {
                  System.out.println(nef);
                  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                  return;
                }
              } else if (parameterTypeName.equals("char") || parameterTypeName.equals("Character")) {
                requestParameters[i] = parameter.charAt(0);
              } else {
                requestParameters[i] = parameter;
              }
            }
            i++;
          } //parameter loop ends

          //setting parameters ends
          //calling method

          System.out.println("Request parameter size " + requestParameters.length);
          serviceResponse = new ServiceResponse();
          serviceResponse.setIsSuccess(true);
          if (requestParameters.length != 0) {
            System.out.println("Request parameter size not zero");

            serviceResponse.setResult(method.invoke(instance, requestParameters));
          } else {
            System.out.println("Request parameter size zero");

            serviceResponse.setResult(method.invoke(instance));
          }
          pw.println(new Gson().toJson(serviceResponse));
          pw.flush();
          return;

        } else if (service.getRequestParameterList() == null || service.getRequestParameterList().size() == 0) {

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
              int i1 = 0;
              for (Map.Entry < String, JsonElement > entry: jsonObject.entrySet()) {
                String paramName = entry.getKey();
                JsonElement paramValue = entry.getValue();

                if (paramValue.isJsonObject()) {
                  // Handle JSON objects
                  arguments[i1] = gson.fromJson(paramValue, parameterTypes[i1]);
                } else if (paramValue.isJsonPrimitive()) {
                  // Handle JSON primitives based on parameter type
                  JsonPrimitive primitive = paramValue.getAsJsonPrimitive();
                  if (parameterTypes[i1] == int.class) {
                    arguments[i1] = primitive.getAsInt();
                  } else if (parameterTypes[i1] == double.class) {
                    arguments[i1] = primitive.getAsDouble();
                  } else if (parameterTypes[i1] == boolean.class) {
                    arguments[i1] = primitive.getAsBoolean();
                  } else {
                    // Handle other primitive types if needed
                  }
                }
                i1++;
              }

            }
            serviceResponse = new ServiceResponse();
            serviceResponse.setIsSuccess(true);
            serviceResponse.setResult(method.invoke(instance, arguments));
            //result = method.invoke(instance, arguments);

          }
        }

        String forwardTo = service.getForwardTo();
        System.out.println("Forwarding to ---------->" + forwardTo);
        if (forwardTo != null) {

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

}