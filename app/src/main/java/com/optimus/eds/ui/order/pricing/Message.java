package com.optimus.eds.ui.order.pricing;

public class Message {

    public Integer MessageSeverityLevel; //1. Error, 2. Warning, 3. Message
    public String MessageText;

    public Integer getMessageSeverityLevel() {
        return MessageSeverityLevel;
    }

    public void setMessageSeverityLevel(Integer messageSeverityLevel) {
        MessageSeverityLevel = messageSeverityLevel;
    }

    public String getMessageText() {
        return MessageText;
    }

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }


}
