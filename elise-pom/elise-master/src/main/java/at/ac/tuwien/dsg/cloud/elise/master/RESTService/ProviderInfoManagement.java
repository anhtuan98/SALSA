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
package at.ac.tuwien.dsg.cloud.elise.master.RESTService;

import at.ac.tuwien.dsg.cloud.elise.model.provider.Provider;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * For managing provider data
 * @author Duc-Hung Le
 */
@Path("/provider")
public interface ProviderInfoManagement {
     /* PROVIDER ACCESS */
    @GET
    @Path("/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    Provider getProviderByID(@PathParam("uniqueID") String uniqueID);

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    String addProvider(Provider provider);

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Set<Provider> getProviders();
    
    @GET
    @Path("/test")
    void test();
}
