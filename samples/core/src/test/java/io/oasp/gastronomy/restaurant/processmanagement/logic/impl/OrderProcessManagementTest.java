package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import io.oasp.gastronomy.restaurant.SpringBootApp;
import io.oasp.gastronomy.restaurant.general.common.DbTestHelper;
import io.oasp.gastronomy.restaurant.general.common.TestUtil;
import io.oasp.gastronomy.restaurant.general.common.api.constants.PermissionConstants;
import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.OrderProcessTasks;
import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderState;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.Salesmanagement;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderPositionEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.impl.SalesManagementTest;
import io.oasp.module.test.common.base.ComponentTest;

/**
 * This is the test-case of {@link OrderProcessmanagement}.
 *
 * @author vmuschter
 */
@SpringApplicationConfiguration(classes = { SpringBootApp.class })
@WebAppConfiguration
public class OrderProcessManagementTest extends ComponentTest {

  @Inject
  private Salesmanagement salesManagement;

  @Inject
  private DbTestHelper dbTestHelper;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private ProcessEngine processEngine;

  private SalesManagementTest salesmanagementTest;

  private OrderProcessmanagementImpl orderProcessmanagement;

  /**
   * @param orderProcessmanagement new value of {@link Inject}.
   */
  @Inject
  public void setOrderProcessmanagement(OrderProcessmanagementImpl orderProcessmanagement) {

    this.orderProcessmanagement = orderProcessmanagement;
  }

  /**
   * Initialization for the test.
   */
  @Before
  public void setUp() {

    TestUtil.login("waiter", PermissionConstants.FIND_ORDER, PermissionConstants.FIND_ORDER_POSITION,
        PermissionConstants.SAVE_ORDER_POSITION, PermissionConstants.SAVE_ORDER, PermissionConstants.FIND_OFFER,
        PermissionConstants.FIND_STAFF_MEMBER);
    this.dbTestHelper.setMigrationVersion("0002");
    // do not reset database because camunda tables are then missing
    // this.dbTestHelper.resetDatabase();

    this.salesmanagementTest = new SalesManagementTest();
    this.salesmanagementTest.setSalesManagement(this.salesManagement);

    // start a first instance of an order process if needed
    getNewProcessInstance();

  }

  /**
   * Log out utility for the test.
   */
  @After
  public void tearDown() {

    // delete all process instances
    List<ProcessInstance> processInstancesList =
        this.processEngine.getRuntimeService().createProcessInstanceQuery().active().list();

    for (ProcessInstance pi : processInstancesList) {
      this.processEngine.getRuntimeService().deleteProcessInstance(pi.getProcessInstanceId(), null);
    }

    TestUtil.logout();
  }

  /**
   * Tests if the {@link ProcessInstance} is started correctly. The test focuses on starting an order process and saving
   * a new {@link OrderEto} and {@link OrderPositionEto}. Test data is created using {@link SalesManagementTest}.
   */
  @Test
  public void testStartOrderProcess() {

    OrderEto order = this.salesmanagementTest.prepareNewOrderEto();
    OrderPositionEto orderPosition = this.salesmanagementTest.prepareNewOrderPositionEto(order);

    ProcessInstance processInstance =
        this.orderProcessmanagement.startOrderProcess(ProcessKeyName.STANDARD_ORDER_PROCESS, order, orderPosition);

    String pId = processInstance.getProcessInstanceId();
    // check if process variables exist with correct value
    Long processOrderId = (Long) this.runtimeService.getVariable(pId, "orderId");
    assertThat(processOrderId).isNotNull();
    Long processOrderPositionId = (Long) this.runtimeService.getVariable(pId, "orderPositionId");
    assertThat(processOrderPositionId).isNotNull();

    // check that an order with the given id exists after the process is started
    OrderEto savedOrder = this.salesManagement.findOrder(processOrderId);
    assertThat(savedOrder).isNotNull();
    // check that an orderPosition with the given id exists after the process is started
    OrderPositionEto savedOrderPosition = this.salesManagement.findOrderPosition(processOrderPositionId);
    assertThat(savedOrder).isNotNull();

    ProcessEngineTests.assertThat(processInstance).isStarted();

  }

