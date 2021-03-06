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
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;


/**
 *
 * @author Duc-Hung Le
 */
public class IdentificationManager {

    static Logger logger = EliseConfiguration.logger;
    static ObjectMapper mapper = new ObjectMapper();
    static File storage = new File(EliseConfiguration.IDENTIFICATION_MAPPING_FILE);

    public static IdentificationDB load() {
        if (!storage.exists()) {
            try {
                storage.createNewFile();
            } catch (IOException ex) {
                logger.error("Cannot create identification DB. Error: " + ex);
            }
        } else {
            try {
                return (IdentificationDB) mapper.readValue(storage, IdentificationDB.class);
            } catch (IOException ex) {
                logger.debug("Identification DB is empty, create a new one! Message: " + ex);
            }
        }
        return new IdentificationDB();
    }

    public void deleteAndUpdate(String globalID) {
        IdentificationDB currentDB = load();
        currentDB.removeGlobalID(globalID);
    }

    public List<GlobalIdentification> searchAndUpdate(LocalIdentification entityComposedID, String possibleGlobalID) {
        // search if there is an exist service identification that "equals" the the provided. Node: equals function is defined
        IdentificationDB currentDB = load();
        List<GlobalIdentification> existGlobals = new ArrayList<>();
        for (GlobalIdentification ite : currentDB.getIdentifications()) {
            if (ite.addLocalIdentification(entityComposedID)) {
                existGlobals.add(ite);
            }
        }

        // update        
        if (existGlobals.isEmpty()) {  // if there is no existSI in the DB, create one
            System.out.println("There is no exist SI match with Identification, generating one...:" + entityComposedID);
            GlobalIdentification newGlobal = new GlobalIdentification();
            newGlobal.setUuid(possibleGlobalID);
            newGlobal.addLocalIdentification(entityComposedID); // add again
            existGlobals.add(newGlobal);
            currentDB.hasIdentification(newGlobal);
        }

        // and save to file        
        try {
            mapper.writeValue(storage, currentDB);
        } catch (IOException ex) {
            logger.error("Cannot save Identification. Error: " + ex);
            ex.printStackTrace();
        }
        return existGlobals;

    }

    //  private synchronized void save(IdentificationDB idb)
//  {
//    IdentificationDB currentDB = load();
//    currentDB.getIdentifications().addAll(idb.getIdentifications());
//    try
//    {
//      mapper.writeValue(storage, currentDB);
//    }
//    catch (IOException ex)
//    {
//      logger.error("Cannot save Identification. Error: " + ex);
//    }
//  }
}
