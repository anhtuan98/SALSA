/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hungld
 */
public class InfoSourceSettings {

    public static enum InformationSourceType {
        // FILE type scan all the files in one folder, recursively
        FILE,        
        // call the REST endpoint
        REST,
        // run system command and get result
        SYSCMD
    }
    
    // by default
    static final String DEFAULT_CONFIG_FILE = "./info-source.conf";
    List<InfoSource> source = new ArrayList<>();

    public InfoSourceSettings() {
    }

    public List<InfoSource> getSource() {
        return source;
    }

    public void setSource(List<InfoSource> source) {
        this.source = source;
    }

    public static class InfoSource {
        InformationSourceType type;        
        String endpoint;
        String transformerClass;
        String settings;

        public InfoSource() {
        }

        public InfoSource(InformationSourceType type, String endpoint, String transformerClass, String settings) {
            this.type = type;
            this.endpoint = endpoint;
            this.transformerClass = transformerClass;
            this.settings = settings;
        }

        public InformationSourceType getType() {
            return type;
        }

        public void setType(InformationSourceType type) {
            this.type = type;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getTransformerClass() {
            return transformerClass;
        }

        public void setTransformerClass(String transformerClass) {
            this.transformerClass = transformerClass;
        }

        public String getSettings() {
            return settings;
        }

        public void setSettings(String settings) {
            this.settings = settings;
        }
        // below functions are hack for the simplicity
        public boolean isVNFResource(){
            String[] values = new String[] {"WeaveRouterResourceDiscovery"};
            String tranformClassName = transformerClass.substring(transformerClass.lastIndexOf(".") + 1);
            return Arrays.asList(values).contains(tranformClassName);
        }
        
        public boolean isGatewayResource(){
            String[] values = new String[] {"SDSensorTranformer","AndroidSensorTransformer","OpenIoTSensorTransformer"};
            String tranformClassName = transformerClass.substring(transformerClass.lastIndexOf(".") + 1);
            return Arrays.asList(values).contains(tranformClassName);
        }

    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static InfoSourceSettings fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, InfoSourceSettings.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static InfoSourceSettings loadFile(String file) {
        String json;
        try {
            json = new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return fromJson(json);
    }

    public static InfoSourceSettings loadDefaultFile() {
        return InfoSourceSettings.loadFile(DEFAULT_CONFIG_FILE);
    }

    public static void main(String[] args) {
        InfoSourceSettings settings = new InfoSourceSettings();
        settings.getSource().add(new InfoSource(InformationSourceType.FILE, "/home/hungld/test/SENSOR_TEST/android.sensor", "at.ac.tuwien.dsg.cloud.salsa.informationmanagement.androidsensortranform.AndroidSensorTransformer", null));
        settings.getSource().add(new InfoSource(InformationSourceType.FILE, "/home/hungld/test/SENSOR_TEST/OpenIoT", "at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor.transformer.OpenIoTSensorTransformer", null));
        settings.getSource().add(new InfoSource(InformationSourceType.FILE, "/opt/iCOMOT/bin/compact/workspace/iCOMOT-Platform/", "at.ac.tuwien.dsg.cloud.salsa.informationmanagement.tranformsdsensor.SDSensorTranformer", "sensor.meta"));
        System.out.println(settings.toJson());
    }

}
