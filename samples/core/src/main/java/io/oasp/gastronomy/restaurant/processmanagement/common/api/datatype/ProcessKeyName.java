package io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype;

/**
 * @author vmuschter
 *
 *         This class contains the process definition keys of the used process models
 */
public enum ProcessKeyName {
  STANDARD_ORDER_PROCESS("RestaurantOrderProcessEngine");

  private final String keyName;

  private ProcessKeyName(String keyName) {
    this.keyName = keyName;
  }

  public String getKeyName() {

    return this.keyName;
  }
}
