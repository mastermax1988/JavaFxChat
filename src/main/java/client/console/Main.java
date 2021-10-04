package client.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args) {
    ShellUI shellUI = new ShellUI();
    shellUI.start();
  }

}
