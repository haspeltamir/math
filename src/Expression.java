//Tamir.D.Haspel 200477115
import java.util.ArrayList;
import java.util.Arrays;

public class Expression {
    String exp;


    public Expression(String exp){
        this.exp=exp;
    }

    public boolean checkValidity() {
        String[] expArray = getPreppedString(exp).split("\\s+");//split to arr// "10 * 125 / 0.5 +  20 - 10"-->{10,*,125,/,0.5,+,20,-,10}
        boolean isStartingWithValidOperator = expArray[0].equals("+") || expArray[0].equals("-");//chack if starts with -/+
        boolean isStartingWithNumericValue = isNumericValue(expArray[0]);//chack if starts with numbers
        boolean isEndingInNumericValue = isNumericValue(expArray[expArray.length - 1]);//chack if ends with numbers
        if (!isStartingWithNumericValue && !isStartingWithValidOperator || !isEndingInNumericValue) {
            return false;
        }
        boolean valuesHaveValidPlacementInExp = valuesHaveValidPlacement(isStartingWithNumericValue,//chacking we get right templet{number,oprator,number...}or {-/+,number..}
                isStartingWithValidOperator, expArray);
        return valuesHaveValidPlacementInExp;
    }

    private String getPreppedString(String exp) {//putting spaces in order to split
        String string1 = exp.trim().replaceAll("\\*", " * ");//"    10*125/0.5+20-10  "-->"10*125/0.5+20-10"--> 10 * 125/0.5+20-10
        String string2 = string1.replaceAll("/", " / "); //"10 * 125/0.5+20-10"--> 10 * 125 / 0.5+20-10
        String string3 = string2.replaceAll("\\+", " + ");//10 * 125 / 0.5+20-10-->10 * 125 / 0.5 + 20-10
        if(exp.startsWith("-")){//10 * 125 / 0.5 + 20-10-->10 * 125 / 0.5 + 20 - 10
            String string4 = string3.replaceAll("-", "- ");
            return string4;
        }
        else {
            String string4 = string3.replaceAll("-", " - ");
            return string4;
        }

    }
    //chacking we get right templet{number,oprator,number...}or {-/+,number..}
    private boolean valuesHaveValidPlacement(boolean isStartingWithNumericValue, boolean isStartingWithValidOperator, String[] expArray) {
//        System.out.println("\nisStartingWithNumericValue " + isStartingWithNumericValue + " | isStartingWithValidOperator " +isStartingWithValidOperator + " | "+ Arrays.toString(expArray));
        if (isStartingWithNumericValue) {
            for (int i = 0; i < expArray.length; i++) {
                if (i%2 != 0 && !Expression.isValidOperator(expArray[i])) {
                    return false;
                }
                if (i%2 == 0 && !this.isNumericValue(expArray[i])) {
                    return false;
                }
            }
        }
        if (isStartingWithValidOperator) {
            for (int i = 0; i < expArray.length; i++) {
                if (i%2 == 0 && !Expression.isValidOperator(expArray[i])) {
                    return false;
                }
                if (i%2 != 0 && !this.isNumericValue(expArray[i])) {
                    return false;
                }
            }
        }
        return true;
    }
//checking valid operator
    private static boolean isValidOperator(String val) {
        String trimmedVal = val.trim();
        if(trimmedVal.equals("+") || trimmedVal.equals("-") || trimmedVal.equals("*") || trimmedVal.equals("/"));
        return true;
    }

    private boolean isNumericValue(String val) {
        try {
            Double.parseDouble(val);
            return true;
        } catch (NumberFormatException err) {
            return false;
        }
    }

