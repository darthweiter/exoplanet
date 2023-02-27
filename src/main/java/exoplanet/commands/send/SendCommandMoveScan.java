package exoplanet.commands.send;

import exoplanet.commands.ACommandClass;

public class SendCommandMoveScan extends ACommandClass {

  public final static String CMD_NAME = "mvscan";

  public SendCommandMoveScan() {
    super(CMD_NAME);
  }
}
