import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

class Temperature {
    private HashMap<String, FuzzySet> xCoordinate;
    private HashMap<String, FuzzySet> degreeValue;
    private HashMap<String, FuzzySet> turnDegree;
    private ArrayList<String> nameList;
    private ArrayList<Integer> numberList;
    private FuzzyLogic fuzzyLogic;
    private double temp;
    private double xCoord;
    private String name[] = {"Small enough", "Small", "Average", "Big", "Big enough"};
    private String nameDegree[] = {"Very small", "Small enough", "Small", "Average", "Big", "Big enough", "Very big"};
    private double resultCoordinate[];
    private double resultWheelDeg[];
    private double[] resultTurnDeg;
    private double diffuseValue;

    Temperature() {
        // prepare the fuzzy logic processor   
        fuzzyLogic = new FuzzyLogic();
        xCoordinate = new HashMap<>();
        degreeValue = new HashMap<>();
        turnDegree = new HashMap<>();
        nameList = new ArrayList<>();
        numberList = new ArrayList<>();
        fuzzyLogic.clear();

        xCoordinate.put("Small enough", new FuzzySet(-150, -45, -150, 1));
        xCoordinate.put("Small", new FuzzySet(-90, 0, -30, 1));
        xCoordinate.put("Average", new FuzzySet(-30, 30, 0, 1));
        xCoordinate.put("Big", new FuzzySet(0, 90, 30, 1));
        xCoordinate.put("Big enough", new FuzzySet(45, 150, 150, 1));

        degreeValue.put("Very small", new FuzzySet(-205, -90, -135, 1));
        degreeValue.put("Small enough", new FuzzySet(-135, -30, -90, 1));
        degreeValue.put("Small", new FuzzySet(-90, 0, -30, 1));
        degreeValue.put("Average", new FuzzySet(-15, 15, 0, 1));
        degreeValue.put("Big", new FuzzySet(0, 90, 30, 1));
        degreeValue.put("Big enough", new FuzzySet(30, 135, 90, 1));
        degreeValue.put("Very big", new FuzzySet(90, 205, 135, 1));

        turnDegree.put("Very small", new FuzzySet(-45, -15, -45, 1));
        turnDegree.put("Small enough", new FuzzySet(-45, -7.5, -15, 1));
        turnDegree.put("Small", new FuzzySet(-15, 0, -7.5, 1));
        turnDegree.put("Average", new FuzzySet(-7.5, 7.5, 0, 1));
        turnDegree.put("Big", new FuzzySet(0, 15, 7.5, 1));
        turnDegree.put("Big enough", new FuzzySet(7.5, 45, 15, 1));
        turnDegree.put("Very big", new FuzzySet(15, 45, 45, 1));


        for (int j = 0; j < xCoordinate.size(); j++) {
            fuzzyLogic.add(xCoordinate.get(name[j]));

        }

        temp = -150;
        xCoord = -125;
        resultCoordinate = fuzzyLogic.evaluate(xCoord);

        fuzzyLogic.clear();
        for (int j = 0; j < degreeValue.size(); j++) {
            fuzzyLogic.add(degreeValue.get(nameDegree[j]));
        }
        resultWheelDeg = fuzzyLogic.evaluate(temp);
        resultTurnDeg = new double[nameDegree.length];

        ruleBase();

        evaluate(resultTurnDeg);
        System.out.println(diffuseValue);
    }

