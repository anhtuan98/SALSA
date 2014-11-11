package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.flexiant;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.VMStates;

import com.extl.jade.user.ExtilityException;
import com.extl.jade.user.FilterCondition;
import com.extl.jade.user.Job;
import com.extl.jade.user.ListResult;
import com.extl.jade.user.NetworkType;
import com.extl.jade.user.Nic;
import com.extl.jade.user.QueryLimit;
import com.extl.jade.user.ResourceMetadata;
import com.extl.jade.user.ResourceType;
import com.extl.jade.user.SearchFilter;
import com.extl.jade.user.Server;
import com.extl.jade.user.ServerStatus;
import com.extl.jade.user.UserAPI;
import com.extl.jade.user.UserService;


public class FlexiantConnector implements CloudInterface{	
	Logger logger;
	String userEmailAddress;
	String customerUUID ;
	String password;
	String endpoint;
	String vdcUUID; 
	String defaultProductOfferUUID;
	String deploymentInstanceUUID;	
	String clusterUUID;
	String networkUUID;
	String sshKey;
	
	static final String initialUser = "ubuntu";
	static final String initialPasswd = "dsg@123";
	
	static final String DEFAULT_IMAGE="a064bd97-c84c-38ef-aa37-c7391a8c8259";
	
	UserService service;
	
	private void enableSNIExtension(){
		// Avoid the handshare SSL error
		System.setProperty("jsse.enableSNIExtension", "false");
	}
	
