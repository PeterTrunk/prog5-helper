<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="hu">
<head>
  <meta charset="UTF-8">
  <title>Regisztráció</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<h1>Regisztráció</h1>

<form:form method="post" action="${pageContext.request.contextPath}/register"
           modelAttribute="registerForm">

  <div>
    <form:label path="username">Felhasználónév</form:label>
    <form:input path="username" autocomplete="username"/>
    <form:errors path="username" cssClass="error"/>
  </div>

  <div>
    <form:label path="password">Jelszó</form:label>
    <form:password path="password" autocomplete="new-password"/>
    <form:errors path="password" cssClass="error"/>
  </div>

  <div>
    <form:label path="confirmPassword">Jelszó újra</form:label>
    <form:password path="confirmPassword" autocomplete="new-password"/>
    <form:errors path="confirmPassword" cssClass="error"/>
  </div>

  <button type="submit">Regisztrálok</button>
</form:form>

<p>Már van fiókod? <a href="${pageContext.request.contextPath}/login">Belépés</a></p>
</body>
</html>
