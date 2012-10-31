package shop;

/**
 * item info
 * User: Silvan
 * Date: 31.10.12
 * Time: 16:48
 */
public class Item {
    private String title;
    private double price;
    private int quantity;
    private ItemType type;

    public Item() {}

    public Item(String title, double price, int quantity, ItemType type) {
        setTitle(title);
        setPrice(price);
        setQuantity(quantity);
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Includes validation check
     * @param title
     */
    public void setTitle(String title) {
        if (title == null || title.length() == 0 || title.length() > 32)
            throw new IllegalArgumentException("Illegal title");

        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Includes validation check
     * @param price
     */
    public void setPrice(double price) {
        if (price < 0.01)
            throw new IllegalArgumentException("Illegal price");

        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Includes validation check
     * @param quantity
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Illegal quantity");

        this.quantity = quantity;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}