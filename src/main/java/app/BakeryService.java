package app;

import java.util.*;

public class BakeryService {

    private final Map<Integer, Buns> bunsStorage = new HashMap<>();
    private final Map<Integer, Buyer> buyersStorage = new HashMap<>();

    private int nextBunId = 1;
    private int nextBuyerId = 1;

    public BakeryService() {
        createBun(67);
        createBun(67);
        createBun(75);
        createBuyer("standart", 200);
        createBuyer("VIP", 500);
        createBuyer("admin", 0);
    }


    public Map<String, Object> getAllBunsInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("stockCount", bunsStorage.size()); // Остаток на складе
        info.put("buns", new ArrayList<>(bunsStorage.values()));
        return info;
    }

    public Buns getBunById(int id) {
        Buns bun = bunsStorage.get(id);
        if (bun == null) {
            throw new NoSuchElementException("Булочка с ID " + id + " не найдена");
        }
        return bun;
    }

    public Buns createBun(int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Цена булочки должна быть больше 0");
        }
        Buns bun = new Buns(nextBunId++, price);
        bunsStorage.put(bun.id, bun);
        return bun;
    }

    public Buns updateBunPrice(int id, int newPrice) {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Новая цена должна быть больше 0");
        }
        Buns bun = getBunById(id);
        bun.price = newPrice;
        return bun;
    }

    public void deleteBun(int id) {
        if (bunsStorage.remove(id) == null) {
            throw new NoSuchElementException("Булочка с ID " + id + " не найдена");
        }
    }

    public void checkAdminAccess(Integer adminId) {
        if (adminId == null) return;
        Buyer user = getBuyerById(adminId);
        if (!"admin".equalsIgnoreCase(user.status)) {
            throw new IllegalArgumentException("Доступ запрещен: пользователь с ID " + adminId + " не является admin");
        }
    }

    public List<Buyer> getAllBuyers() {
        return new ArrayList<>(buyersStorage.values());
    }

    public Buyer getBuyerById(int id) {
        Buyer buyer = buyersStorage.get(id);
        if (buyer == null) {
            throw new NoSuchElementException("Покупатель с ID " + id + " не найден");
        }
        return buyer;
    }

    public Buyer createBuyer(String status, int wallet) {
        if (wallet < 0) {
            throw new IllegalArgumentException("Баланс кошелька не может быть отрицательным");
        }
        String validStatus = (status == null || status.isBlank()) ? "standart" : status;
        Buyer buyer = new Buyer(nextBuyerId++, validStatus, wallet);
        buyersStorage.put(buyer.id, buyer);
        return buyer;
    }

    public Buyer updateBuyer(int id, String newStatus, Integer newWallet) {
        Buyer buyer = getBuyerById(id);
        if (newStatus != null && !newStatus.isBlank()) {
            buyer.status = newStatus;
        }
        if (newWallet != null) {
            if (newWallet < 0) {
                throw new IllegalArgumentException("Баланс не может быть отрицательным");
            }
            buyer.wallet = newWallet;
        }
        return buyer;
    }

    public void deleteBuyer(int id) {
        if (buyersStorage.remove(id) == null) {
            throw new NoSuchElementException("Покупатель с ID " + id + " не найден");
        }
    }

    public Map<String, Object> buyBuns(int buyerId, Integer bunId, Integer quantity) {

        Buyer buyer = getBuyerById(buyerId);
        
        if ("admin".equalsIgnoreCase(buyer.status)) {
            throw new IllegalArgumentException("Администратор не может покупать булочки!");
        }
        List<Buns> bunsToBuy = new ArrayList<>();

        if (bunId != null) {
            bunsToBuy.add(getBunById(bunId));
        } 

        else {
            int count = (quantity == null) ? 1 : quantity;
            if (count <= 0) {
                throw new IllegalArgumentException("Количество булочек должно быть больше 0");
            }
            if ("standart".equalsIgnoreCase(buyer.status) && count > 5) {
                throw new IllegalArgumentException("Обычный клиент (standart) не может купить больше 5 булочек за раз");
            }
            if (bunsStorage.size() < count) {
                throw new IllegalArgumentException("На складе недостаточно булочек! Осталось: " + bunsStorage.size());
            }
            Iterator<Buns> iterator = bunsStorage.values().iterator();
            for (int i = 0; i < count; i++) {
                bunsToBuy.add(iterator.next());
            }
        }

        int totalPrice = 0;
        for (Buns b : bunsToBuy) {
            totalPrice += b.price;
        }
        if ("VIP".equalsIgnoreCase(buyer.status)) {
            totalPrice = (int) (totalPrice * 0.8);
        }

        if (buyer.wallet < totalPrice) {
            throw new IllegalArgumentException("Недостаточно средств! Нужно: " + totalPrice + ", на счете: " + buyer.wallet);
        }

        buyer.wallet -= totalPrice;
        for (Buns b : bunsToBuy) {
            bunsStorage.remove(b.id);
        }

        Map<String, Object> receipt = new LinkedHashMap<>();
        receipt.put("message", "Покупка успешно совершена!");
        receipt.put("buyerId", buyer.id);
        receipt.put("buyerStatus", buyer.status);
        receipt.put("boughtCount", bunsToBuy.size());
        receipt.put("totalPrice", totalPrice);
        receipt.put("remainingWallet", buyer.wallet);
        receipt.put("remainingStock", bunsStorage.size());
        return receipt;
    }
}
