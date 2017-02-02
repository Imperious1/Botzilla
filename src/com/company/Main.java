package com.company;

import com.company.bots.Botzilla;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Botzilla botzilla = new Botzilla("irc.freenode.net", "#CodeStuff", "Imperious99");
        botzilla.setNickname("ShadohhhBot");
        botzilla.setUsername("God");
        botzilla.setShouldLoop(true);
        botzilla.connect(6667);
        botzilla.register();
    }
}
