package nl.hva.chatapp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class Config {
    public int PORT;

    public Config() {
        JSONParser parser = new JSONParser();

        try {

            int JSONPort;
            URL url = getClass().getResource("settings.json");
            Object obj = parser.parse(new FileReader(url.getPath()));

            JSONObject jsonObject = (JSONObject) obj;

            JSONPort = Math.toIntExact((long) jsonObject.get("port"));
            this.PORT = JSONPort;


        } catch (ParseException | IOException e) {
            e.printStackTrace();
            this.PORT = Main.PORT;
        }
    }
}
