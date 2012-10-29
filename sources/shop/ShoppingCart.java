package shop;

import java.util.*;
import java.text.*;

/**
 * Containing items and calculating price.
 */
public class ShoppingCart {
    public static enum ItemType {NEW, REGULAR, SECOND_FREE, SALE};

    /**
     * Tests all class methods.
     */
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
        System.out.println(cart.formatTicket());
    }

    /**
     * Adds new item.
     *
     * @param title    item title 1 to 32 symbols
     * @param price    item price in USD, > 0
     * @param quantity item quantity, from 1
     * @param type     item type
     * @throws IllegalArgumentException if some value is wrong
     * @should throw IllegalArgumentException if title is null or empty or longer than 32 chars
     * @should throw IllegalArgumentException if price is less than penny
     * @should throw IllegalArgumentException if quantity is equals or less than zero
     */
    public void addItem(String title, double price, int quantity, ItemType type) {
        items.add(new Item(title, price, quantity, type));
    }

    /**
     * Formats shopping price.
     *
     * @return string as lines, separated with \n,
     *         first line:   # Item                   Price Quan. Discount      Total
     *         second line: ---------------------------------------------------------
     *         next lines:  NN Title                 $PP.PP    Q       DD%     $TT.TT
     *                       1 Some title              $.30    2         -       $.60
     *                       2 Some very long       $100.00    1       50%     $50.00
     *                       ...
     *                      31 Item 42              $999.00 1000         - $999000.00
     *         end line:    ---------------------------------------------------------
     *         last line:   31                                             $999050.60
     *         <p/>
     *         if no items in cart returns "No items." string.
     * @should return lines of cart items description
     * @should return "No items." if cart is empty
     * TODO add tests for big length of columns for all ones
     * TODO add test for centering
     */
    public String formatTicket() {
        
        if (items.size() == 0)
            return "No items.";

        List<String[]> lines = new ArrayList<String[]>();

        // TODO replace array with object
        // work with table consists of two ways of interaction: filling and formatting
        String[] header = {"#", "Item", "Price", "Quan.", "Discount", "Total"};
        int[] align = new int[]{1, -1, 1, 1, 1, 1};

        // formatting each line
        String[] footer = formatItemLines(lines);

        // formatting table

        // column max length
        int[] width = new int[]{0, 0, 0, 0, 0, 0};
        for (String[] line : lines)
            for (int i = 0; i < line.length; i++)
                width[i] = (int) Math.max(width[i], line[i].length());
        for (int i = 0; i < header.length; i++)
            width[i] = (int) Math.max(width[i], header[i].length());
        for (int i = 0; i < footer.length; i++)
            width[i] = (int) Math.max(width[i], footer[i].length());

        // line length
        int lineLength = width.length - 1;
        for (int w : width)
            lineLength += w;

        StringBuilder sb = new StringBuilder();

        // header
        for (int i = 0; i < header.length; i++)
            appendFormatted(sb, header[i], align[i], width[i]);
        sb.append("\n");

        sb.append(printSeparator(lineLength));

        // lines
        for (String[] line : lines) {
            for (int i = 0; i < line.length; i++)
                appendFormatted(sb, line[i], align[i], width[i]);
            sb.append("\n");
        }
        
        if (lines.size() > 0)
            sb.append(printSeparator(lineLength));

        // footer
        for (int i = 0; i < footer.length; i++)
            appendFormatted(sb, footer[i], align[i], width[i]);

        return sb.toString();
    }

    private String printSeparator(int lineLength) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lineLength; i++)
            result.append("-");
        result.append("\n");
        return result.toString();
    }

    private String[] formatItemLines(List<String[]> lines) {
        double total = 0.00;
        int index = 0;
        for (Item item : items) {
            int discount = calculateDiscount(item.type, item.quantity);

            lines.add(new String[]{
                    String.valueOf(++index),
                    item.title,
                    MONEY.format(item.price),
                    String.valueOf(item.quantity),
                    (discount == 0) ? "-" : (String.valueOf(discount) + "%"),
                    MONEY.format(calculateItemTotal(item))
            });
            total += calculateItemTotal(item);
        }
        return new String[]{String.valueOf(index), "", "", "", "",
                MONEY.format(total)};
    }
    
    private static double calculateItemTotal(Item item) {
        return item.getPrice() * item.getQuantity() *
                (100.00 - calculateDiscount(item.getType(), item.getQuantity())) / 100.00;
    }

    // --- private section -----------------------------------------------------

    private static final NumberFormat MONEY;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        MONEY = new DecimalFormat("$#.00", symbols);
    }

    /**
     * Appends to sb formatted value.
     * Trims string if its length > width.
     *
     * @param align -1 for align left, 0 for center and +1 for align right.
     */
    public static void appendFormatted(StringBuilder sb, String value, int align, int width) {
        if (value.length() > width)
            value = value.substring(0, width);
        int before = (align == 0)
                ? (width - value.length()) / 2
                : (align == -1) ? 0 : width - value.length();
        int after = width - value.length() - before;
        while (before-- > 0)
            sb.append(" ");
        sb.append(value);
        while (after-- > 0)
            sb.append(" ");
        sb.append(" ");
    }

    /**
     * Calculates item's discount.
     * For NEW item discount is 0%;
     * For SECOND_FREE item discount is 50% if quantity > 1  (if quantity equals zero item will not pass validation, 0_o)
     * For SALE item discount is 70%
     * For each full 10 not NEW items item gets additional 1% discount,
     * but not more than 80% total
     *
     * @param type
     * @param quantity
     * @return item's discount
     * @should return zero for NEW item
     * @should return fifty for SECOND_FREE if quantity bigger than 1
     * @should return zero for SECOND_FREE if quantity equals zero
     * @should return seventy for SALE
     * @should return per each 10 items not NEW addition 1
     * @should return per each 10 items not NEW addition 1 but not more than 80
     */
    public static int calculateDiscount(ItemType type, int quantity) {
        int discount = 0;
        switch (type) {
            case NEW:
                return 0;

            case REGULAR:
                discount = 0;
                break;

            case SECOND_FREE:
                if (quantity > 1)
                    discount = 50;
                break;

            case SALE:
                discount = 70;
                break;
        }
        if (discount < 80) {
            discount += quantity / 10;
            if (discount > 80)
                discount = 80;
        }

        return discount;
    }

    /**
     * item info
     */
    private static class Item {
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

    /**
     * Container for added items
     */
    private List<Item> items = new ArrayList<Item>();
}















