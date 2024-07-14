package com.example.calculator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView displayText;
    private StringBuilder input;
    private boolean isResultShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.display_text);
        input = new StringBuilder();
        isResultShown = false;
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        if (isResultShown && !isOperator(buttonText)) {
            clearInput();
        }

        switch (buttonText) {
            case "=":
                calculateResult();
                break;
            case "C":
                clearInput();
                break;
            default:
                updateInput(buttonText);
                break;
        }
    }

    private void clearInput() {
        input.setLength(0);
        displayText.setText("");
        isResultShown = false;
    }

    private void updateInput(String newText) {
        input.append(newText);
        displayText.setText(input.toString());
    }

    private void calculateResult() {
        String expression = input.toString();
        String result;
        try {
            result = evaluateExpression(expression);
        } catch (Exception e) {
            result = "Error";
        }
        displayText.setText(result);
        isResultShown = true;
    }

    private String evaluateExpression(String expression) {
        // Implement actual evaluation logic here
        // For simplicity, using Java's built-in evaluation
        return String.valueOf(eval(expression));
    }

    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    private boolean isOperator(String buttonText) {
        return buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("*") || buttonText.equals("/");
    }
}
