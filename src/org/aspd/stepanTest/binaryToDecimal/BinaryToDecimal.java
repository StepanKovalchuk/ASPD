package org.aspd.stepanTest.binaryToDecimal;

import java.util.Scanner;


public class BinaryToDecimal {
    public static void main(String args[]){
        Scanner input = new Scanner( System.in );
        System.out.println(" Enter a binary number: ");
        String binaryString =input.nextLine();
        System.out.println("Output: "+Integer.parseInt(binaryString,2));
    }
}