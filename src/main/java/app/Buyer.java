package app;

public class Buyer {
    public int id;
    public String status;
    public int wallet;

    public Buyer() {}

    public Buyer(int id, String status, int wallet) {
        this.id = id;
        this.status = status;
        this.wallet = wallet;
    }
}
