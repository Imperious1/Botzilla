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

    /**
     * This method is to handle the server responses/messages, mostly messages.
     * Server related things such as Ping Pong are handled in the base
     *
     * @param response is the raw response from the IRC server
     */
    @Override
    protected void handleResponse(String response) {
        try {
            if (response.contains("PRIVMSG") && response.contains("web/freenode/ip.")) {
                String message = response.substring(getMessageStartIndex(response), response.length());
                System.out.println(message);
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

    /**
     * Allows you to handle when a user joins the IRC channel
     *
     * @param username the username of the user joining (e.g "John")
     */
    @Override
    protected void onJoin(String username) {
        try {
            super.sendChannelMessage(String
                    .format("Welcome, %s! Type \\\\help for a list of commands!", username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the server after the next received message (couldn't be bothered to find a better way)
     */
    @Override
    public void stop() {
        super.stop();
    }

    /**
     * Connects to the IRC server given during object construction
     *
     * @param port to be used
     * @throws IOException if connection errors occurs
     */
    @Override
    public void connect(int port) throws IOException {
        super.connect(port);
    }

    /**
     * Handles the message to be sent upon bot connection, if any.
     */
    @Override
    protected void handleIntro() {
        try {
            super.sendChannelMessage(INTRO_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Public message sending, so it can be used directly by object.sendMessage() in Main
     *
     * @param message the raw message to be sent (e.g "Cj is asian")
     * @throws IOException if connection errors occurs
     */
    public void sendMessage(String message) throws IOException {
        super.sendChannelMessage(message);
    }

    /**
     * Currently only returns one line. To work you would need to send multiple messages per command you wish to reveal
     *
     * @return my own help message, replace the string with your own
     */
    private String getHelpMessage() {
        return "\\\\echoworld message - for public echo of your message \r\r\n\n \\\\echome message - for a private echo";
    }

}
