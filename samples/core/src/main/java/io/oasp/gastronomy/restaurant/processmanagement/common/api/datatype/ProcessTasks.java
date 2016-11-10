package io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public enum ProcessTasks {
  USERTASK_ACCEPTORDER("UserTask_acceptOrder"), USERTASK_UPDATEPREPAREDORDER(
      "UserTask_UpdatePreparedOrder"), USERTASK_UPDATESERVEDORDER("UserTask_UpdateServedOrder");

  private final String taskName;

  private ProcessTasks(String taskName) {
    this.taskName = taskName;
  }

  public String getTaskName() {

    return this.taskName;
  }
}
