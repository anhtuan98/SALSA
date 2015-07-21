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
package generated.oasis.tosca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;


/**
 * <p>Java class for tDefinitions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tDefinitions">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Extensions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Extension" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtension" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Import" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tImport" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Types" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="ServiceTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tServiceTemplate"/>
 *           &lt;element name="NodeType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tNodeType"/>
 *           &lt;element name="NodeTypeImplementation" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tNodeTypeImplementation"/>
 *           &lt;element name="RelationshipType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRelationshipType"/>
 *           &lt;element name="RelationshipTypeImplementation" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRelationshipTypeImplementation"/>
 *           &lt;element name="RequirementType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementType"/>
 *           &lt;element name="CapabilityType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityType"/>
 *           &lt;element name="ArtifactType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactType"/>
 *           &lt;element name="ArtifactTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactTemplate"/>
 *           &lt;element name="PolicyType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicyType"/>
 *           &lt;element name="PolicyTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicyTemplate"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDefinitions", propOrder = {
    "extensions",
    "_import",
    "types",
    "serviceTemplateOrNodeTypeOrNodeTypeImplementation"
})
@XmlSeeAlso({
    Definitions.class
})
public class TDefinitions
    extends TExtensibleElements
{

    @XmlElement(name = "Extensions")
    protected TDefinitions.Extensions extensions;
    @XmlElement(name = "Import")
    protected List<TImport> _import;
    @XmlElement(name = "Types")
    protected TDefinitions.Types types;
    @XmlElements({
        @XmlElement(name = "ServiceTemplate", type = TServiceTemplate.class),
        @XmlElement(name = "NodeType", type = TNodeType.class),
        @XmlElement(name = "NodeTypeImplementation", type = TNodeTypeImplementation.class),
        @XmlElement(name = "RelationshipType", type = TRelationshipType.class),
        @XmlElement(name = "RelationshipTypeImplementation", type = TRelationshipTypeImplementation.class),
        @XmlElement(name = "RequirementType", type = TRequirementType.class),
        @XmlElement(name = "CapabilityType", type = TCapabilityType.class),
        @XmlElement(name = "ArtifactType", type = TArtifactType.class),
        @XmlElement(name = "ArtifactTemplate", type = TArtifactTemplate.class),
        @XmlElement(name = "PolicyType", type = TPolicyType.class),
        @XmlElement(name = "PolicyTemplate", type = TPolicyTemplate.class)
    })
    protected List<TExtensibleElements> serviceTemplateOrNodeTypeOrNodeTypeImplementation;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    /**
     * Gets the value of the extensions property.
     * 
     * @return
     *     possible object is
     *     {@link TDefinitions.Extensions }
     *     
     */
    public TDefinitions.Extensions getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TDefinitions.Extensions }
     *     
     */
    public void setExtensions(TDefinitions.Extensions value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the import property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the import property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TImport }
     * 
     * 
     */
    public List<TImport> getImport() {
        if (_import == null) {
            _import = new ArrayList<TImport>();
        }
        return this._import;
    }

    /**
     * Gets the value of the types property.
     * 
     * @return
     *     possible object is
     *     {@link TDefinitions.Types }
     *     
     */
    public TDefinitions.Types getTypes() {
        return types;
    }

    /**
     * Sets the value of the types property.
     * 
     * @param value
     *     allowed object is
     *     {@link TDefinitions.Types }
     *     
     */
    public void setTypes(TDefinitions.Types value) {
        this.types = value;
    }

    /**
     * Gets the value of the serviceTemplateOrNodeTypeOrNodeTypeImplementation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceTemplateOrNodeTypeOrNodeTypeImplementation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TServiceTemplate }
     * {@link TNodeType }
     * {@link TNodeTypeImplementation }
     * {@link TRelationshipType }
     * {@link TRelationshipTypeImplementation }
     * {@link TRequirementType }
     * {@link TCapabilityType }
     * {@link TArtifactType }
     * {@link TArtifactTemplate }
     * {@link TPolicyType }
     * {@link TPolicyTemplate }
     * 
     * 
     */
    public List<TExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
        if (serviceTemplateOrNodeTypeOrNodeTypeImplementation == null) {
            serviceTemplateOrNodeTypeOrNodeTypeImplementation = new ArrayList<TExtensibleElements>();
        }
        return this.serviceTemplateOrNodeTypeOrNodeTypeImplementation;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the targetNamespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Extension" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtension" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "extension"
    })
    public static class Extensions {

        @XmlElement(name = "Extension", required = true)
        protected List<TExtension> extension;

        /**
         * Gets the value of the extension property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the extension property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExtension().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TExtension }
         * 
         * 
         */
        public List<TExtension> getExtension() {
            if (extension == null) {
                extension = new ArrayList<TExtension>();
            }
            return this.extension;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Types {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * {@link Element }
         * 
         * 
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

    }

}
