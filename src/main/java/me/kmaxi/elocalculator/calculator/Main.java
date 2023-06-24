package me.kmaxi.elocalculator.calculator;

public class Main {

    public static void main(String[] args) {

        EloCalculator eloCalculator = new EloCalculator();

        eloCalculator.calculateELOs();

        System.out.println(eloCalculator);
    }


}
