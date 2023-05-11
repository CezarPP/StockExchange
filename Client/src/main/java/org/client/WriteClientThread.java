package org.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Objects;

public class WriteClientThread extends Thread {
    private final PrintWriter out;

    WriteClientThread(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void run() {
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // TODO(send commands from interface)
                String request = inputReader.readLine().toLowerCase();
                out.println(request);
                out.flush();
                if (Objects.equals(request, "exit") || Objects.equals(request, "stop")) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
