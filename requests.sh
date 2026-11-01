#!/usr/bin/env bash
#!/bin/bash

echo "проверочный тест 1: покупатель просто заходит в пекарню (Просмотр меню через GET) "
curl -X GET "http://localhost:8080/buns"
echo -e "\n"

echo "проверочный тест 2: обычный клиент покупает 2 булочки в пределах лимита  "
curl -X POST "http://localhost:8080/buns?quantity=2&status=standart&wallet=200"
echo -e "\n"

echo "проверочный тест 3: обычный клиент пытается наглеть и купить сразу 6 булочек (Ошибка лимита) "
curl -X POST "http://localhost:8080/buns?quantity=6&status=standart&wallet=500"
echo -e "\n"

echo "проверочный тест 4: вип покупает 6 булочек (Успешно, лимита нет + скидка 20%) "
curl -X POST "http://localhost:8080/buns?quantity=6&status=VIP&wallet=500"
echo -e "\n"

echo "проверочный тест 5: проверка на стойкость (Запрос без параметров, сервер не должен упасть) "
curl -X POST "http://localhost:8080/buns"
echo -e "\n"

echo "проверочный тест 6: финальный осмотр остатков на складе пекарни "
curl -X GET "http://localhost:8080/buns"
echo -e "\n"

