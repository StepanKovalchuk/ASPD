package org.aspd.stepanTest.binaryToDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String args[]){
        Scanner input = new Scanner( System.in );
        System.out.print("Enter a binary number: ");
        String binaryString =input.nextLine();
        System.out.println("Output: "+Integer.parseInt(binaryString,2));
    }
}
