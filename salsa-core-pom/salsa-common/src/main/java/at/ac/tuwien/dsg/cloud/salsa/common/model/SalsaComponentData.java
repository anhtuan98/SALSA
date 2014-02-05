package at.ac.tuwien.dsg.cloud.salsa.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ServiceUnit")
@XmlSeeAlso({  
    SalsaTopologyData.class,
    SalsaComponentInstanceData.class
})
public class SalsaComponentData extends SalsaEntity {	
	
	@XmlElement(name = "Replica")
	List<SalsaComponentInstanceData> repLst = new ArrayList<SalsaComponentInstanceData>();;
		
	@XmlAttribute(name = "type")
	String type;
	
	public void addInstance(SalsaComponentInstanceData instance){
		repLst.add(instance);
	}
	
	public SalsaComponentInstanceData getInstanceById(int instance){
		for (SalsaComponentInstanceData node : repLst) {
			if (node.getInstanceId() == instance){
				return node;
			}			
		}
		return null;
	}
	
	public SalsaComponentData(String id, String type){
		this.id = id;
		this.type = type;
	}
	
	public SalsaComponentData(){}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getInstanceNumber(){
		return repLst.size();
	}
	
	public List<SalsaComponentInstanceData> getInstanceByState(SalsaEntityState state){
		List<SalsaComponentInstanceData> lst = new ArrayList<>();
		for (SalsaComponentInstanceData rep : repLst) {
			if (rep.getState() == state ){
				lst.add(rep);
			}
		}
		return lst;
	}
	
	
	public int getInstanceNumberByState(SalsaEntityState state){
		int counter = 0;
		for (SalsaComponentInstanceData rep : repLst) {
			if (rep.getState() == state ){
				counter++;
			}
		}
		return counter;
	}

	public List<SalsaComponentInstanceData> getInstanceList() {
		return repLst;
	}	

}
