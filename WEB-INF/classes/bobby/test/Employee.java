package bobby.test;
public class Employee implements java.io.Serializable
{
private int id;
private String name;
private int age;

public Employee()
{
this.id=0;
this.name="";
this.age=0;
}


public Employee(int id,String name,int age)
{
this.id=id;
this.name=name;
this.age=age;
}

public void setId(int id)
{
this.id=id;
}

public int getRollNumber()
{
return this.id;
}

public void setName(String name)
{
this.name=name;
}

public String getName()
{
return this.name;
}

public void setAge(int age)
{
this.age=age;
}

public int getAge()
{
return this.age;
}

public String toString()
{
return id + "|" + name + "|" + age;
}

}//class ends