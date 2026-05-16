package com.example.a_calculatorapp;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Locale;

public class CalculatorEngine {

    public static final String ERROR = "ERROR";

    public String evaluate(String visibleExpression) {
        if (visibleExpression == null || visibleExpression.trim().isEmpty()) {
            return ERROR;
        }

        try {
            String expressionText = prepareExpression(visibleExpression);
            Expression expression = new ExpressionBuilder(expressionText).build();
            double result = expression.evaluate();

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return ERROR;
            }

            return formatResult(result);
        } catch (ArithmeticException | IllegalArgumentException exception) {
            return ERROR;
        }
    }

    private String prepareExpression(String visibleExpression) {
        // Die Anzeige nutzt gut lesbare Symbole, exp4j erwartet normale Operatoren.
        return visibleExpression
                .replace("×", "*")
                .replace("÷", "/")
                .replace(",", ".")
                .trim();
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format(Locale.US, "%d", (long) result);
        }

        return String.format(Locale.US, "%.10f", result)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }
}
