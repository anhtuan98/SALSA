package at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Capability.class, name = "Capability"),
    @JsonSubTypes.Type(value = ControlPoint.class, name = "ControlPoint")
})
public class VNF {

    /**
     * The VNF usually takes the same name of VM or container.
     */
    String name;

    /**
     * Setting of the router
     */
    String routingProtocol;

    /**
     * List of neighbour router
     */
    List<VNF> peers;

    /**
     * List of live hosts that are allocated. This can hints of which hosts is being routed via this router
     */
    List<String> dns;

    List<ControlPoint> controlPoints;

    /**
     * List of interface which can be configured as access point
     */
    List<AccessPoint> networkInterface;

    public void connectWith(VNF vnf) {
        if (peers == null) {
            peers = new ArrayList<>();
        }
        peers.add(vnf);
    }

    public void disconnectWith(VNF vnf) {
        if (peers != null) {
            peers.remove(vnf);
        }
    }

    public VNF() {
    }

    public VNF(String name, String routingProtocol, AccessPoint networkInterface) {
        this.name = name;
        this.routingProtocol = routingProtocol;
        this.networkInterface = new ArrayList<>();
        this.networkInterface.add(networkInterface);
    }

    public String getRoutingProtocol() {
        return routingProtocol;
    }

    public void setRoutingProtocol(String routingProtocol) {
        this.routingProtocol = routingProtocol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VNF> getPeers() {
        if (peers == null) {
            this.peers = new ArrayList<>();
        }
        return peers;
    }

    public void setPeers(List<VNF> peers) {
        this.peers = peers;
    }

    public List<String> getDns() {
        if (dns == null) {
            dns = new ArrayList<>();
        }
        return dns;
    }

    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    public List<AccessPoint> getNetworkInterface() {
        if (networkInterface == null) {
            networkInterface = new ArrayList<>();
        }
        return networkInterface;
    }

    public void setNetworkInterface(List<AccessPoint> networkInterface) {
        this.networkInterface = networkInterface;
    }

    public List<ControlPoint> getControlPoints() {
        if (controlPoints == null) {
            controlPoints = new ArrayList<>();
        }
        return controlPoints;
    }

    public void setControlPoints(List<ControlPoint> controlPoints) {        
        this.controlPoints = new ArrayList<>();
        this.controlPoints.addAll(controlPoints);        
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public static VNF fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, VNF.class);
        } catch (IOException ex) {
            return null;
        }
    }

}
