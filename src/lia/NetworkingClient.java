package lia;

import com.google.protobuf.InvalidProtocolBufferException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static lia.AiApiMessages.*;

/**
 * Handles the connection to the game engine and takes
 * care of sending and retrieving data.
 **/
public class NetworkingClient extends WebSocketClient {

    private Callable myBot;

    private static Exception illegalArgumentsException = new Exception(
            "Illegal arguments. See --help for the correct structure."
    );

    public static NetworkingClient connectNew(String[] args, Callable myBot) throws Exception {
        String playerId = "";
        String port = "8887";

        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            System.out.println("HELP!!");
            return null;
        }

        // Parse arguments
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-p") || arg.equals("--port")) {
                if (i + 1 < args.length) {
                    port = args[i + 1];
                } else {
                    throw illegalArgumentsException;
                }
            }
            else if (arg.equals("-i") || arg.equals("--id")) {
                if (i + 1 < args.length) {
                    playerId = args[i + 1];
                } else {
                    throw illegalArgumentsException;
                }
            }
        }

        // Setup headers
        Map<String,String> httpHeaders = new HashMap<>();
        httpHeaders.put("Id", playerId);

        NetworkingClient c = new NetworkingClient(new URI("ws://localhost:" + port), httpHeaders, myBot);
        c.connect();

        return c;
    }

    private NetworkingClient(URI serverUri, Map<String, String> httpHeaders, Callable myBot) {
        super(serverUri, httpHeaders);
        this.myBot = myBot;
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            Message msg = Message.parseFrom(bytes.array());

            Response response = new Response(msg.getUid());

            // If the message is data about map
            if (msg.hasMapData()) {
                MapData mapData = msg.getMapData();
                myBot.process(mapData);
            }
            // If the message is an update about game state
            else if (msg.hasStateUpdate()) {
                StateUpdate stateUpdate = msg.getStateUpdate();
                myBot.process(stateUpdate, response);
            }

            // Send response back to the game engine
            send(response.toByteArray());

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Exiting...");
        System.exit(0);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {}

    @Override
    public void onMessage(String message) {}
}