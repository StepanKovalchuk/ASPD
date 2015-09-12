package org.aspd;

import com.sun.javafx.animation.TickCalculation;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {


        String inPath = "D:\\Work\\ASPD\\TestData\\Sound\\3.wav";
        String outPath = "D:\\Work\\ASPD\\TestData\\Sound\\test3.wav";

        WaveFile wf = new WaveFile(new File(inPath));

        byte[] tempData = wf.getData();
//        int sample = wf.getSampleInt(10000);
        wf.setSampleInt(12000, 128);


//        Arrays.sort(tempData);



//        wf.saveFile(new File(outPath), tempData);

        System.out.println("Channels = " + wf.getChannels());
//        System.out.println(wf.getSampleRate());
//        System.out.println(wf.getSampleSizeInBits());
        System.out.println(wf.getStreamSpeed());

//        for (int i = 100; i < 102; i++)
//            System.out.println(wf.getSampleInt(3));
//
        System.out.println(wf.getSampleSize());
        System.out.println(wf.getFramesCount());
//        System.out.println(wf.getData().length/4);





        /*// создание одноканального wave-файла из массива целых чисел
        System.out.println("Создание моно-файла...");
        int[] samples = new int[3000000];
        for(int i=0; i < samples.length; i++){
            samples[i] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*440*i/44100)));
        }

        WaveFile wf = new WaveFile(4, 44100, 1, samples);
        wf.saveFile(new File("D:\\Work\\ASPD\\TestData\\Sound\\test1.wav"));
        System.out.println("Продолжительность моно-файла: " + wf.getDurationTime() + " сек.");

        // Создание стерео-файла
        System.out.println("Создание стерео-файла...");
        int x=0;
        for(int i=0; i < samples.length; i++){
            samples[i++] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*329.6*x/44100)));
            samples[i] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*440*x/44100)));
            x++;
        }
        wf = new WaveFile(4, 44100, 2, samples);
        wf.saveFile(new File("D:\\Work\\ASPD\\TestData\\Sound\\test2.wav"));
        System.out.println("Продолжительность стерео-файла: " + wf.getDurationTime() + " сек.");

        // Чтение данных из файла
        System.out.println("Чтение данных из моно-файла:");
        wf = new WaveFile(new File("D:\\Work\\ASPD\\TestData\\Sound\\test1.wav"));
        for(int i=0; i<10; i++){
            System.out.println(wf.getSampleInt(i));
        }*/
    }
}
