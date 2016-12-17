package io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype;

/**
 * @author vmuschter
 *
 *         This class contains the task names of the restaurant order process modell
 */
public enum OrderProcessTasks {
  USERTASK_ACCEPTORDER("UserTask_AcceptOrder"), USERTASK_UPDATEPREPAREDORDER(
      "UserTask_UpdatePreparedOrder"), USERTASK_UPDATESERVEDORDER(
          "UserTask_UpdateServedOrder"), SERVICETASK_CALCULATEBILL(
              "ServiceTask_CalculateBill"), USERTASK_CONFIRMPAYMENT(
                  "UserTask_ConfirmPayment"), USERTASK_CLOSEORDER("UserTask_CloseOrder");

  private final String taskName;

  private OrderProcessTasks(String taskName) {
    this.taskName = taskName;
  }

  public String getTaskName() {

    return this.taskName;
  }
}
