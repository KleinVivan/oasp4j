package io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public enum ProcessKeyName {
  STANDARD_ORDER_PROCESS("StandardOrderProcess");

  private final String keyName;

  private ProcessKeyName(String keyName) {
    this.keyName = keyName;
  }

  public String getKeyName() {

    return this.keyName;
  }
}
