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
        String expressionText = visibleExpression
                .replace("×", "*")
                .replace("÷", "/")
                .replace(",", ".")
                .trim();

        return closeOpenParentheses(expressionText);
    }

    private String closeOpenParentheses(String expressionText) {
        int openParentheses = 0;
        int closeParentheses = 0;

        for (int i = 0; i < expressionText.length(); i++) {
            char character = expressionText.charAt(i);
            if (character == '(') {
                openParentheses++;
            } else if (character == ')') {
                closeParentheses++;
            }
        }

        StringBuilder completedExpression = new StringBuilder(expressionText);
        for (int i = closeParentheses; i < openParentheses; i++) {
            completedExpression.append(")");
        }

        return completedExpression.toString();
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
