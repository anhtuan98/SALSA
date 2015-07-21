/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class SalsaMessage {

    MESSAGE_TYPE msgType;
    
    String fromSalsa;

    String topic;

    String feedbackTopic;

    String payload;

    long timeStamp;

    public SalsaMessage() {
    }

    public SalsaMessage(MESSAGE_TYPE msgType, String fromSalsa, String topic, String feedbackTopic, String payload) {
        this.fromSalsa = fromSalsa;
        this.msgType = msgType;
        this.topic = topic;
        this.feedbackTopic = feedbackTopic;
        this.payload = payload;
        this.timeStamp = System.currentTimeMillis();
    }

    public enum MESSAGE_TYPE {
        discover,   // collect SALSA pioneer
        deploy,     // first time deployment
        reconfigure,     // call an action
        answer,     // state update, error report
        messageReceived,    // simple notify that a message is received
        pioneer_alived,  // a pioneer register itself
        log     // for sending log
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SalsaMessage fromJson(byte[] bytes) {
        return fromJson(new String(bytes, StandardCharsets.UTF_8));
    }

    public static SalsaMessage fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, SalsaMessage.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MESSAGE_TYPE getMsgType() {
        return msgType;
    }

    public String getFromSalsa() {
        return fromSalsa;
    }

    public String getTopic() {
        return topic;
    }

    public String getFeedbackTopic() {
        return feedbackTopic;
    }

    public String getPayload() {
        return payload;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    

    
    @Override
    public String toString() {
        return "SalsaMessage{" + "MsgType=" + msgType + ", payload=" + payload + '}';
    }
    
    
    
}
