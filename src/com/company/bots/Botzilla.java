package com.company.bots;

import com.company.base.IRCBase;

import java.io.IOException;

/**
 * Created by blaze on 2/2/2017.
 */
public class Botzilla extends IRCBase {


    private static final String INTRO_MESSAGE = "Welcome to Java, here we have brackets and many other neat functions that Python will never have! Just type a command and learn something new, or if you're new yourself, type \\\\help.";

    public Botzilla(String url, String channel, String hostname) {
        super(url, channel, hostname);
    }

    @Override
    protected void handleResponse(String response) {
        System.out.println(response);
        try {
            if (response.contains("PRIVMSG") && response.contains("web/freenode/ip.")) {
                String message = response.substring(getMessageStartIndex(response), response.length());
                if (message.startsWith("\\\\echoworld"))
                    super.sendChannelMessage(message.substring(12, message.length()));
                else if (message.startsWith("\\\\echome"))
                    super.sendPrivateMessage(response);
                else if (message.startsWith("\\\\help"))
                    super.sendChannelMessage(getHelpMessage());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onJoin(String username) {
        try {
            super.sendChannelMessage(String
                    .format("Welcome, %s! Type \\\\help for a list of commands!", username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(int port) throws IOException {
        super.connect(port);
    }

    @Override
    protected void handleIntro() {
        try {
            super.sendChannelMessage(INTRO_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHelpMessage() {
        return "\\\\echoworld message - for public echo of your message \r\r\n\n \\\\echome message - for a private echo";
    }

}
