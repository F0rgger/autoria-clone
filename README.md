# Autoria Clone

## 1. Розгортання бази даних PostgreSQL у Docker

Виконайте команду для запуску контейнера PostgreSQL:

```
docker run --name postgres-autoria -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=mysecurepassword123 -e POSTGRES_DB=autoria_clone -p 5432:5432 -d postgres:latest
```

## 2. Налаштування підключення до бази даних у IDE

У вашій IDE створіть нове підключення до бази даних із наступними параметрами:
- **Назва:** PostgreSQL - autoria_clone
- **Хост:** localhost
- **Порт:** 5432
- **Користувач:** postgres
- **Пароль:** mysecurepassword123
- **База даних:** autoria_clone

## 3. Інтеграція з Postman

- Відкрийте Postman.
- Імпортуйте файл `autoria.postman_collection.json` , який знаходиться у корені репозиторію.

## 4. Вимоги до Java

- Для запуску проекту потрібна **Java 17**

---
