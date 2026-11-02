<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head>
    <title>Search Results</title>
</head>
<body>
<form method="get" action="${pageContext.request.contextPath}/students/search">
    <input type="text" name="name" value="${param.name}"/>
    <button type="submit">Search</button>
</form>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${results}" var="student">
        <tr>
            <td>${student.id}</td>
            <td>${student.name}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
