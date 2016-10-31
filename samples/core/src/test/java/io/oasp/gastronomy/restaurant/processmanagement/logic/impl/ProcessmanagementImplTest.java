package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;

import io.oasp.module.test.common.base.ModuleTest;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public class ProcessmanagementImplTest extends ModuleTest {

  // private static final String BPMN_LOCATION = "./src/main/resources/processes/standardOrderProcess.bpmn";
  //
  // private static final String BPMN_NAME = "standardOrderProcess.bpmn";
  //
  // private static final String PROCESS_KEY = ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName();

  private static final String BPMN_LOCATION = "./src/main/resources/processes/prototypeProcess.bpmn";

  private static final String BPMN_NAME = "prototypeProcess.bpmn";

  private static final String PROCESS_KEY = "prototypeProcess";

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  /**
   * Load and deploy the BPMN model - alternative to @Deploy annotation
   *
   * @throws FileNotFoundException
   */
  @Before
  public void loadModel() throws FileNotFoundException {

    Deployment deployment = this.repositoryService.createDeployment()
        .addInputStream(BPMN_NAME, new FileInputStream(BPMN_LOCATION)).deploy();
    assertNotNull(deployment.getId());
    assertTrue(this.repositoryService.getDeploymentResourceNames(deployment.getId()).contains(BPMN_NAME));

  }

  /**
   * This test method checks the functionality of UserTaskListener
   */
  @Test
  public void testStartProcess() {

    // Given
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
    assertNotNull(processInstance);
  }

  /*
   * File table = null;
   *
   * OrderProcess orderProcess;
   *
   * @Inject Processmanagement processmanagement;
   *
   *//**
     * This method initializes the object {@link ProcessmanagementImpl} and assigns the mocked objects of the classes
     * {@link XXXX} and {@link BeanMapper} to the attributes of the {@link ProcessmanagementImpl} object before tests,
     * if they are not null.
     */
  /*
   * @Before public void init() {
   *
   * // assertThat(this.table.canWrite()); }
   *
   *//**
     * This method dereferences all object after each test
     *//*
       * @After public void clean() {
       *
       * // wie hinterlässt man die Tabelle? }
       *
       * @Test public void startProcessTest() {
       *
       * Long orderProcessId = 1L;
       *
       * // assertTrue(this.processmanagement.startProcess(orderProcessId));
       *
       * }
       *
       * @Test public void stopProcessTest() {
       *
       * Long orderProcessId = 1L; assertTrue(this.processmanagement.stopProcess(orderProcessId));
       *
       * }
       *
       * @Test public void getOrderProcessStateTest() {
       *
       * Long orderProcessId = 1L; assertThat(this.processmanagement.getOrderProcessState(orderProcessId));
       *
       * }
       *
       * private void prepareTestData() {
       *
       * // this.orderProcess = new OrderProcessEto(); //
       * this.orderProcess.setOrderPositionState(OrderPositionState.ORDERED); }
       */
}
