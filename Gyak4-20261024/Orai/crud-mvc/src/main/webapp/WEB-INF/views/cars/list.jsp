<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cars</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="top-bar">
        <h1>Cars</h1>
        <a class="button" href="${pageContext.request.contextPath}/cars/new">+ New car</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>Id</th>
                <th>Make</th>
                <th>Model</th>
                <th>Year</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="car" items="${cars}">
            <tr>
                <td>${car.id}</td>
                <td>${car.make}</td>
                <td>${car.model}</td>
                <td>${car.manufactureYear}</td>
                <td class="actions">
                    <a class="button" href="${pageContext.request.contextPath}/cars/${car.id}/edit">Edit</a>
                    <form action="${pageContext.request.contextPath}/cars/${car.id}/delete" method="post"
                          onsubmit="return confirm('Delete this car?');">
                        <button type="submit" class="danger">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty cars}">
            <tr>
                <td colspan="5">No cars yet.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</body>
</html>
