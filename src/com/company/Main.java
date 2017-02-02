package com.company;

import com.company.bots.Botzilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        Botzilla botzilla = new Botzilla("irc.freenode.net", "#CodeStuff", "Imperious99");
        botzilla.setNickname("ShadohhhBot");
        botzilla.setUsername("God");
        botzilla.setShouldLoop(true);
        botzilla.connect(6667);
        botzilla.register();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            String shit = br.readLine();
            if(shit.equals("exit 101011")) {
                botzilla.stop();
                break;
            }
            else botzilla.sendMessage(shit);
        }

    }
}
