package com.hassanhaider.ZevsDiceBot.DiceEngine;

import java.util.ArrayList;
import java.util.Random;

public class Dice {
    //setter and getters for dice sides
    private int diceSides;
    public int getDiceSides() {
        return diceSides;
    }
    public void setDiceSides(int diceSides) {
        this.diceSides = diceSides;
    }

    //setter and getter for dice sides
    private int diceModifier;
    public int getDiceModifier() {
        return diceModifier;
    }
    public void setDiceModifier(int diceModifier) {
        this.diceModifier = diceModifier;
    }

    //total value of roll made
    private int Total;
    public int getTotal() {
        return Total;
    }
    public void setTotal(int total) {
        Total = total;
    }

    //getter for list of dice rolls
    private ArrayList<Integer> diceRolls = new ArrayList<>();
    public ArrayList<Integer> getDiceRolls() {
        return diceRolls;
    }

    //custom constructor for dice
    public Dice (String dieRollExpression, Random random) {
        String[] dieRollSet = dieRollExpression.split("d",0);
        Roll(Integer.parseInt(dieRollSet[0]),Integer.parseInt(dieRollSet[1]),random);
    }

    //roll die value
    private void Roll (int dieCount, int dieSides, Random random) {
        for (int dieCountNum = dieCount; dieCountNum > 0; dieCountNum--) {
            int int_random = random.nextInt(dieSides - 1);

            int diceRoll = (int) ((Math.random() * (dieSides) + 1) + 0);
            setTotal(getTotal() + diceRoll);
            diceRolls.add(diceRoll);
        }
    }
}
