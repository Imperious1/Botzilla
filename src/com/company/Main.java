package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Botzilla botzilla = new Botzilla("irc.freenode.net", "#tempBotzillaChat", "Imperios2");
        botzilla.setNickname("Shadohhh");
        botzilla.setUsername("God");
        botzilla.setShouldLoop(true);
        botzilla.connect(6667);
        botzilla.register();
    }
}
