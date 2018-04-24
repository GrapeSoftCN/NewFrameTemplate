package Test;

import common.java.httpServer.booter;
import common.java.nlogger.nlogger;

public class TestTemplate {
    public static void main(String[] args) {
        booter booter = new booter();
        try {
            System.out.println("Template");
            System.setProperty("AppName", "Template");
            booter.start(1007);
        } catch (Exception e) {
            nlogger.logout(e);
        }
    }
}
