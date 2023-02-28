package exoplanet.commands.send;

import exoplanet.commands.Command;

public class SendCommandMoveScan extends ASendCommand {

  public final static String CMD_NAME = Command.mvscan.name();

  public SendCommandMoveScan() {
    super(CMD_NAME);
  }

  @Override
  public String toString() {
    return CMD_NAME;
  }
}
