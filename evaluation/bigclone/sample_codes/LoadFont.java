import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//HEURISTIC: createFont AND registerFont
// 1 - Create the font.  2 - Register the font with the graphics environemnt.

public class LoadFont {
	public static void loadFont1(URL url) throws FontFormatException, IOException {
		InputStream stream = url.openStream();
		Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
	}
	
	public static void loadFont2(File file) throws FontFormatException, IOException {
		FileInputStream stream = new FileInputStream(file);
		Font font = Font.createFont(Font.TYPE1_FONT, stream);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
	}
	
	public static void loadFont3(File file) throws FontFormatException, IOException {
		Font font = Font.createFont(Font.TRUETYPE_FONT, file);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
	}
}
