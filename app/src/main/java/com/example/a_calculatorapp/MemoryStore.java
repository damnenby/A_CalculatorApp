package com.example.a_calculatorapp;

public class MemoryStore {

    private String memorySlot1 = "0";
    private String memorySlot2 = "0";

    public void saveSlot1(String value) {
        memorySlot1 = normalizeValue(value);
    }

    public void saveSlot2(String value) {
        memorySlot2 = normalizeValue(value);
    }

    public String readSlot1() {
        return memorySlot1;
    }

    public String readSlot2() {
        return memorySlot2;
    }

    private String normalizeValue(String value) {
        if (value == null || value.trim().isEmpty() || CalculatorEngine.ERROR.equals(value)) {
            return "0";
        }

        return value.trim();
    }
}
