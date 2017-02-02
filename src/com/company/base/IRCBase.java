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

    private static final int MESSAGE_BEGINNING = 27;

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

    public IRCBase(String url, String channel, String hostname) {
        this.url = url;
        this.channel = channel;
        this.hostname = hostname;
    }

    protected void connect(String url, int port) throws IOException {
        this.socket = new Socket(url, port);
    }

    protected void connect(int port) throws IOException {
        this.socket = new Socket(url, port);
    }

    public void register() throws IOException {
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.write(String.format("USER %s %s %s : %s \n\n", username, hostname, nickname, nickname));
        pw.write(String.format("NICK %s \n\n", nickname));
        pw.write(String.format("JOIN %s \n\n", channel));
        pw.flush();
        receive();
        handleIntro();
    }

    protected void receive() {
        new Thread(() -> {
            try {
                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                byte[] shit = new byte[2048];
                while (shouldLoop && bis.read(shit) != -1) {
                    String response = new String(shit);
                    System.out.println(response);
                    if (response.startsWith("PING")) {
                        sendCommand(String.format("PONG :%s", response.substring(6)));
                    } else if (response.endsWith(String.format("JOIN %s", channel))) { // NOT WORKING as of now
                        System.out.println("Did it work bitch");
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

    protected void stop() {
        shouldLoop = false;
    }

    protected void sendCommand(String message) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write(message.getBytes());
        bos.flush();
    }

    protected void sendChannelMessage(String message) throws IOException {
        sendCommand(String.format("PRIVMSG %s :%s \n\n", channel, message));
    }

    protected void sendPrivateMessage(String response) throws IOException {
        String message = response.substring(getMessageStartIndex(response), response.length());
        String username = getUsernameFromResponse(response);
        sendCommand(String.format("PRIVMSG %s :%s \n\n", username, message.substring(9, message.length())));
    }

    private String getUsernameFromResponse(String response) {
        return response.substring(1, response.indexOf("!"));
    }

    protected int getMessageStartIndex(String message) {
        return message.lastIndexOf("PRIVMSG") + MESSAGE_BEGINNING;
    }

    public void setShouldLoop(boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
    }

    public IRCBase setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public IRCBase setUsername(String username) {
        this.username = username;
        return this;
    }
}