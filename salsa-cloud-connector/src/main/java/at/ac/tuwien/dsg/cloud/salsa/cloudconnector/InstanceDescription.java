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
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * This class acts as a container for all the information
 * 
 * @author Mario Bisignani (bisignam@usi.ch)
 * 
 */

public class InstanceDescription {

	// public enum InstanceState {PENDING, RUNNING, SHUTTING_DOWN, TERMINATED,
	// STOPPING, STOPPED}

	
	//private FQN replicaFQN;
	private String instanceId;
	private InetAddress privateIp;
	private InetAddress publicIp;
	private String privateDNS;
	private String publicDNS;
	private VMStates state;
	private int quota = 0;

	// return new InstanceDescription(instanceID,
	// InetAddress.getByName(instance.getPrivateIpAddress()),
	// InetAddress.getByName(instance.getIpAddress()),
	// instance.getPrivateDnsName(), instance.getDnsName());

	public InstanceDescription(String instanceId, String _privateIp, String _publicIp) {
		try {
			if (_privateIp != null && _privateIp.length() > 0) {
				this.privateIp = InetAddress.getByName(_privateIp);
			}
			if (_publicIp != null && _publicIp.length() > 0) {
				this.publicIp = InetAddress.getByName(_publicIp);
			}
			
			this.instanceId = instanceId;		
			this.state = VMStates.Pending;
		} catch (UnknownHostException e){
			System.out.println("Error by undefined IP address");
		}
	}
	
	public InstanceDescription(String instanceId,
			InetAddress privateIp, InetAddress publicIp, String privateDNS,
			String publicDNS, int quota) {

		
		this.instanceId = instanceId;
		this.privateIp = privateIp;
		this.publicIp = publicIp;
		this.privateDNS = privateDNS;
		this.publicDNS = publicDNS;
		this.state = VMStates.Pending;
		this.quota = quota;
	}

	
	public VMStates getState() {
		return state;
	}

	public void setState(VMStates state) {
		this.state = state;
	}

	// Copy constructor
	public InstanceDescription(InstanceDescription inst) {

		this.instanceId = new String(inst.instanceId);
		try {
			this.privateIp = InetAddress
					.getByName(inst.privateIp.getHostName());
			this.publicIp = InetAddress.getByName(inst.publicIp.getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.privateDNS = new String(inst.privateDNS);
		this.publicDNS = new String(inst.publicDNS);

	}

	
	public int getQuota() {
		return quota;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public InetAddress getPrivateIp() {
		return privateIp;
	}

	public InetAddress getPublicIp() {
		return publicIp;
	}

	public String getPrivateDNS() {
		return privateDNS;
	}

	public String getPublicDNS() {
		return publicDNS;
	}

	@Override
	public String toString() {
		return "InstanceDescription [instanceId=" + instanceId + ", privateIp=" + privateIp
				+ ", publicIp=" + publicIp + ", privateDNS=" + privateDNS
				+ ", publicDNS=" + publicDNS + "]";
	}


}
