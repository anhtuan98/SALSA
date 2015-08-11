/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.CollectorInterface;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.Set;

/**
 *
 * @author Duc-Hung Le
 */
public abstract class UnitInstanceCollector extends GenericCollector {

    public abstract Set<UnitInstance> collectAllInstance();

    public abstract UnitInstance collectInstanceByID(ServiceCategory instanceType, String domainID);

    public abstract LocalIdentification identify(UnitInstance paramUnitInstance);
    
    
}
