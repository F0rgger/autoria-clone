AutoRia Clone

AutoRia Clone — це сучасна веб-платформа для продажу автомобілів, розроблена з використанням Spring Boot, PostgreSQL і контейнеризації для деплоя в AWS. Підтримує ролі (Покупець, Продавець, Менеджер, Адміністратор, Автосалон), типи акаунтів (Базовий, Преміум), створення/пошук оголошень, аналітику, модерацію та інтеграцію з зовнішніми сервісами (ПриватБанк API, SMTP.com, Google Cloud Natural Language API).

Вимоги





Java: 17



Maven: 3.8+



Docker: 20.10+



PostgreSQL: 15



Змінні оточення:





SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD (для PostgreSQL)



SMTP_USERNAME, SMTP_PASSWORD (для SMTP.com)



GOOGLE_CLOUD_NLP_API_KEY (для Google Cloud Natural Language API)

Налаштування





Клонування репозиторію:

git clone <repository-url>
cd autoria-clone



Налаштування змінних оточення: Створіть файл .env або експортуйте змінні:

export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/autoria
export SPRING_DATASOURCE_USERNAME=autoria
export SPRING_DATASOURCE_PASSWORD=autoria
export SMTP_USERNAME=your_smtp_username
export SMTP_PASSWORD=your_smtp_password
export GOOGLE_CLOUD_NLP_API_KEY=your_google_api_key



Налаштування Google Cloud NLP:





Створіть проєкт у Google Cloud Console.



Увімкніть Cloud Natural Language API.



Згенеруйте API key і додайте до GOOGLE_CLOUD_NLP_API_KEY.

Локальний запуск





Складання проєкту:

mvn clean install



Запуск з Docker Compose:

docker-compose up -d





API доступне на http://localhost:8080.



PostgreSQL на localhost:5432.



Запуск без Docker:

mvn spring-boot:run

Тестування





Запуск тестів:

mvn test





Використовуються JUnit5, Mockito, Testcontainers, WireMock.



Postman:





Імпортуйте AutoRiaCloneAPI.postman_collection.json у Postman.



Налаштуйте base_url (http://localhost:8080) і jwt_token (отримайте через автентифікацію).



Виконайте запити для створення оголошень, пошуку, модерації, аналітики, управління автосалонами, зв’язку з продавцем.

Деплой в AWS





Підготовка образу:

docker build -t autoria-clone-api .



Деплой в AWS ECS:





Оновіть ecs-task-definition.json, додавши змінні оточення (SMTP_USERNAME, SMTP_PASSWORD, GOOGLE_CLOUD_NLP_API_KEY).



Використовуйте AWS CLI:

aws ecs register-task-definition --cli-input-json file://ecs-task-definition.json
aws ecs update-service --cluster autoria-cluster --service autoria-service --task-definition autoria-task



Деплой в AWS EKS:





Налаштуйте Kubernetes з k8s-deployment.yaml.



Застосуйте конфігурацію:

kubectl apply -f k8s-deployment.yaml

Основні ендпоінти





Оголошення:





POST /advertisements: Створення оголошення (потрібен дозвіл CREATE_ADVERTISEMENT).



GET /advertisements/search: Пошук оголошень (фільтри: марка, модель, ціна, місто, регіон, валюта).



POST /advertisements/{id}/contact: Зв’язок з продавцем/автосалоном.



Аналітика (Преміум):





GET /analytics/views/{id}: Перегляди оголошення.



GET /analytics/prices/{id}: Середня ціна.



Модерація:





POST /moderation/check/{id}: Перевірка оголошення на нецензурну лексику.



Адмін:





POST /users/managers: Створення менеджера.



POST /admin/approve-brand-model: Обробка запитів марки/моделі.



Автосалони:





POST /dealerships: Створення автосалону.

Структура проєкту





core: Утилити, конфігурації.



domain: Сутності, репозиторії.



application: Бізнес-логіка.



infrastructure: Інтеграції (Google Cloud NLP, ПриватБанк, SMTP.com).



api: REST-контролери, DTO.

Контакти

Для питань: support@autoria.clone.