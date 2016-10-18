package io.oasp.gastronomy.restaurant.processmanagement.logic.api.to;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.OrderProcess;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public class OrderProcessEto implements OrderProcess {

  private Long orderProcessId;

  private Long orderPositionId;

  private OrderPositionState orderPositionState;

  @Override
  public Long getOrderProcessId() {

    return this.orderProcessId;
  }

  @Override
  public Long getOrderPositionId() {

    return this.orderPositionId;
  }

  @Override
  public void setOrderPositionId(Long orderPositionId) {

    this.orderPositionId = orderPositionId;
  }

  @Override
  public OrderPositionState getOrderPositionState() {

    return this.orderPositionState;
  }

  @Override
  public void setOrderPositionState(OrderPositionState orderPositionState) {

    this.orderPositionState = orderPositionState;
  }

  @Override
  public void setOrderProcessId(Long orderProcessId) {

    this.orderProcessId = orderProcessId;
  }

}
