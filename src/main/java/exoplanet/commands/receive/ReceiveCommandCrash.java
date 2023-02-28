package exoplanet.commands.receive;


import exoplanet.commands.Command;

public class ReceiveCommandCrash extends AReceiveCommand {
  public final static String CMD_NAME = Command.crashed.name();

  public ReceiveCommandCrash() {
    super(CMD_NAME);
  }

  @Override
  public String toString() {
    return CMD_NAME;
  }
}
