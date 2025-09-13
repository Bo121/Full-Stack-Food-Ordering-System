# Welcome to QuickBite Takeout!

# 1. Technologies Used

## (1) Backend

**Spring Boot:** REST API framework.

**Spring Security:** Authentication and authorization.

**Hibernate:** ORM for database operations, integrated with Spring Security for role-based data access.

**Redis:** Caching system.

**MySQL:** Relational database.

**JavaMailSender:** Email service.

## (2) Frontend

**Vue.js:** Frontend framework for reactive UI.

**Vuex:** State management library for handling application state efficiently.

**Element-UI:** UI component library.

**Axios:** HTTP client for API requests.

## (3) Others

**Aliyun SMS API:** For sending SMS notifications.



# 2. QuickBite Takeout Features 

## (1) User Portal

### 1. Login
![User login screen](Feature-Images/User_Login.png)

### 2. Email Verification
![Verification code input](Feature-Images/Verification_Code.png)

### 3. Browse & Order
![User main ordering page](Feature-Images/User_Main_Page.png)

### 4. Flavor & Options
![Dish details and option selection](Feature-Images/Dish_Info.png)

### 5. Cart
![Shopping cart summary](Feature-Images/Cart.png)

### 6. Address Management
![Address editing form](Feature-Images/Address.png)

### 7. Place Order
![Order confirmation and submit](Feature-Images/Place_Order.png)


## (2) Admin Dashboard

### 1. Admin Login
![Admin login](Feature-Images/Login.png)

### 2. Employees
![Employee management page](Feature-Images/Employee_Page.png)

### 3. Categories
![Category management page](Feature-Images/Category_Page.png)

### 4. Dishes
![Dish management page](Feature-Images/Dish_Page.png)

### 5. Combos
![Combo builder page](Feature-Images/Combo.png)

### 6. Orders
![Orders overview and details](Feature-Images/Orders_Page.png)
# 3. Project Structure

## (1) Backend

src/main/java/com/app/quickbite

**controller:** RESTful API controllers.

**service:** Business logic and service layer.

**mapper:** MyBatis mappers for database interaction.

**entity:** Java entity classes for database tables.

**config:** Configuration files for Redis, MyBatis, etc.

## (2) Frontend

**public/:** Static assets.

**src/**

**api/:** Axios service calls.

**components/:** Vue.js components.

**views/:** Page components.

**store/:** Vuex state management.

# 4. API Endpoints

## (1) Employee Management

**POST /employee/login:** Login API.

**POST /employee/logout:** Logout API.

**GET /employee/page:** List employees with pagination.

**POST /employee:** Add employee.

**PUT /employee:** Edit or enable/disable employee.

## (2) Category Management

**GET /category/page:** List categories with pagination.

**POST /category:** Add category.

**PUT /category:** Edit category.

**DELETE /category:** Delete category.

## (3) Dish Management

**GET /dish/page:** List dishes with pagination.

**POST /dish:** Add dish.

**PUT /dish:** Edit dish.

**DELETE /dish:** Delete dish.

## (4) Set Meal Management

**GET /setmeal/page:** List set meals with pagination.

**POST /setmeal:** Add set meal.

**PUT /setmeal:** Edit set meal.

**DELETE /setmeal:** Delete set meal.

**POST /setmeal/status:** Update set meal availability.

## (5) Order Management

**GET /order/page:** List orders with pagination.

**PUT /order:** Update order status.

**GET /orderDetail/{id}:** Fetch order details.
