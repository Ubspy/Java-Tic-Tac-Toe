import java.io.InputStream;

final public class ImageLoader
{
	public static InputStream load(String path)
	{
		InputStream input = ImageLoader.class.getResourceAsStream(path);
		
		if(input == null)
		{
			input = ImageLoader.class.getResourceAsStream("\\" + path);
		}
		
		if(input == null)
		{
			input = ImageLoader.class.getResourceAsStream("/" + path);
		}
		
		return input;
	}
}
