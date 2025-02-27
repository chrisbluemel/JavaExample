
import Interfaces.Rede;
import NLP.NLPAnnotator;
import database.Extensions.MongoDBProtocolLoader;
import org.apache.uima.resource.ResourceInitializationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static com.mongodb.client.model.Filters.eq;

/**
 * Main class that runs the software.
 *
 * @author Christian Bluemel
 */
public class Main {
    /**
     * Static main method that starts the menu.
     * @param args unused Array of arguments.
     * @throws ParserConfigurationException If the parser messes up.
     * @throws IOException If something is input that was not supposed to be input.
     * @throws SAXException If the SAX parser messes up on a different level.
     * @throws InterruptedException If the IO is interrupted for some reason.
     */
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, InterruptedException, ResourceInitializationException {
        File configPath = new File("C:\\Users\\chris\\Desktop\\ProgPrak\\projects\\uebung3\\Uebung3\\src\\main\\config\\PRG_WiSe22_314.txt");
        Properties properties = new Properties();
        FileInputStream config = new FileInputStream(configPath);
        properties.load(config);
        MongoDBProtocolLoader protocols = new MongoDBProtocolLoader(properties);

        ArrayList<Rede> testSpeeches = protocols.findRedenFilteredBy(eq("sessionID", "42"));


        System.out.println(testSpeeches.size());

        NLPAnnotator anno = new NLPAnnotator();

        //anno.processJCas();

        /*
        Menu menu = new Menu();
        menu.runCommandlineMenu();
         */
    }
}
