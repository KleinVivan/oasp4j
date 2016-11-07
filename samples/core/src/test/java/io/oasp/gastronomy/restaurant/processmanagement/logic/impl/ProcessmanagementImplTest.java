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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.oasp.gastronomy.restaurant.SpringBootApp;
import io.oasp.gastronomy.restaurant.general.common.base.AbstractRestServiceTest;
import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderCto;
import io.oasp.gastronomy.restaurant.salesmanagement.service.api.rest.SalesmanagementRestService;
import io.oasp.gastronomy.restaurant.salesmanagement.service.impl.rest.SalesmanagementRestServiceTest;
import io.oasp.gastronomy.restaurant.salesmanagement.service.impl.rest.SalesmanagementRestServiceTestHelper;
import io.oasp.gastronomy.restaurant.salesmanagement.service.impl.rest.SalesmanagementRestTestConfiguration;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { SpringBootApp.class, SalesmanagementRestTestConfiguration.class })
@TestPropertySource(properties = { "flyway.locations=filesystem:src/test/resources/db/tablemanagement" })
public class ProcessmanagementImplTest extends AbstractRestServiceTest {

  private static final String BPMN_LOCATION = "./src/main/resources/processes/standardOrderProcess.bpmn";

  private static final String BPMN_NAME = "standardOrderProcess.bpmn";

  private static final String PROCESS_KEY = ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName();

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  private SalesmanagementRestServiceTest salesmanagementRestServiceTest;

  private SalesmanagementRestService service;

  @Inject
  private SalesmanagementRestServiceTestHelper helper;

  /**
   * Load and deploy the BPMN model - alternative to @Deploy annotation
   *
   * @throws FileNotFoundException
   */
  @Before
  public void init() throws FileNotFoundException {

    // this.service = getRestTestClientBuilder().build(SalesmanagementRestService.class);
    this.salesmanagementRestServiceTest = new SalesmanagementRestServiceTest();

    Deployment deployment = this.repositoryService.createDeployment()
        .addInputStream(BPMN_NAME, new FileInputStream(BPMN_LOCATION)).deploy();
    assertNotNull(deployment.getId());
    assertTrue(this.repositoryService.getDeploymentResourceNames(deployment.getId()).contains(BPMN_NAME));

  }

  /**
   * Provides clean up after tests.
   */
  @After
  public void clean() {

    this.service = null;
  }

  /**
   * This test method checks the functionality of startProcess
   */

  // @Deployment(resources = { "classpath:/processes/*.bpmn" })
  @Test
  public void testStartProcess() {

    // Given
    // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
    // assertNotNull(processInstance);

    OrderCto responseOrderCto =
        this.salesmanagementRestServiceTest.getSavedOrder(this.salesmanagementRestServiceTest.getSampleOrder());

    // OrderCto sampleOrderCto = this.helper.createSampleOrderCto(SAMPLE_TABLE_ID);
    // OrderCto responseOrderCto = this.service.saveOrder(sampleOrderCto);
    assertThat(responseOrderCto).isNotNull();

    ProcessInstance processInstance = (ProcessInstance) this.runtimeService.createExecutionQuery().singleResult();
    assertNotNull(processInstance);
    // String processInstanceID = this.runtimeService.createExecutionQuery().singleResult().getProcessInstanceId();

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
