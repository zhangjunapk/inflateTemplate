package libreoffice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import net.sf.jooreports.templates.image.ImageSource;
import net.sf.jooreports.templates.image.RenderedImageSource;
//只支持odt文件
public class JodTest {
	public static void main(String[] args) throws IOException, DocumentTemplateException {
		DocumentTemplateFactory documentTemplateFactory =
				new DocumentTemplateFactory();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		DocumentTemplate template = documentTemplateFactory.getTemplate(new FileInputStream("/media/zhangjun/新加卷/raw_1.odt"));
		Map data = new HashMap();
		data.put("sx_no", "John Doe");
		data.put("userList", createUserList());

		//ImageSource image = new RenderedImageSource(ImageIO.read(classLoader.getResourceAsStream("hamster1.jpg")));
		//ImageSource image = new FileImageSource(imagePath);
		//ImageSource image = new ClasspathImageSource(imagePath);
		//data.put("image", image);

		template.createDocument(data, new FileOutputStream("/media/zhangjun/新加卷/document.odt"));
	}

	private static ArrayList<User> createUserList() {
		ArrayList<User> userList = new ArrayList<>();

		User user = new User();
		user.setFirstName("Mary");
		user.setLastName("Poppins");

		userList.add(user);

		user = new User();
		user.setFirstName("Willy");
		user.setLastName("Wonka");

		userList.add(user);

		return userList;
	}
}
