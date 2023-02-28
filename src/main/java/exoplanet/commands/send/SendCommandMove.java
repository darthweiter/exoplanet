package exoplanet.commands.send;

import exoplanet.commands.Command;

public class SendCommandMove extends ASendCommand {

  public final static String CMD_NAME = Command.move.name();
  public SendCommandMove() {
    super(CMD_NAME);
  }

  @Override
  public String toString() {
    return CMD_NAME;
  }
}
