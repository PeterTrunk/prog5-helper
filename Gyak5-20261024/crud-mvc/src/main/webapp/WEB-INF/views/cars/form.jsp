<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${car.id == null ? 'New car' : 'Edit car'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>${car.id == null ? 'New car' : 'Edit car'}</h1>

    <form:form class="card" modelAttribute="car"
               action="${pageContext.request.contextPath}/cars${car.id == null ? '' : '/'.concat(car.id)}"
               method="post">

        <label for="make">Make</label>
        <form:input path="make" id="make"/>
        <form:errors path="make" cssClass="error"/>

        <label for="model">Model</label>
        <form:input path="model" id="model"/>
        <form:errors path="model" cssClass="error"/>

        <label for="manufactureYear">Manufacture year</label>
        <form:input path="manufactureYear" id="manufactureYear" type="number"/>
        <form:errors path="manufactureYear" cssClass="error"/>

        <p>
            <input type="submit" value="Save"/>
            <a class="button" href="${pageContext.request.contextPath}/cars">Cancel</a>
        </p>
    </form:form>
</body>
</html>
