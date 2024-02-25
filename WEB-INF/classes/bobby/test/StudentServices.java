package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import java.util.*;
import java.sql.*;
@SecuredAccess(checkPost="bobby.test.StudentHome",guard="access")
@Path("/sstudentServices")
@Get
@Post
@InjectSessionScope
public class StudentServices
{

public SessionScope sessionScope;
@AutoWired(name="nnumberrr")
private Integer number;
@AutoWired(name="myyNamee")
private String myName;
@AutoWired(name="Stud")
private Student s;



public void sets(Student s)
{
this.s=s;
System.out.println("s works .............");
}

public void setnumber(Integer number)
{
this.number=number;
System.out.println("number works .............");
}

public void setmyName(String myName)
{
this.myName=myName;
System.out.println("myName works .............");
}


public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
System.out.println("SetSessionScope method got called"+sessionScope);
//System.out.println("SetSessionScope method got called"+sessionScope.getAttribute());
System.out.println("SetSessionScope method got called"+this.sessionScope);
}


public static Connection getConnection()
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/hrdb","hr","hr");
}catch(Exception e)
{
System.out.println(e);
}
return connection;
}//function ends

@Path("/aaddStudent")
@Get
@ForwardTo("/student.jsp")
public void addStudent(Student s) throws Exception
{
/*
System.out.println("*****add got called********");
System.out.println(s.getRollNumber());
System.out.println(s.getAge());
System.out.println(s.getName());
*/

Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select rollNumber from Student where rollNumber=?");
preparedStatement.setInt(1,s.getRollNumber());
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new Exception("student with roll number "+s.getRollNumber()+" already exists.");
}
preparedStatement=connection.prepareStatement("insert into Student (rollNumber,name,age) values(?,?,?)");
preparedStatement.setInt(1,s.getRollNumber());
preparedStatement.setString(2,s.getName());
preparedStatement.setInt(3,s.getAge());
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();

}//function ends


@Path("/uupdateStudent")
@Post
@ForwardTo("/sstudentServices/aaddStudent")
public void updateStudent(Student s) throws Exception
{
System.out.println("*************");
}


@Path("/rremoveStudent")
@Get
@Post
public void removeStudent(@RequestParameter("rollNumber") int rollNumber) throws Exception
{
System.out.println("*************");
}

@Path("/getAllStudent")
public List<Student> getAllStudent() throws Exception
{
System.out.println("******getAllGotCalled*******");
/*
List<Student> list=new ArrayList<>();
list.add(new Student(102,"Ishaan",21));
list.add(new Student(103,"Nipun",23));
list.add(new Student(104,"Mohit",23));
list.add(new Student(105,"Megh",21));
for(Student s:list)
{
System.out.println(s.getRollNumber());
System.out.println(s.getName());
System.out.println(s.getAge());
}
*/
List<Student> list=new LinkedList<Student>();
Student s;
Connection connection=getConnection();
ResultSet resultSet;
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select * from Student");
resultSet=preparedStatement.executeQuery();
while(resultSet.next())
{
s=new Student();
s.setRollNumber(resultSet.getInt("rollNumber"));
s.setName(resultSet.getString("name"));
s.setAge(resultSet.getInt("age"));
list.add(s);
}
resultSet.close();
preparedStatement.close();
connection.close();
return list;
}

@Path("/getStudentByRollNumber")
public Student getStudentByRollNumber(@RequestParameter("rollNumber") int rollNumber) throws Exception
{
System.out.println("*************");
return new Student(201,"Manan",23);
}//function ends

}//class ends