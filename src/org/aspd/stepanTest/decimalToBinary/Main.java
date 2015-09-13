package org.aspd.stepanTest.decimalToBinary;

import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // User input
        System.out.println("Enter decimal number: ");
        int num = in.nextInt();
        System.out.print("\nBinary representation is:");
        System.out.println(Integer.toBinaryString(num));
    }
}
