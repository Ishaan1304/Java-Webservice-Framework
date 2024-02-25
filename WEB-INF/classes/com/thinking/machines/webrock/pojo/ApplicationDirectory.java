package com.thinking.machines.webrock.pojo;
import java.io.*;
public class ApplicationDirectory implements java.io.Serializable
{
private File directory;
public ApplicationDirectory(File directory)
{
this.directory=directory;
}
public File getDirectory()
{
return this.directory;
}
}
