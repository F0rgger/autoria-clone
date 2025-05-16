Autoria Clone
Ласкаво просимо до Autoria Clone, додатку на основі Spring Boot, який відтворює основні функції онлайн-платформи для оголошень про автомобілі. Цей проєкт надає RESTful API для автентифікації користувачів, управління оголошеннями та адміністративних завдань.
Зміст






Автентифікація користувачів та контроль доступу на основі ролей (наприклад, BUYER, SELLER, ADMIN).
CRUD-операції для оголошень про автомобілі.
Безпека на основі JWT для API ендпоїнтів.
Функція підвищення ролі (наприклад, з BUYER до SELLER).
Інструменти адміністратора для управління користувачами та ролями.
Інтеграція з PostgreSQL для збереження даних.



API Ендпоїнти
Автентифікація

POST /api/auth/register - Реєстрація нового користувача.
POST /api/auth/login - Вхід та отримання JWT-токена.
GET /api/auth/me - Отримання даних поточного користувача.
POST /api/auth/upgrade - Підвищення ролі користувача (наприклад, до SELLER).
POST /api/auth/create-manager - Створення користувача-менеджера (потрібна роль ADMIN).

Оголошення

POST /advertisements - Створення нового оголошення (потрібна роль SELLER).
GET /advertisements/{id} - Отримання оголошення за ID.
PUT /advertisements/{id} - Оновлення оголошення (потрібна роль SELLER).
DELETE /advertisements/{id} - Видалення оголошення (потрібна роль SELLER).

Приклад запиту (Створення оголошення)
{
"userId": 1,
"carBrand": "BMW",
"carModel": "X5",
"price": 35000,
"originalCurrency": "USD",
"city": "Kyiv",
"region": "Kyiv",
"description": "Good car"
}


Заголовок: Authorization: Bearer <your-jwt-token>
