package uk.co.emiratesfinancial.test.orderbook;

import java.util.Objects;

public class Order {
  private final long id;
  private final double price;
  private final char side;
  private final long size;

  public Order(final long id, final double price, final char side, final long size) {

    this.id = id;
    this.price = price;
    this.side = side;
    this.size = size;
  }

  public long getId() {

    return id;
  }

  public double getPrice() {

    return price;
  }

  public char getSide() {

    return side;
  }

  public long getSize() {

    return size;
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Order order = (Order) o;
    return id == order.id && Double.compare(order.price, price) == 0 && side == order.side && size == order.size;
  }

  @Override
  public int hashCode() {

    return Objects.hash(id, price, side, size);
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder("Order{");
    sb.append("id=")
      .append(id);
    sb.append(", price=")
      .append(price);
    sb.append(", side=")
      .append(side);
    sb.append(", size=")
      .append(size);
    sb.append('}');
    return sb.toString();
  }
}
