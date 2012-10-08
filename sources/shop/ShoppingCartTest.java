package shop;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import shop.ShoppingCart;
import shop.ShoppingCart.ItemType;

import static org.junit.Assert.*;

/**
 * User: roman.gural
 * Date: 08.10.12
 * Time: 14:21
 */
public class ShoppingCartTest {

    private String testCase4FormatTicket =
            "# Item                          Price Quan. Discount   Total \n" +
                    "------------------------------------------------------------\n" +
                    "1 Apple                          $.99     5        -   $4.95 \n" +
                    "2 Banana                       $20.00     4      50%  $40.00 \n" +
                    "3 A long piece of toilet paper $17.20     1      70%   $5.16 \n" +
                    "4 Nails                         $2.00   500      50% $500.00 \n" +
                    "------------------------------------------------------------\n" +
                    "4                                                    $550.11 ";

    private ShoppingCart cart;

    @Before
    public void prepare() {
        cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
    }

    @After
    public void clean() {
        cart = null;
    }

    /**
     * @verifies return lines of cart items description
     * @see ShoppingCart#formatTicket()
     */
    @Test
    public void formatTicket_shouldReturnLinesOfCartItemsDescription() throws Exception {
        assertEquals("Format ticket with 4 Items", testCase4FormatTicket, cart.formatTicket());
    }

    /**
     * @verifies return "No items." if cart is empty
     * @see ShoppingCart#formatTicket()
     */
    @Test
    public void formatTicket_shouldReturnNoItemsIfCartIsEmpty() throws Exception {
        assertEquals("Format ticket of empty cart", "No items.", new ShoppingCart().formatTicket());
    }

    /**
     * @verifies throw IllegalArgumentException if title is null or empty or longer than 32 chars
     * @see ShoppingCart#addItem(String, double, int, shop.ShoppingCart.ItemType)
     */
    @Test(expected = IllegalArgumentException.class)
    public void addItem_shouldThrowIllegalArgumentExceptionIfTitleIsNullOrEmptyOrLongerThan32Chars() throws Exception {
        cart.addItem(null, 0.99, 5, ItemType.NEW);
        cart.addItem("", 0.99, 5, ItemType.NEW);
        cart.addItem("tooooooooooooooooooooooooooo long", 0.99, 5, ItemType.NEW);
    }

    /**
     * @verifies throw IllegalArgumentException if price is less than penny
     * @see ShoppingCart#addItem(String, double, int, shop.ShoppingCart.ItemType)
     */
    @Test(expected = IllegalArgumentException.class)
    public void addItem_shouldThrowIllegalArgumentExceptionIfPriceIsLessThanPenny() throws Exception {
        cart.addItem("Good title", 0.001, 5, ItemType.NEW);
    }

    /**
     * @verifies throw IllegalArgumentException if quantity is equals or less than zero
     * @see ShoppingCart#addItem(String, double, int, shop.ShoppingCart.ItemType)
     */
    @Test(expected = IllegalArgumentException.class)
    public void addItem_shouldThrowIllegalArgumentExceptionIfQuantityIsEqualsOrLessThanZero() throws Exception {
        cart.addItem("Good title", 0.001, 0, ItemType.NEW);
    }

    /**
     * @verifies return zero for NEW item
     * @see ShoppingCart#calculateDiscount(shop.ShoppingCart.ItemType, int)
     */
    @Test
    public void calculateDiscount_shouldReturnZeroForNEWItem() throws Exception {
        assertEquals(0, ShoppingCart.calculateDiscount(ItemType.NEW, 100));
    }

    /**
     * @verifies return fifty for SECOND_FREE if quantity bigger than 1
     * @see ShoppingCart#calculateDiscount(shop.ShoppingCart.ItemType, int)
     */
    @Test
    public void calculateDiscount_shouldReturnFiftyForSECOND_FREEIfQuantityBiggerThan1() throws Exception {
        assertEquals(50, ShoppingCart.calculateDiscount(ItemType.SECOND_FREE, 100));
    }

    /**
     * @verifies return zero for SECOND_FREE if quantity equals zero
     * @see ShoppingCart#calculateDiscount(shop.ShoppingCart.ItemType, int)
     */
    @Test
    public void calculateDiscount_shouldReturnZeroForSECOND_FREEIfQuantityEqualsZero() throws Exception {
        assertEquals(0, ShoppingCart.calculateDiscount(ItemType.SECOND_FREE, 0));
    }

    /**
     * @verifies return seventy for SALE
     * @see ShoppingCart#calculateDiscount(shop.ShoppingCart.ItemType, int)
     */
    @Test
    public void calculateDiscount_shouldReturnSeventyForSALE() throws Exception {
        assertEquals(70, ShoppingCart.calculateDiscount(ItemType.SALE, 100));
    }

    /**
     * @verifies return per each 10 items not NEW addition 1
     * @see ShoppingCart#calculateDiscount(shop.ShoppingCart.ItemType, int)
     */
    @Test
    public void calculateDiscount_shouldReturnPerEach10ItemsNotNEWAddition1() throws Exception {
        assertEquals(60, ShoppingCart.calculateDiscount(ItemType.REGULAR, 600));
    }

    /**
     * @verifies return per each 10 items not NEW addition 1 but not more than 80
     * @see ShoppingCart#calculateDiscount(shop.ShoppingCart.ItemType, int)
     */
    @Test
    public void calculateDiscount_shouldReturnPerEach10ItemsNotNEWAddition1ButNotMoreThan80() throws Exception {
        assertEquals(80, ShoppingCart.calculateDiscount(ItemType.REGULAR, 1000));
    }
}
