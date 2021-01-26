package uk.co.emiratesfinancial.test.orderbook;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderBookTest {

  @Test
  void testBidOrdersPrioritizedCorrectlyWithHighestBidFirstInLevelTimeOrder() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'B', 10);
    Order order2 = new Order(2, 4.7, 'B', 10);
    Order order3 = new Order(3, 3.8, 'B', 10);
    Order order4 = new Order(4, 3.8, 'B', 10);


    // When
    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);
    List<Order> results = orderBook.getAllOrdersByLevelTimeOrder('B');

    // Then
    assertThat(results).containsExactly(order2, order1, order3, order4);
    System.out.println(results);
  }

  @Test
  void testOfferOrdersPrioritizedCorrectlyWithLowestOfferFirstInLevelTimeOrder() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'O', 10);
    Order order2 = new Order(2, 4.7, 'O', 10);
    Order order3 = new Order(3, 3.8, 'O', 10);
    Order order4 = new Order(4, 3.8, 'O', 10);


    // When
    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);
    List<Order> results = orderBook.getAllOrdersByLevelTimeOrder('O');

    // Then
    assertThat(results).containsExactly(order3, order4, order1, order2);
    System.out.println(results);
  }


  @Test
  void testInvalidSideReturnsEmptyResults() {
    // Given
    OrderBook orderBook = new OrderBook();

    // When
    orderBook.addOrder(null);
    List<Order> results = orderBook.getAllOrdersByLevelTimeOrder('O');

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void testCanRemoveOfferOrderByID() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'O', 10);
    Order order2 = new Order(2, 4.7, 'O', 10);
    Order order3 = new Order(3, 3.8, 'O', 10);
    Order order4 = new Order(4, 3.8, 'O', 10);

    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    // When
    orderBook.removeOrderById(4);

    // Then
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('O')).containsExactly(order3, order1, order2);
  }

  @Test
  void testCanRemoveBidOrderByID() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'O', 10);
    Order order2 = new Order(2, 4.7, 'B', 10);
    Order order3 = new Order(3, 3.8, 'B', 10);
    Order order4 = new Order(4, 3.8, 'B', 10);

    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    // When
    orderBook.removeOrderById(4);

    // Then
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('B')).containsExactly(order2, order3);
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('O')).hasSize(1);
  }

  @Test
  void testRemoveOrderByInvalidIdDoesntAffectOrderBook() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'O', 10);
    Order order2 = new Order(2, 4.7, 'B', 10);
    Order order3 = new Order(3, 3.8, 'B', 10);
    Order order4 = new Order(4, 3.8, 'B', 10);

    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    // When
    orderBook.removeOrderById(100);

    // Then
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('B')).containsExactly(order2, order3, order4);
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('O')).containsExactly(order1);
  }


  @Test
  void testCanModifyExistingOrderSizeById() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'B', 10);
    Order order2 = new Order(2, 4.7, 'B', 10);
    Order order3 = new Order(3, 3.8, 'B', 10);
    Order order4 = new Order(4, 3.8, 'B', 10);


    // When
    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    orderBook.findByIdAndModifySize(3, 15);

    // Then
    Order expectedOrder3 = new Order(3, 3.8, 'B', 15);
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('B')).contains(expectedOrder3);
    assertThat(orderBook.getAllOrdersByLevelTimeOrder('B')).doesNotContain(order3);
  }

  @Test
  void testCanRetreiveOrdersByLevel() {
    // Given
    OrderBook orderBook = new OrderBook();

    Order order1 = new Order(1, 4.4, 'B', 10);
    Order order2 = new Order(2, 4.7, 'B', 10);
    Order order3 = new Order(3, 3.8, 'B', 10);
    Order order4 = new Order(4, 3.8, 'B', 10);


    // When
    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    // check 1st level
    Double actual = orderBook.getPriceByLevel('B', 1);

    // Then
    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(4.7);

    // check 2nd level
    actual = orderBook.getPriceByLevel('B', 2);

    // Then
    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(4.4);

    // check 3rd level
    actual = orderBook.getPriceByLevel('B', 3);

    // Then
    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(3.8);

    // check invalid level
    actual = orderBook.getPriceByLevel('B', 4);

    // Then
    assertThat(actual).isNull();
  }

  @Test
  void testCanRetreiveSizeByLevel() {
    // Given
    OrderBook orderBook = new OrderBook();

    Order order1 = new Order(1, 4.4, 'O', 10);
    Order order2 = new Order(2, 4.7, 'O', 10);
    Order order3 = new Order(3, 3.8, 'O', 10);
    Order order4 = new Order(4, 3.8, 'O', 10);


    // When
    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    // check 1st level
    Long actual = orderBook.getSizeByLevel('O', 1);

    // Then
    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(20);

    // check 3rs level
    actual = orderBook.getSizeByLevel('O', 2);

    // Then
    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(10);

  }


  @Test
  void testRetreiveSizeByLevelNullWhenOrderBookEmpty() {
    // Given
    OrderBook orderBook = new OrderBook();

    // When

    Long actual = orderBook.getSizeByLevel('O', 1);

    // Then
    assertThat(actual).isNull();
  }

  @Test
  void testThrowsExceptionForAnInvalidCharSide() {
    // Given
    OrderBook orderBook = new OrderBook();
    Order order1 = new Order(1, 4.4, 'B', 10);
    Order order2 = new Order(2, 4.7, 'B', 10);
    Order order3 = new Order(3, 3.8, 'B', 10);
    Order order4 = new Order(4, 3.8, 'B', 10);

    orderBook.addOrder(order1);
    orderBook.addOrder(order2);
    orderBook.addOrder(order3);
    orderBook.addOrder(order4);

    // When
    assertThrows(IllegalArgumentException.class, () ->orderBook.getAllOrdersByLevelTimeOrder('L'));

  }

  @Test
  void testOrderEqualsAndHashcode() {
    // Given
    Order order1 = new Order(1, 4.4, 'B', 10);
    Order order2 = new Order(1, 4.4, 'B', 10);

    // When / then
    assertTrue(order1.equals(order2));
    assertTrue(order1.hashCode() == order2.hashCode());
  }

}