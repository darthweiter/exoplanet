package exoplanet.commands.send;


import exoplanet.commands.Command;

public class SendCommandScan extends ASendCommand {

  public final static String CMD_NAME = Command.scan.name();
  public SendCommandScan() {
    super(CMD_NAME);
  }


  @Override
  public String toString() {
    return CMD_NAME;
  }
}
