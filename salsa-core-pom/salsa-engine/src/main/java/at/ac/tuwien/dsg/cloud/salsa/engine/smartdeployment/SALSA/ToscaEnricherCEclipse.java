/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA;

import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifacts;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Repositories;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import java.io.IOException;
import javax.xml.bind.JAXBException;

/**
 *
 * @author hungld
 */
public class ToscaEnricherCEclipse {
    TDefinitions toscaDef;
    TDefinitions knowledgeBase;
    TTopologyTemplate knowledgeTopo;
    Artifacts artifactList = new Artifacts();
    Repositories repoList = new Repositories();
    
    
    public ToscaEnricherCEclipse(TDefinitions def) {
        this.toscaDef = def;
        try {
            knowledgeBase = ToscaXmlProcess.readToscaFile(ToscaEnricherSALSA.class
                    .getResource("/data/salsa.knowledge.xml").getFile());

            knowledgeTopo = ((TServiceTemplate) knowledgeBase.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0)).getTopologyTemplate();

            artifactList.importFromXML(ToscaEnricherSALSA.class.getResource("/data/salsa.artifacts.xml").getFile());
            repoList.importFromXML(ToscaEnricherSALSA.class.getResource("/data/salsa.repo.xml").getFile());

        } catch (IOException e1) {
            EngineLogger.logger.error("Not found knowledge base file. " + e1);
        } catch (JAXBException e2) {
            EngineLogger.logger.error("Error when parsing knowledge base file. " + e2);
        }
    }
}