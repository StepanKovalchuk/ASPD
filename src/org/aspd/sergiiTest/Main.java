package org.aspd.sergiiTest;

import org.aspd.storage.WaveStorage;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

//
//        String inPath = "D:\\Work\\ASPD\\TestData\\Sound\\3.34.34.34.45.wav";
//        String outPath = "D:\\ASPD\\TestData\\out1.wav";
//
//        WaveStorage wf = new WaveStorage(new File(inPath));
//
//
//
//
//
//
//
//
//        byte[] tempData = wf.getData();

//        int sample = wf.getSampleInt(10000);
//        wf.setSampleInt(12000, 128);


//        Arrays.sort(tempData);



//        wf.saveFile(new File(outPath), tempData);






        /*// создание одноканального wave-файла из массива целых чисел
        System.out.println("Создание моно-файла...");
        int[] samples = new int[3000000];
        for(int i=0; i < samples.length; i++){
            samples[i] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*440*i/44100)));
        }

        WaveStorage wf = new WaveStorage(4, 44100, 1, samples);
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
        wf = new WaveStorage(4, 44100, 2, samples);
        wf.saveFile(new File("D:\\Work\\ASPD\\TestData\\Sound\\test2.wav"));
        System.out.println("Продолжительность стерео-файла: " + wf.getDurationTime() + " сек.");

        // Чтение данных из файла
        System.out.println("Чтение данных из моно-файла:");
        wf = new WaveStorage(new File("D:\\Work\\ASPD\\TestData\\Sound\\test1.wav"));
        for(int i=0; i<10; i++){
            System.out.println(wf.getSampleInt(i));
        }*/
    }
}
