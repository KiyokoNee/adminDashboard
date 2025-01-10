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
		<title>Admin Dashboard</title>
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
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th>Name</th>
					<th>Email</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${allusers}">
				<c:if test="${currentuser.roles[0].name == 'ROLE_SUPER_ADMIN' || user.roles[0].name != 'ROLE_SUPER_ADMIN' }">
					<tr>
						<td><c:out value="${user.firstname}"></c:out> <c:out value="${user.lastname}"></c:out></td>
						<td><c:out value="${user.email}"></c:out></td>
						<td class="d-flex">
							<c:choose>
								<c:when test="${user.roles[0].name == 'ROLE_ADMIN'}">
									<c:choose>
										<c:when test="${currentuser.roles[0].name == 'ROLE_SUPER_ADMIN' }">
											<form method="post" action="/admin/${user.id}" >
												<input type="hidden" name="_method" value="delete" />
        										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
												<button class="btn btn-link" type="submit">Delete Admin</button>
											</form>
										</c:when>
										<c:otherwise>
											Admin
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${user.roles[0].name == 'ROLE_SUPER_ADMIN' }">
									superstar
								</c:when>
								<c:otherwise>
									<form method="post" action="/admin/${user.id}" >
										<input type="hidden" name="_method" value="put" />
        								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
										<button class="btn btn-link" type="submit">Make Admin</button>
									</form>
									<form method="post" action="/admin/${user.id}" >
										<input type="hidden" name="_method" value="delete" />
        								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
										<button class="btn btn-link" type="submit">Delete User</button>
									</form>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:if>
				</c:forEach>
			</tbody>
		</table>
	</body>
</html>