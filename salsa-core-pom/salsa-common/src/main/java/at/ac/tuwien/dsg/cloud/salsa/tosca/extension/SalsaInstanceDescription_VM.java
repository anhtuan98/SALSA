package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;


/**
 * 
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 * 
 * @author Le Duc Hung
 * TODO: Unified instance type. Currently: use String.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescription")
public class SalsaInstanceDescription_VM {

	@XmlElement(name = "provider")
	private String provider;
	
	@XmlElement(name = "baseImage")
	private String baseImage;
	
	@XmlElement(name = "instanceType")
	private String instanceType;

	@XmlElement(name = "id")
	private String instanceId;

	@XmlElement(name = "privateIp")
	private String privateIp;
	@XmlElement(name = "publicIP")
	private String publicIp;
	@XmlElement(name = "privateDNS")
	private String privateDNS;
	@XmlElement(name = "publicDNS")
	private String publicDNS;
	@XmlElement(name = "state")
	private String state;
	
	@XmlElement(name = "Packages")
	PackagesDependencies packagesDependencies;

	public SalsaInstanceDescription_VM(){
	}
	
	public SalsaInstanceDescription_VM(String provider, String instanceId){
		this.provider = provider;
		this.instanceId = instanceId;
//		this.replicaNumber = replicaNumber;
	}
	
	

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	

//	public int getReplicaNumber() {
//		return replicaNumber;
//	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public void setPrivateIp(String privateIp) {
		this.privateIp = privateIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public void setPrivateDNS(String privateDNS) {
		this.privateDNS = privateDNS;
	}
	public void setPublicDNS(String publicDNS) {
		this.publicDNS = publicDNS;
	}
//	public void setReplicaNumber(int replicaNumber) {
//		this.replicaNumber = replicaNumber;
//	}

	


	public String getInstanceId() {
		return instanceId;
	}

	

	public String getPrivateIp() {
		return privateIp;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public String getPrivateDNS() {
		return privateDNS;
	}

	public String getPublicDNS() {
		return publicDNS;
	}
	
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getBaseImage() {
		return baseImage;
	}

	public void setBaseImage(String baseImage) {
		this.baseImage = baseImage;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	
	public PackagesDependencies getPackagesDependenciesList() {
		return packagesDependencies;
	}

	public void setPackagesDependencies(PackagesDependencies packagesDependencies) {
		this.packagesDependencies = packagesDependencies;
	}

	@Override
	public String toString() {
		return "SalsaInstanceDescription [provider=" + provider
				+ ", baseImage=" + baseImage + ", instanceType=" + instanceType
				+ ", instanceId=" + instanceId + ", privateIp=" + privateIp
				+ ", publicIp=" + publicIp + ", privateDNS=" + privateDNS
				+ ", publicDNS=" + publicDNS + ", state=" + state + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SalsaInstanceDescription_VM) {
			return instanceId.equals(((SalsaInstanceDescription_VM) obj)
					.getInstanceId());
		}
		return false;
	}
	
	
	public void updateFromMappingProperties(SalsaMappingProperties maps){
		for (SalsaMappingProperty	map : maps.getProperties()) {
			if (map.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				this.provider = map.get("provider");
				this.baseImage = map.get("baseImage");
				this.instanceType = map.get("instanceType");
				String packageStr = map.get("packages");
				
				List<String> packagelist = new ArrayList<String>(Arrays.asList(packageStr.split(",")));	
				this.packagesDependencies = new PackagesDependencies();
				this.packagesDependencies.setPackageDependency(packagelist);				
			}
		}		
	}
	
	public Map<String,String> exportToMap(){
		Map<String,String> resMap = new HashMap<String, String>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("provider", this.provider);
		map.put("baseImage", this.baseImage);
		map.put("instanceType", this.instanceType);
		map.put("instanceId", this.instanceId);
		map.put("publicIp", this.publicIp);
		map.put("privateIp", this.privateIp);
		map.put("publicDNS", this.publicDNS);
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = (Map.Entry<String,String>) iterator.next();
			if (mapEntry.getValue()!=null){
				resMap.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		
		return resMap;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "Packages")
	public static class PackagesDependencies{
		
		@XmlElement(name = "Package")
		List<String> packageDependency;

		
		public List<String> getPackageDependency() {
			return packageDependency;
		}

		public void setPackageDependency(List<String> packageDependency) {
			if (this.packageDependency == null){
				this.packageDependency = new ArrayList<>();
			}
			this.packageDependency = packageDependency;
		}

		@Override
		public String toString() {
			return "PackagesDependencies [packageDependency="
					+ packageDependency + "]";
		}			
		
	}

}
