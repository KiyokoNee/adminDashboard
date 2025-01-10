<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- c:out ; c:forEach etc. --> 
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!-- Formatting (dates) --> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"  %>
<!-- form:form -->
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!-- for rendering errors on PUT routes -->
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Dashboard</title>
		<link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" href="/css/style.css" />
    	<script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
	</head>
	<body class="w-75 mx-auto my-5">
		<div class="d-flex justify-content-between">
			<h1>Welcome, <c:out value="${currentuser.username}"></c:out>!</h1>
			<form id="logoutForm" method="POST" action="/logout">
        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        		<input type="submit" value="Logout!" />
    		</form>
		</div>
		<p>First Name: <c:out value="${currentuser.firstname}"></c:out></p>
		<p>Last Name: <c:out value="${currentuser.lastname}"></c:out></p>
		<p>Email: <c:out value="${currentuser.email}"></c:out></p>
		<p>Signup Date: <fmt:formatDate pattern="${createdatepattern}" value="${currentuser.createdAt}"/></p>
		<p>Last Sign In: 
			<fmt:parseDate value="${signdate }" pattern="yyyy-MM-dd" var="parsedDate" type="date" />
			<fmt:formatDate value="${parsedDate}" pattern="${signdatepattern }" />
		</p>
	</body>
</html>