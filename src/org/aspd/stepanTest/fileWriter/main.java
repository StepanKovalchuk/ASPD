package org.aspd.stepanTest.fileWriter;


import java.io.*;

public class main {
    public static void main(String[] args){
        try{
            PrintWriter out=new PrintWriter(new File("d:\\22.txt"),"utf-8");
            out.println("");
            out.println("");
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
