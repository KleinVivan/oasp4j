package io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype;

/**
 * @author vmuschter
 */
public enum ProcessTasks {
  USERTASK_ACCEPTORDER("UserTask_AcceptOrder"), USERTASK_UPDATEPREPAREDORDER(
      "UserTask_UpdatePreparedOrder"), USERTASK_UPDATESERVEDORDER(
          "UserTask_UpdateServedOrder"), SERVICETASK_CALCULATEBILL("ServiceTask_CalculateBill");

  private final String taskName;

  private ProcessTasks(String taskName) {
    this.taskName = taskName;
  }

  public String getTaskName() {

    return this.taskName;
  }
}
