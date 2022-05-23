package net.bjmsw.hal.io;

import net.bjmsw.hal.model.Instruction;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    File programFile;
    InputStream is;
    BufferedReader reader;
    final Map<Integer, Instruction> instructions;
    int instructionsCount = 0;


    public FileManager(String filePath) {
        instructions = new HashMap<>();
        try {
            programFile = new File(filePath);
            is = new FileInputStream(programFile);
            reader = new BufferedReader(new InputStreamReader(is));
            while(reader.ready()) {
                //instructions.add(new Instruction(reader.readLine()));
                String line = reader.readLine();
                if (line.startsWith("//") || line.isEmpty()) continue;
                Instruction i = new Instruction(line);
                instructions.put(i.getLineNumber(), i);
                instructionsCount++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("The specified file does not exist!");
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Something went wrong while reading from the program file!");
            System.exit(0);
        }
    }

    public Instruction getInstruction(Integer number) {
        return instructions.get(number);
    }

    public int getInstructionsCount() {
        return instructionsCount;
    }
}
