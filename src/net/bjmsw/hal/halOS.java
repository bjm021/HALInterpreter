package net.bjmsw.hal;

import net.bjmsw.hal.model.Buffer;
import net.bjmsw.hal.model.HALThread;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class halOS {

    private static Map<String, HALThread> threads;

    public static void main(String[] args) throws IOException {
        threads = new HashMap<>();

        // TODO - Better arg parsing
        File jsonFile = new File(args[0]);
        String content = Files.readString(jsonFile.toPath(), StandardCharsets.US_ASCII);
        JSONObject root = new JSONObject(content);

        JSONArray hals = root.getJSONArray("hal");
        hals.forEach(o -> {
            JSONObject hal = (JSONObject) o;
            HALThread t = new HALThread(hal.getString("id"), hal.getString("program-file"));
            threads.put(hal.getString("id"), t);
        });

        JSONArray connections = root.getJSONArray("connections");
        connections.forEach(o -> {
            JSONObject conn = (JSONObject) o;

            // Create Buffer
            Buffer b = new Buffer();

            // Add to corresponding idsw
            threads.get(conn.getString("startID")).addSBuffer(conn.getInt("startPort"), b);
            threads.get(conn.getString("destID")).addRBuffer(conn.getInt("destPort"), b);

        });

        threads.forEach((s, halThread) -> {
            halThread.start();
        });

    }

}