    private void ruleBase() {
        if (xCoordinate.get(name[0]).isMember(xCoord) && degreeValue.get(nameDegree[0]).isMember(temp)) {
            resultTurnDeg[6] = Math.min(resultCoordinate[0], resultWheelDeg[0]);
        }
        if (xCoordinate.get(name[0]).isMember(xCoord) && degreeValue.get(nameDegree[6]).isMember(temp)) {
            resultTurnDeg[5] = Math.min(resultCoordinate[0], resultWheelDeg[6]);
        }
        if (xCoordinate.get(name[1]).isMember(xCoord) && degreeValue.get(nameDegree[5]).isMember(temp)) {
            if (resultTurnDeg[5] != 0) {
                resultTurnDeg[5] = Math.max(Math.min(resultCoordinate[1], resultWheelDeg[5]), resultTurnDeg[5]);
            } else resultTurnDeg[5] = Math.min(resultCoordinate[1], resultWheelDeg[5]);
        }
        if (xCoordinate.get(name[1]).isMember(xCoord) && degreeValue.get(nameDegree[4]).isMember(temp)) {
            if (resultTurnDeg[5] != 0) {
                resultTurnDeg[5] = Math.max(Math.min(resultCoordinate[1], resultWheelDeg[4]), resultTurnDeg[5]);
            } else resultTurnDeg[5] = Math.min(resultCoordinate[1], resultWheelDeg[4]);
        }
        if (xCoordinate.get(name[2]).isMember(xCoord) && degreeValue.get(nameDegree[4]).isMember(temp)) {
            if (resultTurnDeg[5] != 0) {
                resultTurnDeg[5] = Math.max(Math.min(resultCoordinate[2], resultWheelDeg[4]), resultTurnDeg[5]);
            } else resultTurnDeg[5] = Math.min(resultCoordinate[2], resultWheelDeg[4]);
        }
        if (xCoordinate.get(name[2]).isMember(xCoord) && degreeValue.get(nameDegree[3]).isMember(temp)) {
            resultTurnDeg[3] = Math.min(resultCoordinate[2], resultWheelDeg[3]);
        }
    }

    int getSize(int Number) {
        if (Number == 0)
            return xCoordinate.size();
        else if (Number == 1)
            return degreeValue.size();
        else if (Number == 2)
            return turnDegree.size();
        else return 0;
    }

    FuzzySet getSet(String name, int Number) {
        if (Number == 0)
            return xCoordinate.get(name);
        else if (Number == 1)
            return degreeValue.get(name);
        else
            return turnDegree.get(name);

    }

    double[] getResult(int Number) {
        if (Number == 0)
            return resultCoordinate;
        else if (Number == 1)
            return resultWheelDeg;
        else return resultTurnDeg;
    }

    double getTemp(int Number) {
        if (Number == 0)
            return xCoord;
        else
            return temp;
    }

    void coordinateValueChange(double value) {
        fuzzyLogic.clear();
        for (int j = 0; j < xCoordinate.size(); j++) {
            fuzzyLogic.add(xCoordinate.get(name[j]));

        }
        xCoord = value;
        resultCoordinate = fuzzyLogic.evaluate(xCoord);
        resultTurnDeg = new double[nameDegree.length];

        ruleBase();

        evaluate(resultTurnDeg);
        System.out.println(diffuseValue);
    }

    private void evaluate(double[] result) {

        nameList.clear();
        numberList.clear();
        for (int i = 0; i < result.length; i++) {
            if (result[i] != 0) {
                nameList.add(nameDegree[i]);
                numberList.add(i);
            }
        }
        double[] tempValue = new double[nameList.size()];
        double tempMC = 0;
        double tempM = 0;
        for (int i = 0; i < 3750; i++) {
            for (int j = 0; j < nameList.size(); j++) {
                tempValue[j] = turnDegree.get(nameList.get(j)).trumpFunc((i * 0.01), resultTurnDeg[numberList.get(j)]);
            }
            tempMC += (i * 0.01) * max(tempValue);
            tempM += max(tempValue);
        }
        diffuseValue = tempMC / tempM;
    }

    private double max(double[] in) {
      //  System.out.println("L "+in.length);
        if (in.length != 0) {
            double max = in[0];
            for (double anIn : in) {
                if (anIn > max) {
                    max = anIn;
                }
            }
            return max;
        } else return 0;
    }


    void changeWheelDegValue(double value) {
        fuzzyLogic.clear();
        for (int j = 0; j < degreeValue.size(); j++) {
            fuzzyLogic.add(degreeValue.get(nameDegree[j]));
        }

        temp = value;
        resultWheelDeg = fuzzyLogic.evaluate(temp);
        resultTurnDeg = new double[nameDegree.length];

        ruleBase();

        evaluate(resultTurnDeg);
        System.out.println(diffuseValue);
    }

    public double getDiffuseValue(){
        return diffuseValue;
    }

}
