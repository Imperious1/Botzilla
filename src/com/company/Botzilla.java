package com.company;

import java.io.IOException;

/**
 * Created by blaze on 2/2/2017.
 */
public class Botzilla extends IRCBase {

    private static final int MESSAGE_BEGINNING = 27;
    private static final String PONG = "PONG :PingPongProtocol";
    private static final String INTRO_MESSAGE = "Welcome to Java, here we have brackets and many other neat functions that Python will never have! Just type a command and learn something new, or if you're new yourself, type /help.";

    public Botzilla(String url, String channel, String hostname) {
        super(url, channel, hostname);
    }

    @Override
    protected void handleResponse(String response) {
        System.out.println(response);
        try {
            if (response.equals("PING"))
                sendCommand(PONG);
            else if (response.contains("PRIVMSG") && response.contains("web/freenode/ip.")) {
                sendChannelMessage(response.substring(getMessageStartIndex(response), response.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleIntro() {
        try {
            sendChannelMessage(INTRO_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendChannelMessage(String message) throws IOException {
        sendCommand(String.format("PRIVMSG %s :%s \n\n", super.channel, message));
    }

    private int getMessageStartIndex(String message) {
        return message.lastIndexOf("PRIVMSG") + MESSAGE_BEGINNING;
    }
}
