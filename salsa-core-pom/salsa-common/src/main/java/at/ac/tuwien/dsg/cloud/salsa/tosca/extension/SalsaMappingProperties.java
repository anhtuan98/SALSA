package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * The properties for Capabilities and Requirements. E.g:	<br/>
 * <MappingProperties>	<br/>
 * 		<MappingProperty type="os">	<br/>
 *			<property name="name">Linux</property>	<br/>
 *			<property name="arch">x64</property>	<br/>
 *		</MappingProperty>	<br/>
 * </MappingProperties>	<br/>
 * 
 * @author Le Duc Hung
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "MappingProperties")
public class SalsaMappingProperties{
	
	@XmlElement(name="MappingProperty")	
	List<SalsaMappingProperty> properties = new ArrayList<>();

	
	public List<SalsaMappingProperty> getProperties() {
		return properties;
	}	
		
	public void put(String type, String name, String value){
		for (SalsaMappingProperty property : properties) {
			if (property.mytype.equals(type)){
				property.put(name, value);
				return;
			}
		}
		SalsaMappingProperty newProp = new SalsaMappingProperty();
		newProp.mytype = type;
		newProp.put(name, value);
		properties.add(newProp);		
	}
	
	public void put(String type, Map<String, String>map){
		for (SalsaMappingProperty property : properties) {
			if (property.mytype.equals(type)){				
				property.putAll(map);
				return;
			}
		}
		SalsaMappingProperty newProp = new SalsaMappingProperty();
		newProp.mytype = type;
		newProp.putAll(map);
		properties.add(newProp);
	}
	
	public String get(String type, String name){
		for (SalsaMappingProperty property : properties) {
			if (property.mytype.equals(type)){
				return property.get(name);
			}
		}
		return null;
	}
	
	public static class SalsaMappingProperty {
		@XmlAttribute(name="type")
		String mytype;
		
		@XmlElement(name="property")
		List<Property> properties = new ArrayList<>();
		
		public String getType() {
			return mytype;
		}
		
		public String get(String name) {		
			for (Property item : properties) {
				if (item.name.equals(name)){
					return item.value;
				}
			}
			return null;
		}

		public void put(String key, String value) {
			if (!properties.contains(key)){			
				properties.add(new Property(key, value));
			}
		}
		
		public void putAll(Map<String, String> map){
			for (Map.Entry<String, String> entry : map.entrySet()) {
			   properties.add(new Property(entry.getKey(),entry.getValue()));
			}
		}

		public static class Property{
			@XmlAttribute
			String name;
			
			@XmlValue
			String value;
			
			public Property(){};
			
			public Property(String name, String value){
				this.name = name;
				this.value = value;
			}
			
		}
		
	}

	
	
}


