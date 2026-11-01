package app;
public class Buns {
    public int price;
    public int quantity;

    public Buns(int price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public boolean IsAvailability(int WantBuns) {
        return quantity >= WantBuns;
    }
}
