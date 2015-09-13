package org.aspd.stepanTest.fileread;

import java.io.*;

public class Main {

    public static void main( String[] args )
            throws FileNotFoundException, IOException {

        File file = new File( "D:\\11.txt" );

        BufferedReader br = new BufferedReader (
                new InputStreamReader(
                        new FileInputStream( file ), "UTF-8"
                )
        );
        String line = null;
        while ((line = br.readLine()) != null) {
            //variable line does NOT have new-line-character at the end
            System.out.println( line );
        }
        br.close();
    }
}
