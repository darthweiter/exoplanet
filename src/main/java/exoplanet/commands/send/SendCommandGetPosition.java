package exoplanet.commands.send;


import exoplanet.commands.ACommandClass;

public class SendCommandGetPosition extends ACommandClass {

  public final static String CMD_NAME = "getpos";

  public SendCommandGetPosition() {
    super(CMD_NAME);
  }
}
