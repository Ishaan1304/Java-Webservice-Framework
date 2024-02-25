
class Student
{
constructor(rollNumber,name,age)
{
this.rollNumber=rollNumber;
this.name=name;
this.age=age;
}

setRollNumber(rollNumber)
{
this.rollNumber=rollNumber;
}
getRollNumber()
{
return this.rollNumber;
}

setName(name)
{
this.name=name;
}
getName()
{
return this.name;
}

setAge(age)
{
this.age=age;
}
getAge()
{
return this.age;
}

}//class ends


class StudentService
{
addStudent(requestData)
{
var prm=new Promise(function(resolve,reject){
$.ajax({
"url" : "/TMWebRock/schoolService/sstudentServices/aaddStudent",
"type" : "POST",
"data" : JSON.stringify(requestData),
"contentType" : "application/json",
"success" : function(response){
resolve("student data added");
},
"error" : function(){
reject("student data not added");
}
});
});
return prm;
}//addStudent function ends

getAllStudent()
{
var prm=new Promise(function(resolve,reject){
$.ajax({
"url" : "/TMWebRock/schoolService/sstudentServices/getAllStudent",
"type" : "GET",
"contentType" : "application/json",
"success" : function(responseData){
resolve(responseData);
},
"error" : function(){
reject("response failed");
}
});
});
return prm;
}

}//class ends


