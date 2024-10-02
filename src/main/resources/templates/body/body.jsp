<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="sidebar pe-4 pb-3">
	<nav class="navbar bg-light navbar-light">
		<a href="${pageContext.request.contextPath}/homePage" class="navbar-brand mx-4 mb-3">
			<h3 class="text-primary">
				<i class="fa fa-hashtag me-2"></i>超市後台系統
			</h3>
		</a>
		<div class="d-flex align-items-center ms-4 mb-4">
			<div class="position-relative">
				<img class="rounded-circle"
					src="${pageContext.request.contextPath}/resources/img/user.jpg" alt=""
					style="width: 40px; height: 40px;">
				<div
					class="bg-success rounded-circle border border-2 border-white position-absolute end-0 bottom-0 p-1"></div>
			</div>
			<div class="ms-3">
				<h6 class="mb-0">${employee.employeeName}</h6>
				<span>${employee['class'].simpleName eq 'EmpBean' ? employee.positionId : employee.positionName}</span>
			</div>
		</div>
		<div class="navbar-nav w-100">
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"><i class="fa fa-laptop me-2"></i>人資系統</a>
				<div class="dropdown-menu bg-transparent border-0">
					<a href="${pageContext.request.contextPath}/employee/empMain" class="dropdown-item">員工資訊</a>
					<a href="${pageContext.request.contextPath}/AskForLeaveCon/view" class="dropdown-item">請假</a>
                    <a href="${pageContext.request.contextPath}/schedule" class="dropdown-item">排班</a>
				</div>
			</div>

			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"><i class="fa fa-laptop me-2"></i>商品資訊</a>
				<div class="dropdown-menu bg-transparent border-0">
					<a href="${pageContext.request.contextPath}/insertProduct" class="dropdown-item">新增商品</a>
					<a href="${pageContext.request.contextPath}/getPagesProduct" class="dropdown-item">商品主頁</a>
				</div>
			</div>
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"><i class="fa fa-laptop me-2"></i>庫存系統</a>
				<div class="dropdown-menu bg-transparent border-0">
					<a href="${pageContext.request.contextPath}/restock/RestockMain" class="dropdown-item">新增庫存</a>
					<a href="${pageContext.request.contextPath}/restock/GetAllRestockDetailsDetail" class="dropdown-item">庫存明細</a>

				</div>
			</div>
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"><i class="fa fa-laptop me-2"></i>訂單系統</a>
				<div class="dropdown-menu bg-transparent border-0">
					<a
						href="${pageContext.request.contextPath}/checkout/checkoutMain"
						class="dropdown-item">訂單資訊</a>
				</div>
			</div>
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"><i class="fa fa-laptop me-2"></i>會員系統</a>
				<div class="dropdown-menu bg-transparent border-0">
					<a href="${pageContext.request.contextPath}/customer/cusMain" class="dropdown-item">會員資訊</a>
					<a href="${pageContext.request.contextPath}/bonusExchange" class="dropdown-item">紅利兌換查詢</a>
				</div>
			</div>
			<div class="dropdown-menu bg-transparent border-0">
				<a href="signin.html" class="dropdown-item">Sign In</a> <a
					href="signup.html" class="dropdown-item">Sign Up</a> <a
					href="404.html" class="dropdown-item">404 Error</a> <a
					href="blank.html" class="dropdown-item">Blank Page</a>
			</div>
		</div>
	</div>
</nav>
</div>
<div class="content">
	<!-- Navbar Start -->
	<nav
		class="navbar navbar-expand bg-light navbar-light sticky-top px-4 py-0">
		<a href="${pageContext.request.contextPath}/homePage" class="navbar-brand d-flex d-lg-none me-4">
			<h2 class="text-primary mb-0">
				<i class="fa fa-hashtag"></i>
			</h2>
		</a> <a href="#" class="sidebar-toggler flex-shrink-0"> <i
			class="fa fa-bars"></i>
		</a>
		<form class="d-none d-md-flex ms-4">
			<input class="form-control border-0" type="search"
				placeholder="Search">
		</form>
		<div class="navbar-nav align-items-center ms-auto">
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"> <i class="fa fa-envelope me-lg-2"></i>
					<span class="d-none d-lg-inline-flex">Message</span>
				</a>
				<div
					class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0">
					<a href="#" class="dropdown-item">
						<div class="d-flex align-items-center">
							<img class="rounded-circle"
								src="${pageContext.request.contextPath}/img/user.jpg" alt=""
								style="width: 40px; height: 40px;">
							<div class="ms-2">
								<h6 class="fw-normal mb-0">Jhon send you a message</h6>
								<small>15 minutes ago</small>
							</div>
						</div>
					</a>
					<hr class="dropdown-divider">
					<a href="#" class="dropdown-item">
						<div class="d-flex align-items-center">
							<img class="rounded-circle"
								src="${pageContext.request.contextPath}/resources/img/user.jpg" alt=""
								style="width: 40px; height: 40px;">
							<div class="ms-2">
								<h6 class="fw-normal mb-0">Jhon send you a message</h6>
								<small>15 minutes ago</small>
							</div>
						</div>
					</a>
					<hr class="dropdown-divider">
					<a href="#" class="dropdown-item">
						<div class="d-flex align-items-center">
							<img class="rounded-circle"
								src="${pageContext.request.contextPath}/resources/img/user.jpg" alt=""
								style="width: 40px; height: 40px;">
							<div class="ms-2">
								<h6 class="fw-normal mb-0">Jhon send you a message</h6>
								<small>15 minutes ago</small>
							</div>
						</div>
					</a>
					<hr class="dropdown-divider">
					<a href="#" class="dropdown-item text-center">See all message</a>
				</div>
			</div>
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle"
					data-bs-toggle="dropdown"> <i class="fa fa-bell me-lg-2"></i> <span
					class="d-none d-lg-inline-flex">Notificatin</span>
				</a>
				<div
					class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0">
					<a href="#" class="dropdown-item">
						<h6 class="fw-normal mb-0">Profile updated</h6> <small>15
							minutes ago</small>
					</a>
					<hr class="dropdown-divider">
					<a href="#" class="dropdown-item">
						<h6 class="fw-normal mb-0">New user added</h6> <small>15
							minutes ago</small>
					</a>
					<hr class="dropdown-divider">
					<a href="#" class="dropdown-item">
						<h6 class="fw-normal mb-0">Password changed</h6> <small>15
							minutes ago</small>
					</a>
					<hr class="dropdown-divider">
					<a href="#" class="dropdown-item text-center">See all
						notifications</a>
				</div>
			</div>
			<div class="nav-item dropdown">
				<a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
				<img class="rounded-circle me-lg-2" src="<c:url value='/resources/img/user.jpg'/>" alt=""
					style="width: 40px; height: 40px;"> <span class="d-none d-lg-inline-flex">${sessionScope.employee.employeeName}</span>
				</a>
				<div
					class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0">
					<a href="${pageContext.request.contextPath}/employee/details?employeeId=${sessionScope.employee.employeeId}" class="dropdown-item">My Profile</a>
					<a href="${pageContext.request.contextPath}/employee/update?employeeId=${sessionScope.employee.employeeId}" class="dropdown-item">Settings</a>
					<a href="${pageContext.request.contextPath}/logout" class="dropdown-item">Log Out</a>
				</div>
			</div>
		</div>
	</nav>