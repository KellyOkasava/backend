package app;

public class Buyer {
    public String id;
    public String status;
    public int wallet;

    public Buyer(String id, String status, int wallet) {
        this.id = id;
        this.status = status;
        this.wallet = wallet;
    }

    public boolean IsEnoughMoney(int price) {
        return wallet >= price;
    }
}
