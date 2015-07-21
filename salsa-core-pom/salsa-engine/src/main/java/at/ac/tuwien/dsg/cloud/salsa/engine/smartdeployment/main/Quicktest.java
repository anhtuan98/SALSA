/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.main;

import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TPolicy;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author hungld
 */
public class Quicktest {

    public static void main(String[] args) throws IOException, JAXBException {
        testGenerateQuelleReq();
    }

    private static void testGenerateQuelleReq() throws IOException, JAXBException {
        TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/celar/integrateCAMF/DataPlay_withReq.tosca");
        SmartDeploymentService sv = new SmartDeploymentService();
        MultiLevelRequirements multiR = sv.generateQuelleRequirementFromCAMFTosca(def);

        StringWriter sw = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance(MultiLevelRequirements.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(multiR, sw);
        System.out.println(sw);

    }

    private static void testReadToscaWithReq() throws JAXBException, IOException {
        TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/celar/integrateCAMF/DataPlay_withReq.tosca");
        for (TNodeTemplate node : ToscaStructureQuery.getNodeTemplateList(def)) {
            if (node.getPolicies() != null) {
                System.out.println("Node: " + node.getId() + ", name: " + node.getName());
                for (TPolicy p : node.getPolicies().getPolicy()) {
                    System.out.println(" - getName: " + p.getName());
                    System.out.println("   - getLocalPart: " + p.getPolicyType().getLocalPart());
                    System.out.println("   - getNamespaceURI: " + p.getPolicyType().getNamespaceURI());
                    System.out.println("   - getPrefix: " + p.getPolicyType().getPrefix());
                }
                System.out.println("---------------- ");
            } else {
                System.out.println("Node: " + node.getId() + ", name: " + node.getName());
                System.out.println("NULL policies");
            }
        }
    }

}
