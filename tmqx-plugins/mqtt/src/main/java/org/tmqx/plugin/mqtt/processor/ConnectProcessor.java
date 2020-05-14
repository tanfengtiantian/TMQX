package org.tmqx.plugin.mqtt.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.remoting.session.ClientSession;
import org.tmqx.remoting.session.ConnectManager;
import org.tmqx.plugin.mqtt.util.MessageUtil;
import org.tmqx.plugin.mqtt.util.NettyUtil;
import org.tmqx.remoting.netty.RequestProcessor;
import org.tmqx.remoting.util.RemotingHelper;

public class ConnectProcessor implements RequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(ConnectProcessor.class);

    public ConnectProcessor() {

    }

    @Override
    public void processRequest(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttConnectMessage connectMessage = (MqttConnectMessage)mqttMessage;
        MqttConnectReturnCode returnCode = null;
        int mqttVersion = connectMessage.variableHeader().version();
        String clientId = connectMessage.payload().clientIdentifier();
        boolean cleansession = connectMessage.variableHeader().isCleanSession();
        String userName = connectMessage.payload().userName();
        byte[] password = connectMessage.payload().passwordInBytes();
        ClientSession clientSession = null;
        boolean sessionPresent = false;
        try{
            if(!versionValid(mqttVersion)){
                returnCode = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
            } else if(!clientIdVerfy(clientId)){
                returnCode = MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
            } else if(onBlackList(RemotingHelper.getRemoteAddr(ctx.channel()),clientId)){
                returnCode = MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED;
            } else if(!authentication(clientId,userName,password)){
                returnCode = MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD;
            } else{
                int heartbeatSec = connectMessage.variableHeader().keepAliveTimeSeconds();
                if(!keepAlive(clientId,ctx,heartbeatSec)){
                    log.warn("[CONNECT] -> set heartbeat failure,clientId:{},heartbeatSec:{}",clientId,heartbeatSec);
                    throw new Exception("set heartbeat failure");
                }

                if(cleansession){
                    clientSession = createNewClientSession(clientId,ctx);
                    sessionPresent = false;
                }else{
                    clientSession = new ClientSession(clientId,false,ctx);
                    sessionPresent = false;
                }
                boolean willFlag = connectMessage.variableHeader().isWillFlag();
                if(willFlag){
                    //存储消息
                }
                returnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;
                NettyUtil.setClientId(ctx.channel(),clientId);
                ConnectManager.getInstance().putClient(clientId,clientSession);
            }
            MqttConnAckMessage ackMessage = MessageUtil.getConnectAckMessage(returnCode,sessionPresent);
            ctx.writeAndFlush(ackMessage);
            if(returnCode != MqttConnectReturnCode.CONNECTION_ACCEPTED){
                ctx.close();
                log.warn("[CONNECT] -> {} connect failure,returnCode={}",clientId,returnCode);
                return;
            }
            log.info("[CONNECT] -> {} connect to this mqtt server",clientId);

        }catch(Exception ex){
            log.warn("[CONNECT] -> Service Unavailable: cause={}",ex);
            returnCode = MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE;
            MqttConnAckMessage ackMessage = MessageUtil.getConnectAckMessage(returnCode,sessionPresent);
            ctx.writeAndFlush(ackMessage);
            ctx.close();
        }


        /*
        MqttConnAckMessage ackMessage = MessageUtil.getConnectAckMessage(MqttConnectReturnCode.CONNECTION_ACCEPTED,false);
        ctx.writeAndFlush(ackMessage);

         */
    }

    private ClientSession createNewClientSession(String clientId,ChannelHandlerContext ctx){
        ClientSession clientSession = new ClientSession(clientId,true);
        clientSession.setCtx(ctx);
        return clientSession;
    }

    private boolean keepAlive(String clientId,ChannelHandlerContext ctx,int heatbeatSec){
        if(verfyHeartbeatTime(clientId,heatbeatSec)){
            int keepAlive = (int)(heatbeatSec * 1.5f);
            if(ctx.pipeline().names().contains("idleStateHandler")){
                ctx.pipeline().remove("idleStateHandler");
            }
            ctx.pipeline().addFirst("idleStateHandler",new IdleStateHandler(keepAlive,0,0));
            return true;
        }
        return false;
    }

    private boolean verfyHeartbeatTime(String clientId, int heatbeatSec) {
        return true;
    }

    private boolean authentication(String clientId,String username,byte[] password){
        return true;
    }

    private boolean onBlackList(String remoteAddr,String clientId){
        return false;
    }

    private boolean clientIdVerfy(String clientId){
        return true;
    }

    private boolean versionValid(int mqttVersion){
        if(mqttVersion == 3 || mqttVersion == 4){
            return true;
        }
        return false;
    }
}
