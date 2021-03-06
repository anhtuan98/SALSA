/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.AppCapabilityBase;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.VMCapabilityBase;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.VMProvisionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import java.util.List;

/**
 * This class generalize the capability of application and VM into service unit. The states control is enabled here.
 *
 * @author Duc-Hung Le
 */
public class GenericUnitCapability implements UnitCapabilityInterface {

    SalsaCenterConnector centerCon;
    UnitCapabilityInterface lowerCapaApp = new AppCapabilityBase();
    UnitCapabilityInterface lowerCapaVM = new VMCapabilityBase();

    {
        try {
            centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        } catch (EngineConnectionException ex) {
            EngineLogger.logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
        }
    }
    
    static final long cooldown = 2000;
    static long lastDeploymentTime = (new java.util.Date()).getTime();
    
    // This assume that a docker container will always created at least 2 seconds after previous one, on a same VM
    private void waitForCooledDown() {
        long currentTime = (new java.util.Date()).getTime();
        long between = currentTime - lastDeploymentTime;
        if (between < cooldown) {
            EngineLogger.logger.debug("Waiting a "+(cooldown-between)+" miliseconds to reduce the cloud failure when create many VM at a time");
            try {
                Thread.sleep(cooldown-between);
            } catch (InterruptedException ex) {
                lastDeploymentTime = currentTime;
            }
        }
        lastDeploymentTime = currentTime;
    }
    
    

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaException {
        waitForCooledDown();
        EngineLogger.logger.info("Start generic unit deployment for node: {}/{}/{}", serviceId, nodeId, instanceId);
        CloudService newservice = centerCon.getUpdateCloudServiceRuntime(serviceId);
        String topologyId = newservice.getTopologyOfNode(nodeId).getId();
        ServiceUnit node = newservice.getComponentById(topologyId, nodeId);
        EngineLogger.logger.debug("Node type: " + node.getType() + ". String: " + SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString());
        ServiceInstance repData = new ServiceInstance(instanceId, null);
        repData.setState(SalsaEntityState.ALLOCATING);
        repData.setExtra("Waiting for dependencies");
        repData.setHostedId_Integer(2147483647);

        java.util.Date date = new java.util.Date();
        EngineLogger.logger.debug("TIMESTAMP - Node: " + nodeId + "/" + instanceId + ", state: Allocating(manual)" + ", Time: " + date.getTime());

        if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            if (node.getInstanceNumber() >= node.getMax()) {
                EngineLogger.logger.error("Not enough cloud resource quota for the node: " + nodeId + ". Quit !");
                // out of quota
                throw new VMProvisionException(VMProvisionException.VMProvisionError.QUOTA_LIMITED, "Not enough quota as described to deploy the node: " + serviceId + nodeId + instanceId + ". This node can have maxinum " + node.getMax() + " instances");
            } else {
                centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);                
                lowerCapaVM.deploy(serviceId, nodeId, instanceId);
                centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.CONFIGURING, "Pioneer is configuring artifacts");
                EngineLogger.logger.debug("Updated VM state for node: " + nodeId + " to CONFIGURING !");
                EngineLogger.logger.info("Generic unit deployment for VM node: {}/{}/{} is done", serviceId, nodeId, instanceId);
                return new ServiceInstance(instanceId);
            }
        } else {
            centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);

            // app based: now select different kind of app            
            lowerCapaApp.deploy(serviceId, nodeId, instanceId);
            EngineLogger.logger.info("Generic unit deployment for artifact node: {}/{}/{} is done", serviceId, nodeId, instanceId);
            return new ServiceInstance(instanceId);
        }

    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {
        EngineLogger.logger.info("Start remove generic node: {}/{}/{}", serviceId, nodeId, instanceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);
        ServiceInstance instance = node.getInstanceById(instanceId);

        List<ServiceUnit> listUnit = service.getAllComponent();

        // undeploy dependency chain first. It is recursive.
        for (ServiceUnit u : listUnit) {  // all the unit of the service
            EngineLogger.logger.debug("Checking if this unit: " + u.getId() + " is HOSTED ON current removing node: " + nodeId);
            if (u.getHostedId().equals(nodeId)) {    // this is hosted on the node we want to remove. remove it first                        
                EngineLogger.logger.debug("YES! Now check instance of node: " + u.getId() + " is HOSTED ON current removing node: " + nodeId);
                for (ServiceInstance i : u.getInstanceHostOn(instanceId)) {   // the instance of above unit and hosted on current instance
                    EngineLogger.logger.debug("Found instance need to be remove first: " + u.getId() + "/" + i.getInstanceId());
                    GenericUnitCapability geneCapa = new GenericUnitCapability();
                    geneCapa.remove(serviceId, u.getId(), i.getInstanceId());
                }
            }
            for (String connectoId : u.getConnecttoId()) {    // the unit u can connect to something
                EngineLogger.logger.debug("Checking if this unit: " + u.getId() + " is CONNECT TO current removing node: " + nodeId);
                if (connectoId.equals(nodeId)) {             // which can be this node
                    EngineLogger.logger.debug("YES! Now checking instance of node: " + u.getId() + " is CONNECT TO current removing node: " + nodeId);
                    for (ServiceInstance i : u.getInstancesList()) {   // remove all its instance
                        EngineLogger.logger.debug("Found instance need to be remove first: " + u.getId() + "/" + i.getInstanceId());
                        GenericUnitCapability geneCapa = new GenericUnitCapability();
                        geneCapa.remove(serviceId, u.getId(), i.getInstanceId());
                    }
                }
            }
        }
        EngineLogger.logger.debug("The dependency should be cleaned for the node: " + nodeId + "/" + instanceId);

        // check all instance of hoston and connect to are undeployed
        // do similar things and check if all the list are empty
        boolean cleaned;
        do {
            service = centerCon.getUpdateCloudServiceRuntime(serviceId);
            node = service.getComponentById(nodeId);
            listUnit = service.getAllComponent();
            cleaned = true;
            EngineLogger.logger.debug("Checking if dependency is really clean for node: " + nodeId + "/" + instanceId);
            for (ServiceUnit u : listUnit) {
                if (u.getHostedId().equals(nodeId)) {
                    EngineLogger.logger.debug("Checking if dependency is really clean for node: " + nodeId + "/" + instanceId + ". Checking unit: " + u.getId() + " which number of hosted on inst: " + u.getInstanceHostOn(instanceId).size());
                    if (!u.getInstanceHostOn(instanceId).isEmpty()) {
                        EngineLogger.logger.debug("Waiting for cleaning HOST ON nodes of: " + nodeId + "/" + instanceId + ". Nodes left: " + u.getInstanceHostOn(instanceId).size());
                        for (ServiceInstance debugI : u.getInstanceHostOn(instanceId)) {
                            EngineLogger.logger.debug("They are: " + u.getId() + "/" + debugI.getInstanceId());
                        }
                        cleaned = false;
                    }
                }
                for (String connectoId : u.getConnecttoId()) {
                    if (connectoId.equals(nodeId)) {
                        if (!u.getInstancesList().isEmpty()) {
                            EngineLogger.logger.debug("Waiting for cleaning CONNECT TO nodes of: " + nodeId + "/" + instanceId + ". Nodes left: " + u.getInstancesList().size());
                            cleaned = false;
                        }
                    }
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        } while (cleaned == false);

        EngineLogger.logger.debug("It is TRUE, the dependency is now cleaned for the node: " + nodeId + "/" + instanceId);

        // if the state is not DEPLOYED, just remove the metadata
        if (!instance.getState().equals(SalsaEntityState.DEPLOYED)) {
            EngineLogger.logger.warn("Removing metadata for node {},{},{}. However, the state is {}, maybe the actual instance is created but will not be removed.", serviceId, nodeId, instanceId, instance.getState());
            centerCon.removeInstanceMetadata(serviceId, nodeId, instanceId);
            EngineLogger.logger.info("Removed generic node: {}/{}/{} with warning", serviceId, nodeId, instanceId);
            return;
        }

        // Call appropriate catapbility based on type
        if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {      
            EngineLogger.logger.info("Node {}/{}/{} is an VM, calling VMCacapabilityBase...", serviceId, nodeId, instanceId);            
            lowerCapaVM.remove(serviceId, nodeId, instanceId);
        } else {
            EngineLogger.logger.info("Node {}/{}/{} is an app, calling AppCapabilityBase...", serviceId, nodeId, instanceId);            
            lowerCapaApp.remove(serviceId, nodeId, instanceId);
        }
        EngineLogger.logger.info("Removed generic node: {}/{}/{}", serviceId, nodeId, instanceId);

        // unregister pioneer
        String pioneerID = PioneerManager.getPioneerID(SalsaConfiguration.getUserName(), serviceId, nodeId, instanceId);
        if (pioneerID != null) {
            PioneerManager.removePioneer(pioneerID);
            EngineLogger.logger.debug("Also unregistered pioneer ID: {}", pioneerID);
        }
    }

}
