package org.aspd.stepanTest.fileWriter;

import java.io.*;

public class FileApp {
    public static void main(String[] args) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File("d:\\22.txt")), "UTF8"));
            out.append("Stepan");
            out.append("\nПривіт");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
}


}