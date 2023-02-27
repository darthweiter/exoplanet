package exoplanet.commands.error;

public class CommandNotFoundException extends Exception{

  public CommandNotFoundException() {
    super("there was no known command on the input");
  }
}
