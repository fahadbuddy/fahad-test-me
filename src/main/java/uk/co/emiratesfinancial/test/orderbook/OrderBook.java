package uk.co.emiratesfinancial.test.orderbook;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import static java.lang.String.format;


/**
 * A limit order book stores customer orders on a price time priority basis.
 * The highest bid and lowest off􏰀er are considered "best" with all other orders stacked in price levels behind.
 * In this test, the best order is considered to be at level 1.
 */
public class OrderBook {

  // Keep price sorted map.
  Map<Double, Map<Long, Order>> bidOrdersMap = new ConcurrentSkipListMap<>(Comparator.comparingDouble(o -> (double) o)
                                                                                .reversed());

  // keeping this separate map to allow for faster updates/removals
  // having a separate id2OrdersMap allows us to remove/update orders with the order id.
  // We could have kept the single ordersMap above, however the above is keyed by
  // order price, and it would have required a linear traversal to identify the record to
  // be removed/updated. This method allows us to get the order to update in amortized O(1) time (as its using a map).
  Map<Long, Order> bidOrderId2OrdersMap = new ConcurrentHashMap<>();

  Map<Double, Map<Long, Order>> offerOrdersMap = new ConcurrentSkipListMap<>(Comparator.comparingDouble(o -> (double) o));
  Map<Long, Order> offerOrderId2OrdersMap = new ConcurrentHashMap<>();

  // Orders for a given price are inserted in sequence by which addOrder method is called, maintaining Price,
  // Time priority. Here the time order is implicitly determined by whichever thread is able to synchronize.
  // If there was an explicit order time on Order class, we could use a sorted list based on order time to maintain the
  // time ordering.
  public void addOrder(final Order order) {
    if (order == null) return;


    synchronized (this) {
      Map<Long, Order> orders = getOrdersMap(order.getSide()).getOrDefault(order.getPrice(), new LinkedHashMap<>());

      orders.put(order.getId(), order);

      getOrdersMap(order.getSide()).put(order.getPrice(), orders);

      getOrderId2OrdersMap(order.getSide()).put(order.getId(), order);
    }

  }


  public List<Order> getAllOrdersByLevelTimeOrder(final char side) {

    return getOrdersMap(side).entrySet()
                             .stream()
                             .flatMap(e -> e.getValue().values().stream())
                             .collect(Collectors.toUnmodifiableList());
  }

  public void removeOrderById(final long orderId) {

    Character side = findSideByOrderId(orderId);

    if (side == null) return;

    Map<Double, Map<Long, Order>> ordersMap = getOrdersMap(side);
    Map<Long, Order> id2OrdersMap = getOrderId2OrdersMap(side);

    if (ordersMap != null && id2OrdersMap.get(orderId) != null) {

      Order orderToRemove = id2OrdersMap.get(orderId);

      synchronized (this) {
          ordersMap.get(orderToRemove.getPrice()).remove(orderId);
          id2OrdersMap.remove(orderId);
      }
    }

  }


  public Double getPriceByLevel(final char side, final int level) {

    if (level < 1 || level > getOrdersMap(side).size()) return null;

    return getOrdersMap(side).entrySet()
                            .stream()
                            .skip(level-1) // subtract 1 to reflect index starting from 0
                            .map(Map.Entry::getKey)
                            .findFirst().orElse(null);
  }

  public Long getSizeByLevel(final char side, final int level) {

    Double price = getPriceByLevel(side, level);

    if (price == null) return null;

    return getOrdersMap(side).get(price).values().stream().map(Order::getSize).reduce(0L, Long::sum);
  }

  public void findByIdAndModifySize(final long orderId, final int newSize) {

    Character side = findSideByOrderId(orderId);

    if (side == null) return;

    Map<Long, Order> id2OrdersMap = getOrderId2OrdersMap(side);
    Map<Double, Map<Long, Order>> ordersMap = getOrdersMap(side);

    Order orderToUpdate = id2OrdersMap.get(orderId);

    // Need to synchronize the write as the Map is an instance of LinkedHashMap which is not thread-safe
    synchronized (this) {
      Order updatedOrder = new Order(orderId, orderToUpdate.getPrice(), orderToUpdate.getSide(), newSize);
      id2OrdersMap.put(orderId, updatedOrder);
      ordersMap.get(orderToUpdate.getPrice()).put(orderId, updatedOrder);
    }


  }

  private Character findSideByOrderId(final long orderId) {
    if (getOrderId2OrdersMap('B').containsKey(orderId)) {
      return 'B';
    }

    if (getOrderId2OrdersMap('O').containsKey(orderId)) {
      return 'O';
    }

    return null;
  }

  private Map<Long, Order> getOrderId2OrdersMap(final char side) {

    if (side == 'B') {
      return bidOrderId2OrdersMap;
    }

    if (side == 'O') {
      return offerOrderId2OrdersMap;
    }

      throw new IllegalArgumentException(format("side %s not recognized. Should be either B (bid) or O (offer)", side));
  }

  private Map<Double, Map<Long, Order>> getOrdersMap(final char side) {

    if (side == 'B') {
      return bidOrdersMap;
    }

    if (side == 'O') {
      return offerOrdersMap;
    }

    throw new IllegalArgumentException(format("side %s not recognized. Should be either B (bid) or O (offer)", side));
  }



}

