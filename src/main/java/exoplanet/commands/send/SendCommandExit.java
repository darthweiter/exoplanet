package exoplanet.commands.send;

import exoplanet.commands.Command;

public class SendCommandExit extends ASendCommand {
  public final static String CMD_NAME = Command.exit.name();

  public SendCommandExit() {
    super(CMD_NAME);
  }

  @Override
  public String toString() {
    return CMD_NAME;
  }
}
