package net.bjmsw.hal;

import net.bjmsw.hal.model.Buffer;
import net.bjmsw.hal.model.ConsoleBuffer;
import net.bjmsw.hal.model.HALThread;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class halOS {

    private static Map<String, HALThread> threads;

    public static void main(String[] args) throws IOException {
        threads = new HashMap<>();

        // TODO - Better arg parsing


        if (args[0].endsWith(".json")) {
            parseJSONFile(args[0]);
        } else {
            parseProprietaryFile(args[0]);
        }

        threads.forEach((s, halThread) -> {
            halThread.start();
        });
    }

    private static void parseProprietaryFile(String filePath) {
        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lastHalId = 1;
            String line;
            int fileStep = 0;
            System.out.println("Found file. parsing...");
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\\s+", "");

                String[] splitLine = line.split(" ");

                if (line.equalsIgnoreCase("HAL-Prozessoren:")) {
                    fileStep = 1;
                    continue;
                } else if (line.equalsIgnoreCase("HAL-Verbindungen:")) {
                    fileStep = 2;
                    continue;
                }
                if (fileStep == 1 && !line.isEmpty() && line.startsWith(String.valueOf(lastHalId))) {
                    System.out.println("Creating HAL Interpreter: " + lastHalId + " with path: " + line.substring(1, line.length()-1));
                    HALThread t = new HALThread(String.valueOf("lastHalId"), line.substring(1, line.length()-1));
                    threads.put(String.valueOf(lastHalId), t);
                    lastHalId++;
                }
                if (fileStep == 2 && !line.isEmpty()) {
                    System.out.println(line);
                    String[] sp = line.split(">");
                    String[] start = sp[0].split(":");
                    String[] dest = sp[1].split(":");

                    // STDIN Buffer support
                    if (sp[0].equalsIgnoreCase("STDIN")) {
                        if (Integer.parseInt(dest[0])+1 > lastHalId) {
                            System.err.println("The Config files connection section contains HAL IDs that doesn't exist.");
                            System.exit(0);
                        }
                        threads.get(dest[0]).addRBuffer(Integer.parseInt(dest[1]), new ConsoleBuffer());
                        continue;
                    }

                    // STDOUT Buffer support
                    if (sp[1].equalsIgnoreCase("STDOUT")) {
                        if (Integer.parseInt(start[0])+1 > lastHalId) {
                            System.err.println("The Config files connection section contains HAL IDs that doesn't exist.");
                            System.exit(0);
                        }
                        threads.get(start[0]).addSBuffer(Integer.parseInt(start[1]), new ConsoleBuffer());
                        continue;
                    }

                    // check for illegal IDs
                    if (Integer.parseInt(start[0])+1 > lastHalId || Integer.parseInt(dest[0])+1 > lastHalId) {
                        System.err.println("The Config files connection section contains HAL IDs that doesn't exist.");
                        System.exit(0);
                    }

                    // create connections
                    Buffer b = new Buffer();
                    threads.get(start[0]).addSBuffer(Integer.parseInt(start[1]), b);
                    threads.get(dest[0]).addRBuffer(Integer.parseInt(dest[1]), b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Config file malformed!");
        }

    }

    private static void parseJSONFile(String filePath) throws IOException {
        File jsonFile = new File(filePath);
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


    }

}
