package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@Post
@Path("/SSHome")
public class StudentHome 
{
@Get
@Path("/addHome")
public void addHome(Student student)
{
System.out.println("Student home add");
}
@ForwardTo("/usa")
@Path("/removeHome")
public void removeStudent(Student student)
{
System.out.println("Student home remove");
}

public void access(ApplicationScope ap)
{
System.out.println("Access got called");
}

}//class ends 