  /**
   * Tests if the {@link ProcessInstance} / a running order process can be stepped through correctly.
   */
  @Test
  public void testCorrectOrderProcessFlow() {

    try {
      // given
      ProcessInstance processInstance = getNewProcessInstance();
      String processInstanceId = processInstance.getProcessInstanceId();
      Long orderId = (Long) this.runtimeService.getVariable(processInstanceId, "orderId");
      Long orderPositionId = (Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId");

      // check if order process is started and waiting at accept task
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(OrderProcessTasks.USERTASK_ACCEPTORDER.getTaskName());

      OrderPositionEto orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      OrderEto order = this.salesManagement.findOrder(orderPosition.getOrderId());

      // check that order process state is ordered
      assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
          .isEqualTo(OrderPositionState.ORDERED.name());

      // check that order state is open
      OrderState orderState = order.getState();
      assertThat(orderState).isEqualTo(OrderState.OPEN);

      // check that orderpositionstate is ordered
      OrderPositionState orderPositionState = orderPosition.getState();
      assertThat(orderPositionState).isEqualTo(OrderPositionState.ORDERED);

      // check that no cook has been assigned to the orderposition yet (and in that case also not to the next task)
      assertThat(orderPosition.getCookId()).isNull();

      // step on in the process by accepting to prepare the order, that is connected to the process
      this.orderProcessmanagement.acceptOrder(processInstance);

      // check that the user task "Accept Order" is completed
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().hasPassed(OrderProcessTasks.USERTASK_ACCEPTORDER.getTaskName());

      /*
       * check the the new process state is now "accepted" -> means in preparation (still left out because of the
       * required additional state checks in UCManageOrderPositionImpl)
       */
      // assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
      // .isEqualTo(OrderPositionState.ACCEPTED.name());

      // check that now a cook is set for the orderposition
      orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      assertThat(orderPosition.getCookId()).isNotNull();

      // check that the process is now waiting at the next user task
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(OrderProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());

      // check that the user task "Update Prepared Order" is now assigned to that cook
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isWaitingAt(OrderProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName()).task().isAssignedTo("cook");

      // step on in the process by confirming that order is prepared and ready to be served
      this.orderProcessmanagement.updateOrderPrepared(processInstance);

      // check that the update for a prepared order has been made
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().hasPassed(OrderProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());

      // check that order process state is in a new state
      assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
          .isEqualTo(OrderPositionState.PREPARED.name());

      // check that orderposition state has changed too
      orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      orderPositionState = orderPosition.getState();
      assertThat(orderPositionState).isEqualTo(OrderPositionState.PREPARED);

      // check that the process is waiting for the confirmation of the order to be delivered
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(OrderProcessTasks.USERTASK_UPDATESERVEDORDER.getTaskName());

      // complete the serving of the order
      this.orderProcessmanagement.updateOrderServed(processInstance);

      // check that the update for a delivered order has been made
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().hasPassed(OrderProcessTasks.USERTASK_UPDATESERVEDORDER.getTaskName());

      // check that order process state is in a new state
      assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
          .isEqualTo(OrderPositionState.DELIVERED.name());
      // check that orderposition state has changed too
      orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      orderPositionState = orderPosition.getState();
      assertThat(orderPositionState).isEqualTo(OrderPositionState.DELIVERED);

      // check that the process is waiting for the bill to be requested
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isWaitingFor("Message_Bill");

      this.orderProcessmanagement.handleBillRequest(processInstance);

      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().hasPassed(OrderProcessTasks.SERVICETASK_CALCULATEBILL.getTaskName());

      // check that the process is waiting for the input of payment data and the payment confirmation
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(OrderProcessTasks.USERTASK_CONFIRMPAYMENT.getTaskName());

      this.orderProcessmanagement.confirmPayment(processInstance);
      // check that orderposition is in a new state
      assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
          .isEqualTo(OrderPositionState.PAYED.name());
      // check that orderposition state has changed too
      orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      orderPositionState = orderPosition.getState();
      assertThat(orderPositionState).isEqualTo(OrderPositionState.PAYED);

      // check that the process is waiting for the final closing of the order
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(OrderProcessTasks.USERTASK_CLOSEORDER.getTaskName());

      this.orderProcessmanagement.closeOrder(processInstance);

      // check that order state has changed too
      order = this.salesManagement.findOrder(order.getId());
      orderState = order.getState();
      assertThat(orderState).isEqualTo(OrderState.CLOSED);

      // check that process is ended
      ProcessEngineTests.assertThat(processInstance).isEnded();

    } catch (ConstraintViolationException e) {
      // BV is really painful as you need such code to see the actual error in JUnit.
      StringBuilder sb = new StringBuilder(64);
      sb.append("Constraints violated:");
      for (ConstraintViolation<?> v : e.getConstraintViolations()) {
        sb.append("\n");
        sb.append(v.getPropertyPath());
        sb.append(":");
        sb.append(v.getMessage());
      }
      throw new IllegalStateException(sb.toString(), e);
    }

  }

  /**
   * Tests if the {@link ProcessInstance} / a running order process can be stepped through incorrectly.
   */
  @Test
  public void testIncorrectOrderProcessFlow() {

    // try a wrong activity order
    // start process
    ProcessInstance processInstance = getNewProcessInstance();

    // try to mark an order as prepared before it has been accepted
    this.orderProcessmanagement.updateOrderPrepared(processInstance);

    // check that process is still waiting at the previous task
    ProcessEngineTests.assertThat(processInstance).isWaitingAt(OrderProcessTasks.USERTASK_ACCEPTORDER.getTaskName());

    this.orderProcessmanagement.acceptOrder(processInstance);

    // try to mark an order as served before it has been prepared
    this.orderProcessmanagement.updateOrderServed(processInstance);
    // check that process is still waiting at the previous task
    ProcessEngineTests.assertThat(processInstance)
        .isWaitingAt(OrderProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());

    this.orderProcessmanagement.updateOrderPrepared(processInstance);

    // try to confirm the payment before the order has been served
    this.orderProcessmanagement.confirmPayment(processInstance);
    // check that process is still waiting at the previous task
    ProcessEngineTests.assertThat(processInstance)
        .isWaitingAt(OrderProcessTasks.USERTASK_UPDATESERVEDORDER.getTaskName());

    this.orderProcessmanagement.updateOrderServed(processInstance);
    this.orderProcessmanagement.handleBillRequest(processInstance);

    // try to close an order before the order has been payed
    this.orderProcessmanagement.closeOrder(processInstance);
    // check that process is still waiting at the previous task
    ProcessEngineTests.assertThat(processInstance).isWaitingAt(OrderProcessTasks.USERTASK_CONFIRMPAYMENT.getTaskName());
    this.orderProcessmanagement.confirmPayment(processInstance);

    // check that process is now waiting to be closed
    ProcessEngineTests.assertThat(processInstance).isWaitingAt(OrderProcessTasks.USERTASK_CLOSEORDER.getTaskName());
    this.orderProcessmanagement.closeOrder(processInstance);
    // check that process is ended
    ProcessEngineTests.assertThat(processInstance).isEnded();

  }

  /**
   * Creates a new {@link OrderEto} and {@link OrderPositionEto} and passes them to the startOrderProcess method.
   * Returns a new {@link ProcessInstance} which holds the order information as process variables.
   */
  private ProcessInstance getNewProcessInstance() {

    OrderEto order = this.salesmanagementTest.prepareNewOrderEto();
    OrderPositionEto orderPosition = this.salesmanagementTest.prepareNewOrderPositionEto(order);

    // start order process
    ProcessInstance processInstance =
        this.orderProcessmanagement.startOrderProcess(ProcessKeyName.STANDARD_ORDER_PROCESS, order, orderPosition);

    return processInstance;
  }

}
