package com.thinking.machines.webrock.pojo;
import java.io.*;
import java.lang.reflect.*;
public class AutoWiredService implements java.io.Serializable
{
private String name;
private Field autoWiredField;

public AutoWiredService()
{
name="";
autoWiredField=null;
}

public void setName(String name)
{
this.name=name;
}

public String getName()
{
return this.name;
}

public void setAutoWiredField(Field autoWiredField)
{
this.autoWiredField=autoWiredField;
}

public Field getAutoWiredField()
{
return this.autoWiredField;
}


}//class ends
