package net.bjmsw.hal;

import net.bjmsw.hal.io.FileManager;
import net.bjmsw.hal.model.Instruction;
import net.bjmsw.hal.model.InstructionType;
import net.bjmsw.hal.model.RAM;

import java.time.LocalTime;

public class Interpreter {

    public static boolean DEBUG = false;
    public static final int PC_ADDRESS = 0;
    public static final int ACC_ADDRESS = 1;

    public static LocalTime startDate;

    public static void main(String[] args) {

        startDate = LocalTime.now();

        String programFileArgument = "";

        if (args.length == 0) {
            System.err.println("Please specify a File to run!");
            System.exit(0);
        } else if (args.length == 1) {
            programFileArgument = args[0];
        } else if (args.length == 2) {
            if (args[0].equals("-d") || args[0].equals("--debug")) {
                DEBUG = true;
            }
            programFileArgument = args[1];
        } else {
            System.err.println("Unknown argument configuration. Usage: HALInterpreter [-d --debug] file/path.txt");
            System.exit(0);
        }





        // Setup
        RAM ram = new RAM(0xFF);
        ram.write(0, 0); // PC Setup
        FileManager manager = new FileManager(programFileArgument);

        while (true) {
            Instruction i = manager.getInstruction(ram.read(PC_ADDRESS).intValue()); // Get instruction
            ram.write(PC_ADDRESS, ram.read(PC_ADDRESS) + 1); // PC = PC + 1
            if (i == null) {
                if (DEBUG) System.out.println("[DEBUG] Line " + (ram.read(PC_ADDRESS)-1) + " not found, skipping!");
                continue;
            }
            try {
                InstructionType type = InstructionType.valueOf(i.getName());
                if (DEBUG) debugOutput(i, ram);
                InstructionType.runInstruction(type, i.getOperand(), ram);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid instruction: " + i.getName() + " at line " + i.getLineNumber());
                System.exit(0);
            }
        }


    }


    private static void debugOutput(Instruction i, RAM ram) {
        System.out.print("[DEBUG]: Executing " + i.getName() + " ");
        if (i.hasOperand()) {
            System.out.print("with operand " + i.getOperand());
            if (!i.getName().equals("JUMP")) {
                Float f = ram.read(i.getOperand().intValue());
                if (f != null) System.out.println(" with value at [" + i.getOperand() + "] = " + f);
            }
        }
        else System.out.println();

    }

}
