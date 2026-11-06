package app;
public class Buns {
    public int id;
    public int price;
    public int quantity;

    public Buns(int id, int price, int quantity) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
    }

    public boolean IsAvailability(int WantBuns) {
        return quantity >= WantBuns;
    }
}

