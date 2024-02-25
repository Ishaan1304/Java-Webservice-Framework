package bobby.test;
import com.thinking.machines.webrock.annotations.*;

public class Macro 
{
@OnStartup(priority=2)
public void addHome()
{
System.out.println("Student home add");
}
@OnStartup(priority=1)
public void removeStudent()
{
System.out.println("Student home remove");
}

@OnStartup(priority=17)
public void rStudent()
{
System.out.println("Student home remove");
}


@OnStartup(priority=12)
public void mStudent()
{
System.out.println("Student home remove");
}


}//class ends 