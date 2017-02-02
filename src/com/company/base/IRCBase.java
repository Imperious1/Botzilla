package com.company.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by blaze on 2/1/2017.
 */
public abstract class IRCBase {

    protected abstract void handleResponse(String response);

    protected abstract void onJoin(String username);

    protected abstract void handleIntro();

    protected Socket socket;
    protected String channel;
    protected String url;
    protected String nickname = "Botzilla";
    protected String username = "Botzilla";
    protected String hostname;
    private boolean shouldLoop;

    /**
     * @param url      the IRC url (e.g irc.freenode.net)
     * @param channel  the #channel (e.g #CodeStuff)
     * @param hostname the hostname for your bot (doesn't matter apparently)
     */
    public IRCBase(String url, String channel, String hostname) {
        this.url = url;
        this.channel = channel;
        this.hostname = hostname;
    }

    /**
     * Connects to IRC channel via given URL and PORT
     *
     * @param url  the IRC url (e.g irc.freenode.net)
     * @param port to be used
     * @throws IOException if connection errors occurs
     */
    protected void connect(String url, int port) throws IOException {
        this.socket = new Socket(url, port);
    }

    /**
     * @param port to be used
     * @throws IOException if connection errors occurs
     */
    protected void connect(int port) throws IOException {
        this.socket = new Socket(url, port);
    }

    /**
     * Registers the user with the IRC channel using the supplied information from construction
     *
     * @throws IOException if connection errors occurs
     */
    public void register() throws IOException {
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.write(String.format("USER %s %s %s : %s \n\n", username, hostname, nickname, nickname));
        pw.write(String.format("NICK %s \n\n", nickname));
        pw.write(String.format("JOIN %s \n\n", channel));
        pw.flush();
        receive();
        handleIntro();
    }

    /**
     * Listens for server responses and handles them accordingly
     */
    private void receive() {
        new Thread(() -> {
            try {
                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                byte[] shit = new byte[2048];
                while (shouldLoop && bis.read(shit) != -1) {
                    String response = new String(shit);
                    System.out.println(response);
                    if (response.startsWith("PING")) {
                        sendCommand(String.format("PONG :%s", response.substring(6)));
                    } else if (response.contains(String.format("JOIN %s", channel))) {
                        if (!response.contains(nickname))
                            onJoin(getUsernameFromResponse(response));
                    } else {
                        handleResponse(response);
                    }
                    shit = new byte[2048];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Stops the listener upon next loop (message)
     */
    protected void stop() {
        shouldLoop = false;
    }

    /**
     * Sends a command to the IRC server/channel
     *
     * @param message command to be sent (e.g "JOIN #channel")
     * @throws IOException if connection errors occurs
     */
    protected void sendCommand(String message) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write(message.getBytes());
        bos.flush();
    }

    /**
     * Sends a channel wide message
     *
     * @param message message to be sent (e.g "This is a message")
     * @throws IOException if connection errors occurs
     */
    protected void sendChannelMessage(String message) throws IOException {
        sendCommand(String.format("PRIVMSG %s :%s \n\n", channel, message));
    }

    /**
     * Sends a private message to the user supplied from the response
     *
     * @param response the raw response (message)
     * @throws IOException if connection errors occurs
     */
    protected void sendPrivateMessage(String response) throws IOException {
        String message = response.substring(getMessageStartIndex(response), response.length());
        String username = getUsernameFromResponse(response);
        sendCommand(String.format("PRIVMSG %s :%s \n\n", username, message.substring(9, message.length())));
    }

    /**
     * Gets username from a raw response
     *
     * @param response the raw response (message)
     * @return formatted username
     */
    private String getUsernameFromResponse(String response) {
        return response.substring(1, response.indexOf("!"));
    }

    /**
     * Retrieves starting index of message, so you can get a message out of a response
     *
     * @param response the raw response
     * @return starting index of message
     */
    protected int getMessageStartIndex(String response) {
        return response.lastIndexOf("PRIVMSG") + (channel.length() + 10);
    }

    /**
     * @param shouldLoop Bot will not function without setting this to true first
     */
    public void setShouldLoop(boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
    }

    /**
     * @param nickname your custom nickname for the bot
     * @return the instance
     */
    public IRCBase setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    /**
     * @param username your custom username for the bot
     * @return the instance
     */
    public IRCBase setUsername(String username) {
        this.username = username;
        return this;
    }
}