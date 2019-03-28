package nl.hva.chatapp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Config extends Main {
    public int PORT;

    public Config() {
        JSONParser parser = new JSONParser();

        try {
            int JSONPort;
            Object obj = parser.parse(new FileReader("/Users/luc_d/IdeaProjects/chatapp/src/nl/hva/chatapp/settings.json"));

            JSONObject jsonObject = (JSONObject) obj;

            JSONPort = Math.toIntExact((long) jsonObject.get("port"));
            this.PORT = JSONPort;

        } catch (ParseException e) {
            e.printStackTrace();
            this.PORT = Main.PORT;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
