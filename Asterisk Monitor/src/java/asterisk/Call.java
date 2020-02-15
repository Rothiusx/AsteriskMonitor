/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asterisk;

/**
 *
 * @author rothi
 */
public class Call {
    private String agentChanname;
    private String callerChanname;
    private String queueName;
    private String linkedid;

    public Call(String agentChanname, String callerChanname, String queueName, String linkedid) {
        this.agentChanname = agentChanname;
        this.callerChanname = callerChanname;
        this.queueName = queueName;
        this.linkedid = linkedid;
    }

    public String getAgentChanname() {
        return agentChanname;
    }

    public String getCallerChanname() {
        return callerChanname;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getLinkedid() {
        return linkedid;
    }
}
