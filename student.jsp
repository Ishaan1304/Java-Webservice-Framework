<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Information</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h1, h2, h3 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

    <div class="container">
        <h1>Student Information Page</h1>
        <h2>Welcome to the Student Portal</h2>
        <h3>Explore the student resources here</h3>
<% 
Object obj=request.getAttribute("serviceResponse"); 
System.out.println("JSP me hua "+obj);
%>
<h1>TM</h1>

    </div>

</body>
</html>
