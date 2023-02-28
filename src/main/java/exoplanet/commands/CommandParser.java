package exoplanet.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exoplanet.commands.error.CommandNotFoundException;
import exoplanet.commands.model.DIRECTION;
import exoplanet.commands.model.GROUND;
import exoplanet.commands.model.Measure;
import exoplanet.commands.model.Position;
import exoplanet.commands.model.Rotation;
import exoplanet.commands.model.Size;
import exoplanet.commands.model.Status;
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

public class CommandParser {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String CMD = "CMD";

  public static ACommandClass parse(String input) throws CommandNotFoundException {
    String[] command = input.split(":");
    String[] arguments;
    try {
      switch (Command.valueOf(command[0])) {
        case orbit -> {
          return new SendCommandOrbit(command[1]);
        }
        case init -> {
          arguments = command[1].split("\\|");
          return new ReceiveCommandInit(
              new Size(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])));
        }
        case land -> {
          arguments = command[1].split("\\|");
          return new SendCommandLand(
              new Position(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]),
                  DIRECTION.valueOf(arguments[3])));
        }
        case landed -> {
          arguments = command[1].split("\\|");
          return new ReceiveCommandLanded(
              new Measure(GROUND.valueOf(arguments[1]), Float.parseFloat(arguments[2])));
        }
        case scan -> {
          return new SendCommandScan();
        }
        case scaned -> {
          arguments = command[1].split("\\|");
          return new ReceiveCommandScanned(
              new Measure(GROUND.valueOf(arguments[1]), Float.parseFloat(arguments[2])));
        }
        case move -> {
          return new SendCommandMove();
        }
        case moved -> {
          arguments = command[1].split("\\|");
          return new ReceiveCommandMoved(
              new Position(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]),
                  DIRECTION.valueOf(arguments[3])));
        }
        case mvscan -> {
          return new SendCommandMoveScan();
        }
        case mvscaned -> {
          arguments = command[1].split("\\|");
          String[] moreArguments = command[2].split("\\|");
          return new ReceiveCommandMoveScaned(
              new Measure(GROUND.valueOf(arguments[1]), Float.parseFloat(arguments[2])),
              new Position(Integer.parseInt(moreArguments[1]), Integer.parseInt(moreArguments[2]),
                  DIRECTION.valueOf(moreArguments[3])));
        }
        case rotate -> {
          return new SendCommandRotate(Rotation.valueOf(command[1]));
        }
        case rotated -> {
          return new ReceiveCommandRotated(DIRECTION.valueOf(command[1]));
        }
        case crashed -> {
          return new ReceiveCommandCrash();
        }
        case exit -> {
          return new SendCommandExit();
        }
        case error -> {
          return new ReceiveCommandError(command[1]);
        }
        case getpos -> {
          return new SendCommandGetPosition();
        }
        case pos -> {
          arguments = command[1].split("\\|");
          return new ReceiveCommandPosition(
              new Position(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]),
                  DIRECTION.valueOf(arguments[3])));
        }
        case charge -> {
          return new SendCommandCharge(Integer.parseInt(command[1]));
        }
        case charged -> {
          arguments = command[1].split("\\|");
          StringBuilder msg = new StringBuilder();
          for (int i = 2; i < arguments.length; i++) {
            msg.append(arguments[i]);
            if (i != arguments.length - 1) {
              msg.append("|");
            }
          }
          return new ReceiveCommandCharged(
              new Status(Float.parseFloat(arguments[0]), Integer.parseInt(arguments[1]),
                  msg.toString()));
        }
        case status -> {
          arguments = command[1].split("\\|");
          StringBuilder msg = new StringBuilder();
          for (int i = 2; i < arguments.length; i++) {
            msg.append(arguments[i]);
            if (i != arguments.length - 1) {
              msg.append("|");
            }
          }
          return new ReceiveCommandStatus(
              new Status(Float.parseFloat(arguments[0]), Integer.parseInt(arguments[1]),
                  msg.toString()));
        }
        default -> throw new CommandNotFoundException();
      }
    } catch (IllegalArgumentException e) {
      throw new CommandNotFoundException();
    }
  }

  public static ACommandClass parseJson(String json)
      throws JsonProcessingException, CommandNotFoundException {
    JsonNode jsonNode = mapper.readTree(json);
    String command = jsonNode.get(CMD).asText();

    return switch (Command.valueOf(command)) {
      case init -> mapper.readValue(json, ReceiveCommandInit.class);
      case landed -> mapper.readValue(json, ReceiveCommandLanded.class);
      case mvscaned -> mapper.readValue(json, ReceiveCommandMoveScaned.class);
      case scaned -> mapper.readValue(json, ReceiveCommandScanned.class);
      case moved -> mapper.readValue(json, ReceiveCommandMoved.class);
      case rotated -> mapper.readValue(json, ReceiveCommandRotated.class);
      case crashed -> mapper.readValue(json, ReceiveCommandCrash.class);
      case error -> mapper.readValue(json, ReceiveCommandError.class);
      case charged -> mapper.readValue(json, ReceiveCommandCharged.class);
      case pos -> mapper.readValue(json, ReceiveCommandPosition.class);
      case status -> mapper.readValue(json, ReceiveCommandStatus.class);
      default -> throw new CommandNotFoundException();
    };
  }
}
