package net.bjmsw.hal.model;

import net.bjmsw.hal.Interpreter;
import net.bjmsw.hal.io.FileManager;

import java.util.HashMap;
import java.util.Map;

public class HALThread extends Thread {

    private final Interpreter halInterpreter;
    private final String id;
    private Map<Integer, Buffer> sendingBuffers;
    private Map<Integer, Buffer> receivingBuffers;

    public HALThread(String id, String path) {
        this.id = id;
        halInterpreter = new Interpreter(path, false);
        sendingBuffers = new HashMap<>();
        receivingBuffers = new HashMap<>();
    }

    public void addRBuffer(int port, Buffer b) {
        receivingBuffers.put(port, b);
    }

    public void addSBuffer(int port, Buffer b) {
        sendingBuffers.put(port, b);
    }

    public Buffer getRBuffer(int port) { return receivingBuffers.get(port); }

    public Buffer getSBuffer(int port) { return sendingBuffers.get(port); }

    public Interpreter getHalInterpreter() { return halInterpreter; }

    @Override
    public void run() {
        super.run();

        while (true) {
            FileManager manager = halInterpreter.getManager();
            RAM ram = halInterpreter.getRam();
            Instruction i = manager.getInstruction(ram.read(Interpreter.PC_ADDRESS).intValue()); // Get instruction
            ram.write(Interpreter.PC_ADDRESS, ram.read(Interpreter.PC_ADDRESS) + 1); // PC = PC + 1
            if (i == null) {
                if (Interpreter.DEBUG) System.out.println("[DEBUG] Line " + (ram.read(Interpreter.PC_ADDRESS)-1) + " not found, skipping!");
                continue;
            }
            try {
                InstructionType type = InstructionType.valueOf(i.getName());
                if (Interpreter.DEBUG) Interpreter.debugOutput(i, ram);
                InstructionType.runInstruction(type, i.getOperand(), ram, this);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid instruction: " + i.getName() + " at line " + i.getLineNumber());
                System.exit(0);
            }
        }
    }
}