package exoplanet.commands.send;

import exoplanet.commands.ACommandClass;

public class SendCommandMove extends ACommandClass {

  public final static String CMD_NAME = "move";
  public SendCommandMove() {
    super(CMD_NAME);
  }
}