    public String calculateExp() {
        boolean isValidExpression = this.checkValidity();
        if (!isValidExpression) {
            return null;
        }
        ////
        String[] expArray = getPreppedString(exp).split("\\s+");
        boolean isStartingWithNumericValue = isNumericValue(expArray[0]);
        ArrayList<Object> parsedExpression = new ArrayList<Object>();//polymorphysem
        for (int i = 0; i < expArray.length; i++) {
            if (isStartingWithNumericValue) {//starts with number
                if (i % 2 == 0) {
                    parsedExpression.add(Double.parseDouble(expArray[i]));
                } else {
                    parsedExpression.add(expArray[i].trim());
                }
            } else { // starting with operator + or -
                if (i == 0) {
                    if (expArray[i].equals("-")) {//{s+,d10,s*,d125,s/,d0.5,s+,d20,s-,d10}
                        parsedExpression.add(-1*(Double.parseDouble(expArray[i+1])));
                    } else { // starts with +
                        parsedExpression.add(Double.parseDouble(expArray[i+1]));
                    }
                    ++i;
                    continue;
                }
                if (i % 2 != 0) {
                    parsedExpression.add(Double.parseDouble(expArray[i]));
                } else {
                    parsedExpression.add(expArray[i].trim());
                }
            }
        }
            // make sure you cualculate * and / before - and +
        while (this.findNextPrioritizedOperatorIndex(parsedExpression) != -1) {
            //System.out.println("Parsed expression >>> " + parsedExpression);
            int nextOperatorIndex = this.findNextPrioritizedOperatorIndex(parsedExpression);
            double tempExp;
            switch (parsedExpression.get(nextOperatorIndex).toString()) {
                case "*":
                    tempExp = Double.parseDouble(parsedExpression.get(nextOperatorIndex - 1).toString()) * Double.parseDouble(parsedExpression.get(nextOperatorIndex + 1).toString());
                    break;
                case "/":
                    // division by zero
                    if (Double.parseDouble(parsedExpression.get(nextOperatorIndex + 1).toString()) == 0) {
                        return null;
                    }
                    tempExp = Double.parseDouble(parsedExpression.get(nextOperatorIndex - 1).toString()) / Double.parseDouble(parsedExpression.get(nextOperatorIndex + 1).toString());
                    break;
                case "+":
                    tempExp = Double.parseDouble(parsedExpression.get(nextOperatorIndex - 1).toString()) + Double.parseDouble(parsedExpression.get(nextOperatorIndex + 1).toString());
                    break;
                case "-":
                    tempExp = Double.parseDouble(parsedExpression.get(nextOperatorIndex - 1).toString()) - Double.parseDouble(parsedExpression.get(nextOperatorIndex + 1).toString());
                    break;
                default: return null;
            }
            for (int i = 0; i < 3; i++) {
                parsedExpression.remove(nextOperatorIndex - 1);
            }

            parsedExpression.add(nextOperatorIndex - 1, tempExp);
        }
       // System.out.println("PARSED EXP FINAL" + parsedExpression);
        return parsedExpression.get(0).toString();
    }
        // find next operator
    private int findNextPrioritizedOperatorIndex(ArrayList<Object> parsedExpression) {
        int indexOfMultiply = parsedExpression.indexOf("*");
        int indexOfDivide = parsedExpression.indexOf("/");
        int indexOfAdd = parsedExpression.indexOf("+");
        int indexOfSubtract = parsedExpression.indexOf("-");
        // case of multiply first
        if (indexOfMultiply >= 0 && indexOfDivide >=0 && indexOfMultiply < indexOfDivide
                || indexOfMultiply >=0 && indexOfDivide < 0) {
            return indexOfMultiply;
        }
        // case of divide first
        if (indexOfDivide >= 0 && indexOfMultiply >= 0 && indexOfDivide < indexOfMultiply
            || indexOfDivide >= 0 && indexOfMultiply < 0) {
            return indexOfDivide;
        }
        // case of add first
        if (indexOfAdd >= 0 && indexOfSubtract >=0 && indexOfAdd < indexOfSubtract
            || indexOfAdd >= 0 && indexOfSubtract < 0) {
            return indexOfAdd;
        }
        // case of subtract first
        if (indexOfSubtract >= 0 && indexOfAdd >=0 && indexOfSubtract < indexOfAdd
            || indexOfSubtract >= 0 && indexOfAdd < 0) {
            return indexOfSubtract;
        }
        return -1;
    }
    //composite method
    public static Expression compositeExp(Expression exp1,
                 Expression exp2, String operator){
        boolean areExpressionsValid = exp1.checkValidity() && exp2.checkValidity();
        boolean isOperatorValid = Expression.isValidOperator(operator);
        if (areExpressionsValid && isOperatorValid) {
            return new Expression(exp1.exp + " " + operator + " " + exp2.exp);
        }
        return null;
    }

    @Override
    public String toString() {
        return exp;
    }
}
