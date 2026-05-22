package com.example.a_calculatorapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final CalculatorEngine calculatorEngine = new CalculatorEngine();
    private final MemoryStore memoryStore = new MemoryStore();

    private TextView displayText;
    private String currentExpression = "0";
    private boolean lastInputWasResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.displayText);
        registerButtonListeners();
        updateDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuSin) {
            insertFunction("sin");
            return true;
        } else if (itemId == R.id.menuCos) {
            insertFunction("cos");
            return true;
        } else if (itemId == R.id.menuTan) {
            insertFunction("tan");
            return true;
        } else if (itemId == R.id.menuSqrt) {
            insertFunction("sqrt");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerButtonListeners() {
        setClickListener(R.id.buttonZero, view -> appendDigit("0"));
        setClickListener(R.id.buttonOne, view -> appendDigit("1"));
        setClickListener(R.id.buttonTwo, view -> appendDigit("2"));
        setClickListener(R.id.buttonThree, view -> appendDigit("3"));
        setClickListener(R.id.buttonFour, view -> appendDigit("4"));
        setClickListener(R.id.buttonFive, view -> appendDigit("5"));
        setClickListener(R.id.buttonSix, view -> appendDigit("6"));
        setClickListener(R.id.buttonSeven, view -> appendDigit("7"));
        setClickListener(R.id.buttonEight, view -> appendDigit("8"));
        setClickListener(R.id.buttonNine, view -> appendDigit("9"));

        setClickListener(R.id.buttonAdd, view -> appendOperator("+"));
        setClickListener(R.id.buttonSubtract, view -> appendOperator("-"));
        setClickListener(R.id.buttonMultiply, view -> appendOperator("×"));
        setClickListener(R.id.buttonDivide, view -> appendOperator("÷"));

        setClickListener(R.id.buttonDecimal, view -> appendDecimal());
        setClickListener(R.id.buttonEquals, view -> calculateResult());
        setClickListener(R.id.buttonClear, view -> clearAll());
        setClickListener(R.id.buttonClearEntry, view -> clearEntry());
        setClickListener(R.id.buttonSign, view -> changeSign());

        setClickListener(R.id.buttonSin, view -> insertFunction("sin"));
        setClickListener(R.id.buttonCos, view -> insertFunction("cos"));
        setClickListener(R.id.buttonTan, view -> insertFunction("tan"));
        setClickListener(R.id.buttonSqrt, view -> insertFunction("sqrt"));

        setClickListener(R.id.buttonMemorySave, view -> memoryStore.saveSlot1(currentExpression));
        setLongClickListener(R.id.buttonMemorySave, view -> {
            memoryStore.saveSlot2(currentExpression);
            return true;
        });

        setClickListener(R.id.buttonMemoryRead, view -> readMemory(memoryStore.readSlot1()));
        setLongClickListener(R.id.buttonMemoryRead, view -> {
            readMemory(memoryStore.readSlot2());
            return true;
        });
    }

    private void setClickListener(int buttonId, View.OnClickListener listener) {
        View button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    private void setLongClickListener(int buttonId, View.OnLongClickListener listener) {
        View button = findViewById(buttonId);
        if (button != null) {
            button.setOnLongClickListener(listener);
        }
    }

    private void appendDigit(String digit) {
        if (CalculatorEngine.ERROR.equals(currentExpression) || lastInputWasResult) {
            currentExpression = "";
            lastInputWasResult = false;
        }

        if ("0".equals(currentExpression)) {
            currentExpression = digit;
        } else {
            currentExpression += digit;
        }

        updateDisplay();
    }

    private void appendDecimal() {
        if (CalculatorEngine.ERROR.equals(currentExpression) || lastInputWasResult) {
            currentExpression = "0";
            lastInputWasResult = false;
        }

        if (endsWithOperator(currentExpression) || currentExpression.endsWith("(")) {
            currentExpression += "0.";
        } else if (!getCurrentNumber().contains(".")) {
            currentExpression += ".";
        }

        updateDisplay();
    }

    private void appendOperator(String operator) {
        if (CalculatorEngine.ERROR.equals(currentExpression)) {
            currentExpression = "0";
        }

        if (endsWithOperator(currentExpression)) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1) + operator;
        } else {
            currentExpression += operator;
        }

        lastInputWasResult = false;
        updateDisplay();
    }

    private void calculateResult() {
        String result = calculatorEngine.evaluate(currentExpression);
        currentExpression = result;
        lastInputWasResult = !CalculatorEngine.ERROR.equals(result);
        updateDisplay();
    }

    private void clearAll() {
        currentExpression = "0";
        lastInputWasResult = false;
        updateDisplay();
    }

    private void clearEntry() {
        if (CalculatorEngine.ERROR.equals(currentExpression) || currentExpression.length() <= 1) {
            clearAll();
            return;
        }

        currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        lastInputWasResult = false;
        updateDisplay();
    }

    private void changeSign() {
        if (CalculatorEngine.ERROR.equals(currentExpression)
                || endsWithOperator(currentExpression)
                || currentExpression.endsWith("(")) {
            return;
        }

        int numberStart = findCurrentNumberStart();
        String currentNumber = currentExpression.substring(numberStart);
        if ("0".equals(currentNumber) || currentNumber.isEmpty()) {
            return;
        }

        if (currentNumber.startsWith("-")) {
            currentExpression = currentExpression.substring(0, numberStart)
                    + currentNumber.substring(1);
        } else {
            currentExpression = currentExpression.substring(0, numberStart)
                    + "-"
                    + currentNumber;
        }

        lastInputWasResult = false;
        updateDisplay();
    }

    private void readMemory(String memoryValue) {
        currentExpression = memoryValue;
        lastInputWasResult = true;
        updateDisplay();
    }

    private void insertFunction(String functionName) {
        if (CalculatorEngine.ERROR.equals(currentExpression) || "0".equals(currentExpression)) {
            currentExpression = functionName + "(";
        } else if (endsWithOperator(currentExpression) || currentExpression.endsWith("(")) {
            currentExpression += functionName + "(";
        } else {
            int numberStart = findCurrentNumberStart();
            String expressionBeforeNumber = currentExpression.substring(0, numberStart);
            String currentNumber = currentExpression.substring(numberStart);
            currentExpression = expressionBeforeNumber + functionName + "(" + currentNumber + ")";
        }

        lastInputWasResult = false;
        updateDisplay();
    }

    private void updateDisplay() {
        displayText.setText(currentExpression);
    }

    private String getCurrentNumber() {
        return currentExpression.substring(findCurrentNumberStart());
    }

    private int findCurrentNumberStart() {
        for (int i = currentExpression.length() - 1; i >= 0; i--) {
            char character = currentExpression.charAt(i);
            if (isOperator(character)) {
                boolean isNegativeSign = character == '-'
                        && (i == 0 || isOperator(currentExpression.charAt(i - 1))
                        || currentExpression.charAt(i - 1) == '(');
                if (!isNegativeSign) {
                    return i + 1;
                }
            } else if (character == '(') {
                return i + 1;
            }
        }

        return 0;
    }

    private boolean endsWithOperator(String text) {
        return !text.isEmpty() && isOperator(text.charAt(text.length() - 1));
    }

    private boolean isOperator(char character) {
        return character == '+' || character == '-' || character == '×' || character == '÷';
    }
}
