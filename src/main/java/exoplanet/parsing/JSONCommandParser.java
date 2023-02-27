package exoplanet.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exoplanet.commands.error.CommandNotFoundException;
import exoplanet.commands.model.DIRECTION;
import exoplanet.commands.model.Position;
import exoplanet.commands.model.Rotation;
import exoplanet.commands.receive.ReceiveCommandCharged;
import exoplanet.commands.receive.ReceiveCommandCrash;
import exoplanet.commands.receive.ReceiveCommandError;
import exoplanet.commands.receive.ReceiveCommandInit;
import exoplanet.commands.receive.ReceiveCommandLanded;
import exoplanet.commands.receive.ReceiveCommandMoveScaned;
import exoplanet.commands.receive.ReceiveCommandMoved;
import exoplanet.commands.receive.ReceiveCommandPosition;
import exoplanet.commands.receive.ReceiveCommandRotated;
import exoplanet.commands.receive.ReceiveCommandScanned;
import exoplanet.commands.receive.ReceiveCommandStatus;
import exoplanet.commands.send.SendCommandCharge;
import exoplanet.commands.send.SendCommandExit;
import exoplanet.commands.send.SendCommandGetPosition;
import exoplanet.commands.send.SendCommandLand;
import exoplanet.commands.send.SendCommandMove;
import exoplanet.commands.send.SendCommandMoveScan;
import exoplanet.commands.send.SendCommandOrbit;
import exoplanet.commands.send.SendCommandRotate;
import exoplanet.commands.send.SendCommandScan;

public class JSONCommandParser {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String COLON = ":";
  private static final String PIPE = "\\|";

  private static final String CMD = "CMD";


  public static String toJson(String input)
      throws JsonProcessingException, CommandNotFoundException {
    if (input.startsWith(SendCommandOrbit.CMD_NAME)) {
      //orbit:robotname
      String[] commandName = input.split(COLON);
      return mapper.writeValueAsString(new SendCommandOrbit(commandName[1]));
    } else if (input.startsWith(SendCommandLand.CMD_NAME)) {
      //land:POSITION|x|y|direction
      String[] commandName = input.split(COLON);
      String[] position = commandName[1].split(PIPE);
      return mapper.writeValueAsString(new SendCommandLand(
          new Position(Integer.parseInt(position[1]), Integer.parseInt(position[2]),
              DIRECTION.valueOf(position[3]))));
    } else if (input.startsWith(SendCommandScan.CMD_NAME)) {
      //scan
      return mapper.writeValueAsString(new SendCommandScan());
    } else if (input.startsWith(SendCommandMove.CMD_NAME)) {
      //move
      return mapper.writeValueAsString(new SendCommandMove());
    } else if (input.startsWith(SendCommandMoveScan.CMD_NAME)) {
      //mvscan
      return mapper.writeValueAsString(new SendCommandMoveScan());
    } else if (input.startsWith(SendCommandRotate.CMD_NAME)) {
      //rotate:rotation
      String[] commandName = input.split(COLON);
      return mapper.writeValueAsString(new SendCommandRotate(Rotation.valueOf(commandName[1])));
    } else if (input.startsWith(SendCommandExit.CMD_NAME)) {
      //exit
      return mapper.writeValueAsString(new SendCommandExit());
    } else if (input.startsWith(SendCommandGetPosition.CMD_NAME)) {
      //getpos
      return mapper.writeValueAsString(new SendCommandGetPosition());
    } else if (input.startsWith(SendCommandCharge.CMD_NAME)) {
      //charge:duration
      String[] command = input.split(COLON);
      return mapper.writeValueAsString(new SendCommandCharge(Integer.parseInt(command[1])));
    } else {
      throw new CommandNotFoundException();
    }
  }

  public static String parseJson(String json)
      throws JsonProcessingException, CommandNotFoundException {

    JsonNode jsonNode = mapper.readTree(json);
    String command = jsonNode.get(CMD).asText();

    return switch (command) {
      case ReceiveCommandInit.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandInit.class).toString();
      case ReceiveCommandLanded.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandLanded.class).toString();
      case ReceiveCommandMoveScaned.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandMoveScaned.class).toString();
      case ReceiveCommandScanned.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandScanned.class).toString();
      case ReceiveCommandMoved.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandMoved.class).toString();
      case ReceiveCommandRotated.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandRotated.class).toString();
      case ReceiveCommandCrash.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandCrash.class).toString();
      case ReceiveCommandError.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandError.class).toString();
      case ReceiveCommandCharged.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandCharged.class).toString();
      case ReceiveCommandPosition.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandPosition.class).toString();
      case ReceiveCommandStatus.CMD_NAME ->
          mapper.readValue(json, ReceiveCommandStatus.class).toString();
      default -> throw new CommandNotFoundException();
    };
  }
}
