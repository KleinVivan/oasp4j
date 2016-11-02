package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.processmanagement.logic.api.Processmanagement;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
@Component
public class ProcessmanagementImpl implements Processmanagement {

  @Inject
  private ProcessEngine processEngine;

  @Inject
  private RuntimeService runtimeService;

  @Override
  public void startProcess(ProcessKeyName processKeyName, Long orderId) {

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("orderId", orderId);

    ProcessInstance processInstance =
        this.runtimeService.startProcessInstanceByKey(processKeyName.getKeyName(), variables);

    System.out.println(processInstance.getProcessInstanceId());

  }

}
