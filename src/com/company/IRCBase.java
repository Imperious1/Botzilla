package com.company;

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

    protected void register() throws IOException {
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
                    handleResponse(new String(shit));
                    shit = new byte[2048];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    protected void sendCommand(String message) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write(message.getBytes());
        bos.flush();
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
