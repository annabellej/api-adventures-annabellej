import org.glassfish.grizzly.http.server.HttpServer;
import student.server.AdventureResource;
import student.server.AdventureServer;

import java.io.IOException;

/**
 * Main class that runs the game Kidnapped! on local server.
 */
public class Main {
    /**
     * Main method initiating and running adventure game on a local server.
     *
     * @param args command line arguments.
     *
     * @throws IOException if server unable to be initiated.
     */
    public static void main(String[] args) throws IOException {
        HttpServer server = AdventureServer.createServer(AdventureResource.class);
        server.start();
    }
}