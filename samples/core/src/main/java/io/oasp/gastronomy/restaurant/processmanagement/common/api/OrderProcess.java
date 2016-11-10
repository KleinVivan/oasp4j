package io.oasp.gastronomy.restaurant.processmanagement.common.api;

import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public interface OrderProcess {

  /**
   * @return orderProcessId
   */
  Long getOrderProcessId();

  /**
   * @return orderPositionId
   */
  Long getOrderPositionId();

  /**
   * @param orderPositionId new value of orderPositionId.
   */
  void setOrderPositionId(Long orderPositionId);

  /**
   * @return orderPositionState
   */
  OrderPositionState getOrderPositionState();

  /**
   * @param orderPositionState new value of orderPositionState.
   */
  void setOrderPositionState(OrderPositionState orderPositionState);

  /**
   * @param orderProcessId new value of orderProcessId.
   */
  void setOrderProcessId(Long orderProcessId);

}