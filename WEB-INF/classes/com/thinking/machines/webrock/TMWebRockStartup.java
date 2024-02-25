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

public class TMWebRockStartup extends HttpServlet {
  private WebRockModel webRockModel;
  private LinkedList < String > classesName;
  private LinkedList < Service > onStartupMethods;
  private static final Set < String > IS_PRIMITIVE = getIsPrimitiveOrWrapper();
  private static Set < String > getIsPrimitiveOrWrapper() {
    Set < String > ret = new HashSet < String > ();
    ret.add("class java.lang.Integer");
    ret.add("class java.lang.Character");
    ret.add("class java.lang.Byte");
    ret.add("class java.lang.Short");
    ret.add("class java.lang.Boolean");
    ret.add("class java.lang.Long");
    ret.add("class java.lang.Float");
    ret.add("class java.lang.Double");
    ret.add("class java.lang.Void");
    ret.add("int");
    ret.add("char");
    ret.add("byte");
    ret.add("short");
    ret.add("boolean");
    ret.add("long");
    ret.add("float");
    ret.add("double");
    ret.add("void");
    return ret;
  }

  public void init() {
    System.out.println("----------------------------------------------");
    System.out.println("----------------------------------------------");
    System.out.println("-------------___________----------------------");
    System.out.println("------------|____   ____|---------------------");
    System.out.println("---------------- | |--------------------------");
    System.out.println("-----------------| |--------------------------");
    System.out.println("-------------____| |____----------------------");
    System.out.println("------------|___________|---------------------");
    System.out.println("----------------------------------------------");
    System.out.println("----------------------------------------------");
    ServletConfig servletConfig = getServletConfig();
    String servicePackagePrefix = servletConfig.getInitParameter("SERVICE_PACKAGE_PREFIX");
    String pathToUserPackagePrefix = getServletContext().getRealPath("/WEB-INF/classes/") + servicePackagePrefix;
    System.out.println(pathToUserPackagePrefix);
    System.out.println("------------------------------------");
    getListOfclasses(pathToUserPackagePrefix, servicePackagePrefix);
    for (String s: classesName) {
      System.out.println(s);
    }
    System.out.println("------------------------------------");
    populateWebRockModel();
    //adding webRockeModel in application/Scope
    ServletContext servletContext = getServletContext();
    servletContext.setAttribute("webRockModel", webRockModel);
    System.out.println("Successfully setted webRock Model at application scope");

    Map < String, Service > map = webRockModel.getHashMap();
    for (Map.Entry < String, Service > entry: map.entrySet()) {
      System.out.println("*********");
      Service s = entry.getValue();
      System.out.println(s.getPath());
      System.out.println(s.getServiceClass());
      System.out.println(s.getServiceMethod());
      System.out.println("get : " + s.getIsGetAllowed());
      System.out.println("post : " + s.getIsPostAllowed());
      System.out.println(s.getForwardTo());
      System.out.println("InjectSessionScope : " + s.getInjectSessionScope());
      System.out.println("InjectApplicationScope : " + s.getInjectApplicationScope());
      System.out.println("InjectRequestScope : " + s.getInjectRequestScope());
      System.out.println("InjectApplicationDirectory : " + s.getInjectApplicationDirectory());
      System.out.println("auto wired list size  " + s.getAutoWiredList().size());
      System.out.println("request parameters  " + s.getRequestParameterList().size());
      System.out.println("secured  " + s.getIsSecured());
      System.out.println("check post  " + s.getCheckPost());
      System.out.println("guard  " + s.getGuard());
      System.out.println("*********");
    }

    populateOnStartupMethods();
    executeOnStartupMethods();
    System.out.println("???????" + onStartupMethods.size() + "?????????");
    for (Service s: onStartupMethods) {
      System.out.println(s.getPriority());
    }

  } //init function ends

  private void getListOfclasses(String path, String prefixName) {
    this.classesName = new LinkedList < String > ();
    final File folder = new File(path);
    Queue < File > Q = new LinkedList < File > ();
    File f;
    for (final File fileEntry: folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        Q.add(fileEntry);
      } else {
        if (fileEntry.getName().endsWith(".class")) {
          String packName, rawPath;
          rawPath = fileEntry.getPath();
          packName = rawPath.substring(rawPath.indexOf(prefixName + "\\") + 6, rawPath.indexOf(".class"));
          packName = packName.replace("\\", ".");
          packName = prefixName + "." + packName;
          this.classesName.add(packName);
        }
      }
    }

