/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class InstanceDeploy implements CommandHandler {

    // the id must be serviceID/unitID
    @Argument(index = 0, metaVar = "unitID", usage = "The id of the unit to be deployed, should be serviceID/unitID.", required = true)
    String unitID;

    @Argument(index = 1, metaVar = "number_of_instance", usage = "The number of instance will be create.", required = false)
    int number = 1;

    @Override
    public void execute() {
        String[] ss = unitID.split(unitID);
        if (ss.length == 2) {
            String path = "/services/" + ss[0] + "/nodes/" + ss[1] + "/instance-count/" + number;
            RestHandler.callRest(Main.getSalsaAPI(path), RestHandler.HttpVerb.POST, null, null, null);
        }
    }

    @Override
    public String getCommandDescription() {
        return "Deploy one or more instances of a service unit.";
    }

}
