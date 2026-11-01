package app;
public class Buyer {
    public String status;
    public int wallet;

    public Buyer(String status, int wallet) {
        this.status = status;
        this.wallet = wallet;
    }

    public boolean IsEnoughMoney(int price) {
        return wallet >= price;
    }
}
