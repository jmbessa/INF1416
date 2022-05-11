package WrittingFileTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//import org.docx4j.Docx4J;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;


public class DocxHelper {
	public static void writeFile( String filePath, byte[] file ) throws Exception {
        Path path = Paths.get(filePath);
        Files.write(path, file);
    }
}
