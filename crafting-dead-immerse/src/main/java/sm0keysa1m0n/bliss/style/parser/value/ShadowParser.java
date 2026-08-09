package sm0keysa1m0n.bliss.style.parser.value;

import org.jetbrains.annotations.Nullable;
import io.github.humbleui.skija.paragraph.Shadow;
import sm0keysa1m0n.bliss.Color;
import sm0keysa1m0n.bliss.style.parser.ParserException;
import sm0keysa1m0n.bliss.style.parser.StyleReader;

public class ShadowParser {

  @Nullable
  public static Shadow parse(StyleReader reader) throws ParserException {
    var color = ColorParser.parse(reader);

    if (color != null) {
      reader.skipWhitespace();
    }

    Float xOffset;
    Float yOffset;
    Float blurRadius;

    xOffset = reader.readFloat();
    if (xOffset == null) {
      if (color == null) {
        return null;
      }

      throw new ParserException("X-offset expected at index " + reader.getCursor());
    }
    var xOffsetUnit = reader.readUnquotedString();
    if (xOffset != 0 && xOffsetUnit == null) {
      throw new ParserException("X-offset unit expected at index " + reader.getCursor());
    }

    reader.skipWhitespace();

    yOffset = reader.readFloat();
    if (yOffset == null) {
      throw new ParserException("Y-offset expected at index " + reader.getCursor());
    }
    var yOffsetUnit = reader.readUnquotedString();
    if (yOffset != 0 && yOffsetUnit == null) {
      throw new ParserException("Y-offset unit expected at index " + reader.getCursor());
    }

    reader.skipWhitespace();

    blurRadius = reader.readFloat();
    if (blurRadius == null) {
      blurRadius = 0.0F;
    } else {
      var blurRadiusUnit = reader.readUnquotedString();
      if (blurRadius != 0 && blurRadiusUnit == null) {
        throw new ParserException("Blur radius unit expected at index " + reader.getCursor());
      }
      reader.skipWhitespace();
    }

    if (color == null) {
      color = ColorParser.parse(reader);
      if (color == null) {
        color = Color.BLACK;
      }
    }

    return new Shadow(color.valueHex(), xOffset, yOffset, blurRadius);
  }
}
