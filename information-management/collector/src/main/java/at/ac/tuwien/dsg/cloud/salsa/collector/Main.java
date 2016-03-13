/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.collector.utils.DeliseConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.UpdateGatewayStatus;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import org.slf4j.Logger;

/**
 *
 * @author hungld
 */
public class Main {

    static Logger logger = DeliseConfiguration.getLogger();
    static final MessageClientFactory FACTORY = new MessageClientFactory(DeliseConfiguration.getBroker(), DeliseConfiguration.getBrokerType());

    public static void main(String[] args) throws Exception {
        System.out.println("Starting collector...");

        final MessagePublishInterface pub = FACTORY.getMessagePublisher();

        MessageSubscribeInterface subscribeClientBroadCast = FACTORY.getMessageSubscriber(new SalsaMessageHandling() {
            @Override
            public void handleMessage(DeliseMessage msg) {
                if (msg.getMsgType().equals(DeliseMessage.MESSAGE_TYPE.SYN_REQUEST)) {
                    String payload = DeliseConfiguration.getMeta().toJson();
                    DeliseMessage replyMsg = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.SYN_REPLY, DeliseConfiguration.getMyUUID(), DeliseMessageTopic.REGISTER_AND_HEARBEAT, "", payload);
                    pub.pushMessage(replyMsg);

                }
            }
        }); // end new SalsaMessageHandling

        subscribeClientBroadCast.subscribe(DeliseMessageTopic.CLIENT_REQUEST_DELISE);

        MessageSubscribeInterface subscribeClientUniCast = FACTORY.getMessageSubscriber(new SalsaMessageHandling() {
            @Override
            public void handleMessage(DeliseMessage msg) {
                if (msg.getMsgType().equals(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_SDGATEWAY_LOCAL)) {
                    logger.debug("Server get request for SDG information");
                    try {
                        // response
                        SoftwareDefinedGateway gw = InfoCollector.getGatewayInfo();
                        gw.getCapabilities().addAll(InfoCollector.getGatewayConnectivity());
                        String replyPayload = gw.toJson();
                        DeliseMessage replyMsg = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.UPDATE_INFORMATION, DeliseConfiguration.getMyUUID(), msg.getFeedbackTopic(), "", replyPayload);
                        pub.pushMessage(replyMsg);
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.error(ex.getMessage());
                    }
                }

                if (msg.getMsgType().equals(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_NFV_LOCAL)) {
                    logger.debug("Server get request for SDG information");
                    try {
                        // response
                        VNF vnf = InfoCollector.getRouterInfo();
                        vnf.getConnectivities().addAll(InfoCollector.getGatewayConnectivity());
                        String replyPayload = vnf.toJson();
                        DeliseMessage replyMsg = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.UPDATE_INFORMATION, DeliseConfiguration.getMyUUID(), msg.getFeedbackTopic(), "", replyPayload);
                        pub.pushMessage(replyMsg);
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.error(ex.getMessage());
                    }
                }

                if (msg.getMsgType().equals(DeliseMessage.MESSAGE_TYPE.SUBSCRIBE_SDGATEWAY_LOCAL)) {
                    logger.debug("Client wants to subscribe to this collector");
                    InfoMonitor mon = new InfoMonitor();
                    // this will loop forever, thus need to be implement more, e.g. the timeout
                    while (true) {
                        UpdateGatewayStatus update = mon.getSimulatedUpdate();
                        DeliseMessage replyMsg = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.UPDATE_INFORMATION, DeliseConfiguration.getMyUUID(), msg.getFeedbackTopic(), "", update.toJson());
                        pub.pushMessage(replyMsg);
                    }
                }

                if (msg.getMsgType().equals(DeliseMessage.MESSAGE_TYPE.SUBSCRIBE_SDGATEWAY_LOCAL_SET_PARAM)) {
                    logger.debug("Set parameter for the subscription");
                    // assume that the payload is: rate;simulatedRatio, e.g. 5;0.2
                    String[] settings = msg.getPayload().split(";");
                    InfoMonitor.monitorRate = Integer.parseInt(settings[0]);
                    InfoMonitor.simulatedChangeRatio = Double.parseDouble(settings[1]);

                }
            }

        });
        subscribeClientUniCast.subscribe(DeliseMessageTopic.getCollectorTopicByID(DeliseConfiguration.getMyUUID()));
        subscribeClientUniCast.subscribe(DeliseMessageTopic.getCollectorTopicBroadcast());

        logger.debug("DELISE is ready. UUID: " + DeliseConfiguration.getMyUUID());

    }
}
