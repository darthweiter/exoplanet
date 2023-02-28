package exoplanet.commands.send;


import exoplanet.commands.Command;

public class SendCommandGetPosition extends ASendCommand {

  public final static String CMD_NAME = Command.getpos.name();

  public SendCommandGetPosition() {
    super(CMD_NAME);
  }

  @Override
  public String toString() {
    return CMD_NAME;
  }
}
