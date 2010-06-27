package to.networld.cyberagent;

import to.networld.cyberagent.communication.*;

public class Main {
        public static void main(String [] args) {
                System.out.println("[*] Starting test component...");
                System.out.println(new TestClass("Testing a class from a subproject...").getTestString());
        }
}
