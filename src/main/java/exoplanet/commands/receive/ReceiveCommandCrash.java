package exoplanet.commands.receive;


import exoplanet.commands.ACommandClass;

public class ReceiveCommandCrash extends ACommandClass {
  public final static String CMD_NAME = "crashed";

  public ReceiveCommandCrash() {
    super(CMD_NAME);
  }

  @Override
  public String toString() {
    return CMD_NAME;
  }
}