	public FlexiantConnector(Logger logger, String email, String cuscomerUUID, String password, String endpoint, String vdcUUID, String defaultProductOfferUUID, String clusterUUID, String networkUUID, String sshKey){
		this.logger = logger;
		this.userEmailAddress = email;
		this.customerUUID = cuscomerUUID;
		this.password = password;
		this.endpoint = endpoint;
		this.vdcUUID = vdcUUID;
		this.defaultProductOfferUUID = defaultProductOfferUUID;
		this.clusterUUID=clusterUUID;
		this.networkUUID=networkUUID;
		this.sshKey = sshKey;
				 
		enableSNIExtension();
		
        URL url = FlexiantConnector.class.getResource("/Flexiant/UserAPI.wsdl");
        UserAPI api = new UserAPI(url, new QName("http://extility.flexiant.net", "UserAPI"));
        
        service = api.getUserServicePort();
        
        BindingProvider portBP = (BindingProvider) service;
        
        portBP.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  this.endpoint+"/user/");
        portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, userEmailAddress + "/" + customerUUID);
        portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

	}
		
	/**
	 * Note: the instance is in this form: [RAM capa in GB]/[cpu number], such as 4/2
	 */
	@Override
	public String launchInstance(String instanceName, String imageId,
			List<String> securityGroups, String sshKeyName, String userData,
			String instType, int minInst, int maxInst)
			throws ServiceDeployerException {
		enableSNIExtension();
		 try {
			 Nic networkInterface = new Nic();
				networkInterface.setClusterUUID(this.clusterUUID);
				networkInterface.setCustomerUUID(this.customerUUID);
				networkInterface.setDeploymentInstanceUUID(this.deploymentInstanceUUID);

				networkInterface.setNetworkUUID(this.networkUUID);
				networkInterface.setVdcUUID(this.vdcUUID);
				networkInterface.setNetworkType(NetworkType.IP);
			 
	        	Server server = new Server();
	    		server.setResourceName(instanceName);
	    		server.setCustomerUUID(this.customerUUID);
	    		server.setProductOfferUUID(this.defaultProductOfferUUID);
	    		server.setVdcUUID(this.vdcUUID);
	    		server.setImageUUID(imageId);
	    		server.setInitialPassword(initialPasswd);
	    		server.setInitialUser(initialUser);
	    		String[] cpuAndRam=instType.split("/");	    		
	    		server.setCpu(Integer.parseInt(cpuAndRam[0]));	// default
	    		server.setRam(Integer.parseInt(cpuAndRam[1]));
	    		
	    		logger.debug("The instance type have CPU: " + server.getCpu() + " and RAM: " + server.getRam());
	    		server.getNics().add(networkInterface);
	    		
	    		
	    		ResourceMetadata meta = new ResourceMetadata();
	    		meta.setPrivateMetadata(userData);
	    		meta.setPublicMetadata(userData);
	    		meta.setRestrictedMetadata(userData);
	    		
	    		server.setResourceMetadata(meta);
	    		
	    		List<String> sshKeys = new ArrayList<>();
	    		Job createServerJob = service.createServer(server, sshKeys, null);	    		
	    		
	    		try {
	    			service.waitForJob(createServerJob.getResourceUUID(), false);
	    		} catch (ExtilityException e) {    		
	    			logger.error(e.getMessage()); return "";
	    		}
	    		logger.debug("Server created: " + createServerJob.getItemUUID());
	    			    		
	    		// wait for host is up
	    		Job startServer;
	    		try {
	    			startServer = service.changeServerStatus(createServerJob.getItemUUID(), ServerStatus.RUNNING, true,
	    					server.getResourceMetadata(), null);
	    		} catch (ExtilityException e) {
	    			logger.error(e.getMessage()); return "";
	    		}
	    		try {
	    			service.waitForJob(startServer.getResourceUUID(), false);
	    		} catch (ExtilityException e) {
	    			logger.error(e.getMessage()); return "";
	    		}
	    		
	    		logger.debug("Server is up: " + createServerJob.getItemUUID() + ". IP: ");	    		
	    		logger.debug(createServerJob.getItemDescription());
	    		String newVM_id = createServerJob.getItemUUID();
	    		logger.info("~~~~~~~~~~~~~~Created server with uuid "+newVM_id);
	    		
	    		// push pioneer by ssh. add a disable IPv^ for apt-get problem
	    		String updateUserData = "echo 'net.ipv6.conf.all.disable_ipv6 = 1'>>/etc/sysctl.conf \n";
	    		updateUserData += "echo 'net.ipv6.conf.default.disable_ipv6 = 1'>>/etc/sysctl.conf \n";
	    		updateUserData += "echo 'net.ipv6.conf.lo.disable_ipv6 = 1'>>/etc/sysctl.conf \n";
	    		updateUserData += "sysctl -p \n ";
	    		updateUserData += userData;
	    		FileUtils.writeStringToFile(new File("/tmp/" + newVM_id), updateUserData);
	    			    		
	    		InstanceDescription inst = getInstanceDescriptionByID(createServerJob.getItemUUID());
	    		// remove the slash at the beginning of the IP
	    		String ipAddress=inst.getPrivateIp().toString().substring(1);	
	    		logger.debug("Waiting for the ssh server is up at IP: " + ipAddress);
	    		pushAndExecuteBashScript(ipAddress, initialUser, initialPasswd, "/tmp/" + newVM_id);
	    		
	    		return createServerJob.getItemUUID();	    		    		
	        } catch (Exception e) { 
	        	logger.error("Flexiant connector error: " + e);
	        }
		
		return null;
	}
	

	@Override
	public InstanceDescription getInstanceDescriptionByID(String instanceID) {
		enableSNIExtension();
		SearchFilter sf = new SearchFilter();
		
		// Set a limit to the number of results
		QueryLimit lim = new QueryLimit();
		lim.setMaxRecords(40);
		
		try {
			// Call the service to execute the query
			ListResult result = service.listResources(sf, lim, ResourceType.SERVER);
			
			String ip="";
			
			// Iterate through the results
			for (Object o : result.getList()) {
				Server s = ((Server) o);				
				logger.info("List server " + s.getResourceUUID());
				if (s.getResourceUUID().equals(instanceID)){			
					logger.debug("Instance found: " + instanceID);					
					
					// the Server object does not have NIC, we list all NIC and match with server UUID
					List<Nic> nics = listAllNics();	
					
					for (Nic nic : nics) {
						if (nic.getServerUUID().equals(instanceID)){
							logger.debug("Found a NIC: " + nic.getIndex());
							logger.debug("nic.getIpAddresses().size(): " + nic.getIpAddresses().size());
							if (!nic.getIpAddresses().isEmpty()){		
								ip = nic.getIpAddresses().get(0).getIpAddress();
								logger.debug("IP = " + ip);
							}
						}
					}
					InstanceDescription inst = new InstanceDescription(instanceID, ip, ip);
					inst.setState(mapStatus(s.getStatus()));
					return inst;
				}
			}
			return null;
		} catch (Exception e){
			logger.error("Flexiant connector error: " + e);
			return null;
		}
	}


	@Override
	public void removeInstance(String serverUUID)
			throws ServiceDeployerException {	
		enableSNIExtension();
		logger.info("Removing server now: " + serverUUID);
		Job stopServer = null;
		try {
			stopServer = service.changeServerStatus(serverUUID,
					ServerStatus.STOPPED, true, new ResourceMetadata(), null);
			service.waitForJob(stopServer.getResourceUUID(), false);
			Job deleteServer = null;
			deleteServer = service.deleteResource(serverUUID, true, null);		
			service.waitForJob(deleteServer.getResourceUUID(), false);
		} catch (ExtilityException e) {
			logger.error(e.getMessage()); 
			return;
		}
		logger.info("Remove done: " + serverUUID);
	}
	
	

	private List<Nic> listAllNics() {
		List<Nic> nics = new ArrayList<Nic>();		
		try {
			QueryLimit lim = new QueryLimit();
			lim.setMaxRecords(1000);

			// Call the service to execute the query
			ListResult result = service.listResources(null, null, ResourceType.NIC);

			// Iterate through the results
			for (Object o : result.getList()) {
				Nic s = ((Nic) o);				
        	 	nics.add(s);

			}

		} catch (Exception e) {
			logger.info(e.getMessage()); 
		}
		logger.info("Returning "+nics.size()+"number of nics ");
		return nics;
	}
	
	private VMStates mapStatus(ServerStatus status){
		switch(status){
		case BUILDING: return VMStates.Prolog;		 
		case RUNNING: return VMStates.Running; 
		case STOPPED:
		case STOPPING:
		case DELETING: return VMStates.Terminated; 
		case ERROR: return VMStates.Failed;		
		default : return VMStates.Pending;		
		}
	}

	
	public void pushAndExecuteBashScript(String ip, String username, String password, String scriptFile){
		File file = new File(scriptFile);
		try {
			logger.debug("Execute: " + "sshpass -p "+password+" scp -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null "+scriptFile+" "+username+"@"+ip+":/tmp/"+file.getName());
			int count=0;
			Process p;			
			do {				
				p = Runtime.getRuntime().exec("sshpass -p "+password+" scp -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null "+scriptFile+" "+username+"@"+ip+":/tmp/"+file.getName());
				try {
					p.waitFor();
				} catch (InterruptedException e){
					logger.error(e.getMessage());
				}
				logger.debug("Exiting value 1 ("+count++ +"): " + p.exitValue());
				try { Thread.sleep(2000); } catch (Exception e) {
					logger.error("Flexiant connector thread interrupt: " + e);
				}
			} while (p.exitValue()!=0);			
			
			Process p1;
			do {
				String cmd = "/usr/bin/sshpass -p "+password+" /usr/bin/ssh -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null "+username+"@"+ip+" /usr/bin/sudo /bin/bash /tmp/"+file.getName() +" > /dev/null 2>&1 & ";
		
				logger.debug("AAA:   " + cmd);
				p1 = Runtime.getRuntime().exec(cmd);
				
				try { p1.waitFor(); } catch (Exception e) {
					logger.error("Flexiant connector thread interrupt: " + e);
				}
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
				String line="";
				while ((line = reader.readLine())!= null) {
					logger.debug(line);
				}
				
				logger.debug("Exiting value 2 ("+count++ +"): " + p1.exitValue());
				try { Thread.sleep(2000); } catch (Exception e) {
					logger.error("Flexiant connector thread interrupt: " + e);
				}
			} while (p1.exitValue()!=0);
		} catch (IOException e){
			logger.debug(e.getMessage());			
		}

	}
	
	
}