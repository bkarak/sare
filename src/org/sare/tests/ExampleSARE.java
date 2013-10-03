package org.sare.tests;

import org.sare.IGroupAction;
import org.sare.RegexParser;


public class ExampleSARE {
    public static void main(String[] args) {
        String regex = "([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)";
        String data  = "192.255.255.150";

        RegexParser sare = new RegexParser(regex);
        ConsoleOut co = new ConsoleOut();

        sare.assignAction(0, co);
        sare.assignAction(1, co);
        sare.assignAction(2, co);
        sare.assignAction(3, co);
        sare.assignAction(4, co);

        sare.process(data);
    }
}

class ConsoleOut implements IGroupAction {
    public void start() {
        System.out.println("Starting");
    }

    public void end() {
        System.out.println("End");
    }

    public void match(int count, String full, String group) {
        System.out.println("[" + count + "] - " + group);
    }
}
