package at.ac.tuwien.dsg.cloud.salsa.pioneer.services;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityActions;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.ArtifactDeployer;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;

@Service
//@WebService(endpointInterface = "at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface")
public class PioneerServiceImplementation implements SalsaPioneerInterface {
	
	@Override
	public String deployNode(String nodeID, int instanceId) {
		
		PioneerLogger.logger.debug("Recieve command to deploy node: " + nodeID);
		
		Properties prop = SalsaPioneerConfiguration.getPioneerProperties();
		
		String serviceId = prop.getProperty("SALSA_SERVICE_ID");
		String topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		String vm_nodeId = prop.getProperty("SALSA_NODE_ID"); // this node
		int vm_instanceId = Integer.parseInt(prop.getProperty("SALSA_REPLICA"));
		
		SalsaCenterConnector centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		TDefinitions def = centerCon.getToscaDescription(serviceId);// get the latest service description
		CloudService serviceRuntimeInfo;
		try {
			serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
		} catch (SalsaEngineException e){
			return "";
		}
				
		ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeID, vm_instanceId, def, centerCon, serviceRuntimeInfo);
		TNodeTemplate thisNode = ToscaStructureQuery.getNodetemplateById(nodeID, def);
		String newId="";
		try {
			newId = deployer.deploySingleNode(thisNode, instanceId);
		} catch (SalsaEngineException e){			
		}
		return newId;
	}
	
	public void executeAction(String nodeId, int instanceId, String actionName) {
		PioneerLogger.logger.debug("Recieve command to executing action: " + nodeId +"/" + instanceId +"/"+actionName);		
		Properties prop = SalsaPioneerConfiguration.getPioneerProperties();
		
		String serviceId = prop.getProperty("SALSA_SERVICE_ID");
		SalsaCenterConnector centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		CloudService service;
		try {
			service = centerCon.getUpdateCloudServiceRuntime(serviceId);
		} catch (SalsaEngineException e){
			return;
		}
		ServiceUnit unit = service.getComponentById(nodeId);
		
		if (actionName.equals(SalsaEntityActions.UNDEPLOY.getActionString())){
			PioneerLogger.logger.debug("Found the default action: " + actionName);
			this.removeNodeInstance(nodeId, instanceId);			
		} else {
			// TODO: implement to execute other action types
			PrimitiveOperation p = unit.getPrimitiveByName(actionName);
			if (p!=null){
				PioneerLogger.logger.debug("Found a custom action: " + actionName);
				switch (p.getExecutionType()){
					case SCRIPT: {					
						executeCommand(p.getExecutionREF()); break;
					}
					default: {
						PioneerLogger.logger.debug("Action type is not support: " + p.getExecutionType());
					}
				}
			} else {
				PioneerLogger.logger.debug("Action name not found: " + actionName);
			}
		}
		return;
	}

	
	@Override
	public String health(){
		PioneerLogger.logger.debug("Health checked, Pioneer server is alive !");
		return "alive";
	}
	
	@Override
	public String info(){
		PioneerLogger.logger.debug("Querying info of the pioneer ");
		
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(SalsaPioneerConfiguration.getSalsaVariableFile()));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        br.close();
	        return sb.toString();
	    } catch (FileNotFoundException e1){
	    	PioneerLogger.logger.error(e1.toString());
	    } catch (IOException e2){
	    	PioneerLogger.logger.error(e2.toString());
	    } 
		
		return "alive";
	}



	@Override
	public String removeNodeInstance(String nodeID, int instanceId) {
		PioneerLogger.logger.debug("Recieve command to remove node: " + nodeID + "/" + instanceId);
		
		Properties prop = SalsaPioneerConfiguration.getPioneerProperties();
		
		String serviceId = prop.getProperty("SALSA_SERVICE_ID");
		String topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		int thisInstanceId = Integer.parseInt(prop.getProperty("SALSA_REPLICA"));
		
		SalsaCenterConnector centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		TDefinitions def = centerCon.getToscaDescription(serviceId);// get the latest service description
		CloudService serviceRuntimeInfo;
		try {
			serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
		} catch (SalsaEngineException e){
			return "";
		}		
		ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeID, thisInstanceId, def, centerCon, serviceRuntimeInfo);
		TNodeTemplate nodeForRemove = ToscaStructureQuery.getNodetemplateById(nodeID, def);
		ServiceUnit unit = serviceRuntimeInfo.getComponentById(nodeID);
		ServiceInstance instance = unit.getInstanceById(instanceId);
		
		if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())){
			SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM)instance.getProperties().getAny();
			if (vm!=null){
				String containerID = vm.getInstanceId();
				DockerConfigurator docker = new DockerConfigurator("default");
				docker.removeDockerContainer(containerID);
			}
		} else {
			deployer.removeSingleNodeInstance(nodeForRemove, Integer.toString(instanceId));
		}	
		PioneerLogger.logger.debug("Pioneer is updating UNDEPLOYED state for instance: " + serviceId +"/" + topologyId +"/" + nodeID + "/" + instanceId);
		String result = centerCon.updateNodeState(serviceId, topologyId, nodeID, instanceId, SalsaEntityState.UNDEPLOYED);
		PioneerLogger.logger.debug("Update string: " + result);
		return null;
	}
	
	
	public static String executeCommand(String cmd){
		PioneerLogger.logger.debug("Execute command: " + cmd);
		try {
			Process p = Runtime.getRuntime().exec(cmd);
		    
		 
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String line = "";	
		    StringBuffer output = new StringBuffer();
		    while ((line = reader.readLine())!= null) {
		    	PioneerLogger.logger.debug(line);
		    }
		    p.waitFor();			    
			return output.toString();
		} catch (InterruptedException e1){
			PioneerLogger.logger.error("Error when execute command. Error: " + e1);
		} catch (IOException e2){
			PioneerLogger.logger.error("Error when execute command. Error: " + e2);
		}
		return null;
	}
	
	
}
