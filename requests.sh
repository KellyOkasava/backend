#!/usr/bin/env bash
echo "=== Тест 1: Просмотр витрины и остатка булочек (GET /buns) ==="
curl -s -X GET "http://localhost:8080/buns"
echo -e "\n"

echo "=== Тест 2: Просмотр списка покупателей (GET /buyers) ==="
curl -s -X GET "http://localhost:8080/buyers"
echo -e "\n"

echo "=== Тест 3: Админ (ID=3) добавляет новую булочку за 90 руб. (POST /buns) ==="
curl -s -X POST "http://localhost:8080/buns?adminId=3" \
  -H "Content-Type: application/json" \
  -d '{"price": 90}'
echo -e "\n"

echo "=== Тест 4: Обычный клиент (ID=1) пытается добавить булочку (Ожидается ошибка 400) ==="
curl -s -X POST "http://localhost:8080/buns?adminId=1" \
  -H "Content-Type: application/json" \
  -d '{"price": 90}'
echo -e "\n"

echo "=== Тест 5: Админ (ID=3) меняет цену булочки ID=1 на 55 руб. (PUT /buns/1) ==="
curl -s -X PUT "http://localhost:8080/buns/1?adminId=3" \
  -H "Content-Type: application/json" \
  -d '{"price": 55}'
echo -e "\n"

echo "=== Тест 6: Админ (ID=3) удаляет булочку ID=2 (DELETE /buns/2) ==="
curl -s -X DELETE "http://localhost:8080/buns/2?adminId=3"
echo -e "\n"

echo "=== Тест 7: Создание нового VIP-покупателя с балансом 1000 руб. (POST /buyers) ==="
curl -s -X POST "http://localhost:8080/buyers" \
  -H "Content-Type: application/json" \
  -d '{"status": "VIP", "wallet": 1000}'
echo -e "\n"

echo "=== Тест 8: Пополнение кошелька покупателя ID=1 до 500 руб. (PUT /buyers/1) ==="
curl -s -X PUT "http://localhost:8080/buyers/1" \
  -H "Content-Type: application/json" \
  -d '{"status": "standart", "wallet": 500}'
echo -e "\n"

echo "=== Тест 9: Обычный покупатель (ID=1) покупает 2 булочки (POST /buy) ==="
curl -s -X POST "http://localhost:8080/buy" \
  -H "Content-Type: application/json" \
  -d '{"buyerId": 1, "quantity": 2}'
echo -e "\n"

echo "=== Тест 10: Обычный покупатель (ID=1) пытается купить 6 булочек (Ошибка лимита > 5) ==="
curl -s -X POST "http://localhost:8080/buy" \
  -H "Content-Type: application/json" \
  -d '{"buyerId": 1, "quantity": 6}'
echo -e "\n"

echo "=== Тест 11: VIP-покупатель (ID=2) покупает булочку со скидкой 20% (POST /buy) ==="
curl -s -X POST "http://localhost:8080/buy" \
  -H "Content-Type: application/json" \
  -d '{"buyerId": 2, "quantity": 1}'
echo -e "\n"

echo "=== Тест 12: Проверка валидации — покупка отрицательного количества (Ошибка 400) ==="
curl -s -X POST "http://localhost:8080/buy" \
  -H "Content-Type: application/json" \
  -d '{"buyerId": 1, "quantity": -2}'
echo -e "\n"

echo "=== Тест 13: Удаление покупателя ID=4 (DELETE /buyers/4) ==="
curl -s -X DELETE "http://localhost:8080/buyers/4"
echo -e "\n"

echo "=== Тест 14: Финальный осмотр остатков на складе (GET /buns) ==="
curl -s -X GET "http://localhost:8080/buns"
echo -e "\n"
