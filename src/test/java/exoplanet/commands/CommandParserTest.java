package exoplanet.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import exoplanet.commands.error.CommandNotFoundException;
import org.junit.jupiter.api.Test;

public class CommandParserTest {

  @Test
  public void toJason_commandOfOrbit_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "orbit:Robotername";
    String expected = "{\"CMD\":\"orbit\",\"NAME\":\"Robotername\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfLand_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "land:POSITION|3|4|SOUTH";
    String expected = "{\"CMD\":\"land\",\"POSITION\":{\"X\":3,\"Y\":4,\"DIRECTION\":\"SOUTH\"}}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfScan_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "scan";
    String expected = "{\"CMD\":\"scan\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfMove_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "move";
    String expected = "{\"CMD\":\"move\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfMoveScan_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "mvscan";
    String expected = "{\"CMD\":\"mvscan\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfRotation_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "rotate:RIGHT";
    String expected = "{\"CMD\":\"rotate\",\"ROTATION\":\"RIGHT\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfExit_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "exit";
    String expected = "{\"CMD\":\"exit\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfGetPosition_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "getpos";
    String expected = "{\"CMD\":\"getpos\"}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_commandOfCharge_returnsJSON()
      throws JsonProcessingException, CommandNotFoundException {
    final String CMD_INPUT = "charge:3";
    String expected = "{\"CMD\":\"charge\",\"DURATION\":3}";

    String result = CommandParser.parse(CMD_INPUT).toJson();

    assertEquals(expected, result);
  }

  @Test
  public void toJason_notKnownCommand_throwsCommandNotFoundException() {
    final String CMD_INPUT = "unknown";

    assertThrows(CommandNotFoundException.class, () -> CommandParser.parse(CMD_INPUT));
  }

  @Test
  public void parseJson_commandOfInit_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"init\",\"SIZE\":{\"WIDTH\":5,\"HEIGHT\":5}}";
    String expected = "init:SIZE|5|5";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }
  @Test
  public void parseJson_commandOfLanded_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"landed\",\"MEASURE\":{\"GROUND\":\"FELS\",\"TEMP\":13.02}}";
    String expected = "landed:MEASURE|FELS|13.02";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfScaned_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"scaned\",\"MEASURE\":{\"GROUND\":\"SAND\",\"TEMP\":-10.05}}";
    String expected = "scaned:MEASURE|SAND|-10.05";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfMoved_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"moved\",\"POSITION\":{\"X\":2,\"Y\":3,\"DIRECTION\":\"EAST\"}}";
    String expected = "moved:POSITION|2|3|EAST";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfMoveScanned_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"mvscaned\",\"MEASURE\":{\"GROUND\":\"PFLANZEN\",\"TEMP\":4.0},\"POSITION\":{\"X\":1,\"Y\":5,\"DIRECTION\":\"WEST\"}}";
    String expected = "mvscaned:MEASURE|PFLANZEN|4.0:POSITION|1|5|WEST";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfRotated_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"rotated\",\"DIRECTION\":\"NORTH\"}";
    String expected = "rotated:NORTH";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfCrashed_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"crashed\"}";
    String expected = "crashed";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfError_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"error\",\"ERROR\":\"Fehler\"}";
    String expected = "error:Fehler";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfPosition_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"pos\",\"POSITION\":{\"X\":4,\"Y\":5,\"DIRECTION\":\"SOUTH\"}}";
    String expected = "pos:POSITION|4|5|SOUTH";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfCharged_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"charged\",\"STATUS\":{\"TEMP\":22.09,\"ENERGY\":99,\"MESSAGE\":\"CHARGE_END\"}}";
    String expected = "charged:22.09|99|CHARGE_END";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_commandOfStatus_returnsCommandAsString()
      throws JsonProcessingException, CommandNotFoundException {
    final String json = "{\"CMD\":\"status\",\"STATUS\":{\"TEMP\":-10.0,\"ENERGY\":1,\"MESSAGE\":\"WARN_LOW_ENERGY|HEATER_ON\"}}";
    String expected = "status:-10.0|1|WARN_LOW_ENERGY|HEATER_ON";

    String result = CommandParser.parseJson(json).toString();

    assertEquals(expected, result);
  }

  @Test
  public void parseJson_notKnownCommand_throwsCommandNotFoundException() {
    final String CMD_INPUT = "unknown";

    assertThrows(CommandNotFoundException.class, () -> CommandParser.parse(CMD_INPUT));
  }

}
