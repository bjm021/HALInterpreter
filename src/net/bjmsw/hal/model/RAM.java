package net.bjmsw.hal.model;

import java.util.HashMap;
import java.util.Map;

public class RAM {

    private final Map<Integer, Register> ramRegisters;

    public RAM(int size) {
        ramRegisters = new HashMap<>();

        for (int i = 0; i < size; i++) {
            ramRegisters.put(i, new Register());
        }
    }

    public void write(int address, float value) {
        try {
            ramRegisters.get(address).setValue(value);
        } catch (Exception e) {
        }
    }

    public Float read(int address) {
        try {
            return ramRegisters.get(address).getValue();
        } catch (Exception e) {
            return null;
        }
    }

}
