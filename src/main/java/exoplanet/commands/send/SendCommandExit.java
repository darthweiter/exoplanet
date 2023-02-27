package exoplanet.commands.send;

import exoplanet.commands.ACommandClass;

public class SendCommandExit extends ACommandClass {
  public final static String CMD_NAME = "exit";

  public SendCommandExit() {
    super(CMD_NAME);
  }
}
