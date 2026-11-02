<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head>
    <title>Students View</title>
</head>
<body>
<table>
    <thead>
    <tr>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${students}" var="student">
        <tr>
            <td>${student.name}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
