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
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;

/**
 *
 * @author Duc-Hung Le
 */
public class AsyncUnitCapability implements UnitCapabilityInterface {    
    UnitCapabilityInterface lowerCapa = new RichInformationUnitCapability();

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaException {
        EngineLogger.logger.debug("Spawn a thread to deploy: {}/{}/{}", serviceId, nodeId, instanceId);
        new Thread(new asynSpawnInstances(serviceId, nodeId, instanceId)).start();        
        return null;
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {        
        EngineLogger.logger.debug("Spawn a thread to remove: {}/{}/{}", serviceId, nodeId, instanceId);
        new Thread(new asynRemoveInstances(serviceId, nodeId, instanceId)).start();        
    }

    private class asynSpawnInstances implements Runnable {

        String serviceId, topoId, nodeId;
        int instanceId;

        asynSpawnInstances(String serviceId, String nodeId, int instanceId) {
            this.serviceId = serviceId;
            this.nodeId = nodeId;
            this.instanceId = instanceId;
        }

        @Override
        public void run() {
            try {
                EngineLogger.logger.debug("Thread is spawned for {}/{}/{} new... prepare to run the lower capability.", serviceId, nodeId, instanceId);
                lowerCapa.deploy(serviceId, nodeId, instanceId);
            } catch (SalsaException e) {
                EngineLogger.logger.error(e.getMessage());
            }
        }

    }
    
    
    
    private class asynRemoveInstances implements Runnable {

        String serviceId, topoId, nodeId;
        int instanceId;

        asynRemoveInstances(String serviceId, String nodeId, int instanceId) {
            this.serviceId = serviceId;
            this.nodeId = nodeId;
            this.instanceId = instanceId;
        }

        @Override
        public void run() {
            try {
                EngineLogger.logger.debug("Thread is spawned for removing {}/{}/{}", serviceId, nodeId, instanceId);
                lowerCapa.remove(serviceId, nodeId, instanceId);
            } catch (SalsaException e) {
                EngineLogger.logger.error(e.getMessage());
            }
        }

    }

}
