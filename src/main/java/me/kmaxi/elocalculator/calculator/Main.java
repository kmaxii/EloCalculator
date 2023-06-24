package me.kmaxi.elocalculator.calculator;

import me.kmaxi.elocalculator.calculator.EloCalculator;

public class Main {

    public static void main(String[] args) {

        EloCalculator eloCalculator = new EloCalculator();

        eloCalculator.calculateELOs();

        System.out.println(eloCalculator);
    }


}
