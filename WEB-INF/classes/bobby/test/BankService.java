package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import java.sql.*;
import java.util.*;
@Path("/bankService")
@Get
@Post
public class BankService
{
@Path("/addEmployee")
public void addEmployee(Employee e) throws Exception
{
System.out.println("*****add got called********");
}//function ends


@Path("/updateEmployee")
public void updateEmployee(Employee e) throws Exception
{
System.out.println("*******update got called******");
}


@Path("/removeEmployee")
public void removeEmployee(int id) throws Exception
{
System.out.println("******remove got called*******");
}

@Path("/getAllEmployees")
public List<Employee> getAllEmployee() throws Exception
{
System.out.println("*******get all called******");
List<Employee> list=new ArrayList<>();
list.add(new Employee(102,"Ishaan",21));
list.add(new Employee(103,"Nipun",23));
list.add(new Employee(104,"Mohit",23));
list.add(new Employee(105,"Megh",21));
return list;
}

@Path("/getEmployeeId")
public Employee getEmployeeById(int id) throws Exception
{
System.out.println("*************");
return new Employee(201,"Manan",23);
}//function ends

}//class ends