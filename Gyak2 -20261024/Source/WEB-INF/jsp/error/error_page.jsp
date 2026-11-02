<%@ page language="java" contentType="text/html; UTF-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <title>Error</title>
</head>
<body>
<h1>Hiba történt!</h1>
<c:if test="${not empty status}">
    <h1>${status}</h1>
</c:if>
<table>
    <tr>
        <th>Dátum</th>
        <td>${timestamp}</td>
    </tr>
    <c:if test="${not empty exception}">
        <tr>
            <th>Exception</th>
            <td>${exception}</td>
        </tr>
        <tr>
            <th>Üzenet</th>
            <td>${message}</td>
        </tr>
    </c:if>
    <tr>
    </tr>
</table>
</body>
</html>