    while (!Q.isEmpty()) {
      f = Q.peek();
      Q.poll();
      for (final File fileEntry: f.listFiles()) {
        if (fileEntry.isDirectory()) {
          Q.add(fileEntry);
        } else {
          if (fileEntry.getName().endsWith(".class")) {
            String packName, rawPath;
            rawPath = fileEntry.getPath();
            packName = rawPath.substring(rawPath.indexOf(prefixName + "\\") + 6, rawPath.indexOf(".class"));
            packName = packName.replace("\\", ".");
            packName = prefixName + "." + packName;
            this.classesName.add(packName);
          }
        }
      }
    }
  } //function ends

  private void populateWebRockModel() {
    this.webRockModel = new WebRockModel();
    try {
      for (String className: classesName) {
        try {
          // Load the class dynamically
          Class < ? > clazz = Class.forName(className);
          // Check if @Path annotation is present on the class
          if (clazz.isAnnotationPresent(Path.class)) {

            SecuredAccess securedAccessAnnotationOnClass;
            SecuredAccess securedAccessAnnotationOnMethod;

            //setting up autoWiredService starts here
            LinkedList < AutoWiredService > ListOfAutoWiredServices = new LinkedList < AutoWiredService > ();
            Field fields[];
            fields = clazz.getDeclaredFields();
            AutoWired autoWiredAnnotation;
            for (Field field: fields) {
              autoWiredAnnotation = (AutoWired) field.getAnnotation(AutoWired.class);
              if (autoWiredAnnotation != null) {
                AutoWiredService autoWiredService = new AutoWiredService();
                autoWiredService.setName(autoWiredAnnotation.name());
                autoWiredService.setAutoWiredField(field);
                ListOfAutoWiredServices.add(autoWiredService);
              }
            } //fields loop  ends

            //setting up auto WiredService ends here

            Path classPathAnnotation = clazz.getAnnotation(Path.class);
            String classAnnotationValue = classPathAnnotation.value();
            // Scan methods for @Path annotation
            for (Method method: clazz.getDeclaredMethods()) {
              if (method.isAnnotationPresent(Path.class)) {
                Service myService = new Service();
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
                myService.setInjectSessionScope(clazz.isAnnotationPresent(InjectSessionScope.class));
                myService.setInjectApplicationScope(clazz.isAnnotationPresent(InjectApplicationScope.class));
                myService.setInjectRequestScope(clazz.isAnnotationPresent(InjectRequestScope.class));
                myService.setInjectApplicationDirectory(clazz.isAnnotationPresent(InjectApplicationDirectory.class));
                myService.setAutoWiredList(ListOfAutoWiredServices);
                // Print the concatenated result
                String requiredPath = classAnnotationValue + methodAnnotationValue;
                System.out.println(requiredPath);
                System.out.println("#########################################");

                int jsonCount = 0;
                int scopeAndDirectoryCount = 0;
                int requestParameterCount = 0;
                //populating requestParameterList of service Starts 
                Parameter parameters[];
                parameters = method.getParameters();
                LinkedList < RequestParameterService > requestParameterList = new LinkedList < RequestParameterService > ();

                for (Parameter parameter: parameters) {
                  RequestParameter requestParameterAnnotation;
                  requestParameterAnnotation = parameter.getAnnotation(RequestParameter.class);

                  if (requestParameterAnnotation != null) {
                    RequestParameterService requestParameterService = new RequestParameterService();
                    requestParameterService.setName(requestParameterAnnotation.value());
                    requestParameterService.setParameterType(parameter.getType());
                    System.out.println("pppppppp type" + parameter.getType());
                    System.out.println("FFFFFFFFFFFFF premitive " + parameter.getType().isPrimitive());
                    System.out.println("FFFFFFFFFFFFF to string " + parameter.getType().toString());
                    //System.out.println("FFFFFFFFFFFFF to string "+IS_PRIMITIVE.contains(parameter.getType().toString()));
                    if (IS_PRIMITIVE.contains(parameter.getType().toString()) || parameter.getType().toString().equals("class java.lang.String")) {
                      requestParameterService.setIsPrimitive(true);
                      requestParameterList.add(requestParameterService);
                      requestParameterCount++;
                    }
                    continue;
                  }
                  if (requestParameterAnnotation == null) {
                    break;
                  }

                  if (parameter.getType().toString().equals("class com.thinking.machines.webrock.pojo.ApplicationScope")) {
                    RequestParameterService requestParameterService = new RequestParameterService();
                    requestParameterService.setParameterType(parameter.getType());
                    requestParameterService.setIsApplicationScope(true);
                    requestParameterList.add(requestParameterService);
                    scopeAndDirectoryCount++;
                    continue;
                  }
                  if (parameter.getType().toString().equals("class com.thinking.machines.webrock.pojo.ApplicationDirectory")) {
                    RequestParameterService requestParameterService = new RequestParameterService();
                    requestParameterService.setParameterType(parameter.getType());
                    requestParameterService.setIsApplicationDirectory(true);
                    requestParameterList.add(requestParameterService);
                    scopeAndDirectoryCount++;
                    continue;
                  }
                  if (parameter.getType().toString().equals("class com.thinking.machines.webrock.pojo.SessionScope")) {
                    RequestParameterService requestParameterService = new RequestParameterService();
                    requestParameterService.setParameterType(parameter.getType());
                    requestParameterService.setIsSessionScope(true);
                    requestParameterList.add(requestParameterService);
                    scopeAndDirectoryCount++;
                    continue;
                  }
                  if (parameter.getType().toString().equals("class com.thinking.machines.webrock.pojo.RequestScope")) {
                    RequestParameterService requestParameterService = new RequestParameterService();
                    requestParameterService.setParameterType(parameter.getType());
                    requestParameterService.setIsRequestScope(true);
                    requestParameterList.add(requestParameterService);
                    scopeAndDirectoryCount++;
                    continue;
                  }

                  if (!parameter.getType().isPrimitive() && !parameter.getType().toString().equals("class java.lang.String")) {
                    RequestParameterService requestParameterService = new RequestParameterService();
                    requestParameterService.setParameterType(parameter.getType());
                    requestParameterService.setIsJson(true);
                    requestParameterList.add(requestParameterService);
                    jsonCount++;
                    continue;
                  }
                } //parameter loop ends

                //populating requestParameterList of service ends 
                myService.setRequestParameterList(requestParameterList);

                myService.setPath(requiredPath);
                myService.setServiceClass(clazz);
                myService.setServiceMethod(method);
                if (method.isAnnotationPresent(ForwardTo.class)) {
                  ForwardTo forwardToAnnotation = method.getAnnotation(ForwardTo.class);
                  String forwardToValue = forwardToAnnotation.value();
                  myService.setForwardTo(forwardToValue);
                }

                //security part starts 
                securedAccessAnnotationOnMethod = (SecuredAccess) method.getAnnotation(SecuredAccess.class);
                if (securedAccessAnnotationOnMethod != null) {
                  myService.setIsSecured(true);
                  myService.setCheckPost(securedAccessAnnotationOnMethod.checkPost());
                  myService.setGuard(securedAccessAnnotationOnMethod.guard());
                }
                securedAccessAnnotationOnClass = (SecuredAccess) clazz.getAnnotation(SecuredAccess.class);
                if (securedAccessAnnotationOnClass != null) {
                  myService.setIsSecured(true);
                  myService.setCheckPost(securedAccessAnnotationOnClass.checkPost());
                  myService.setGuard(securedAccessAnnotationOnClass.guard());
                }
                //security part ends

                webRockModel.add(requiredPath, myService);
              } //if ends
            } //for loop methods end
          } //if class annotation present ends
        } catch (ClassNotFoundException e) {
          // Handle class not found exception
          e.printStackTrace();
        }

      } //for ends for classes

    } catch (Exception e) //outer try ends
    {
      e.printStackTrace();
    }

  } //function ends

  private void populateOnStartupMethods() {
    this.onStartupMethods = new LinkedList < Service > ();
    try {
      for (String className: classesName) {
        Method methods[];
        Path classAnnotation, methodAnnotation;
        OnStartup onStartupAnnotation;
        Class clazz = Class.forName(className);
        methods = clazz.getDeclaredMethods();
        for (Method method: methods) {
          onStartupAnnotation = (OnStartup) method.getAnnotation(OnStartup.class);
          if (onStartupAnnotation != null && onStartupAnnotation.priority() > 0) {
            Service service = new Service();
            service.setServiceClass(clazz);
            service.setServiceMethod(method);
            service.setRunOnStart(true);
            service.setPriority(onStartupAnnotation.priority());
            this.onStartupMethods.add(service);
          }
        } //method loop end
      } //class loop ends
      //sorting onStartupMethods on base of priority 
      Collections.sort(this.onStartupMethods, new Comparator < Service > () {
        public int compare(Service s1, Service s2) {
          return s1.getPriority() - s2.getPriority();
        }
      });
    } catch (Exception e) {
      System.out.println(e);
    }
  } //function ends

  private void executeOnStartupMethods() {
    try {
      for (Service service: this.onStartupMethods) {
        Class clazz = service.getServiceClass();
        Method method = service.getServiceMethod();
        Object O = clazz.newInstance();
        method.invoke(O);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  } //function ends

} //class ends