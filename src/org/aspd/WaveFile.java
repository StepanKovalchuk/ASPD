package org.aspd;

/**
 * Created by Ra1004ek on 03.08.2015.
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


public class WaveFile {

    private final int INT_SIZE = 4;
    private final int NOT_SPECIFIED = -1;
    private int sampleSize = NOT_SPECIFIED;
    private int sampleSizeInBits = NOT_SPECIFIED;
    private float sampleRate = NOT_SPECIFIED;
    private int channels = NOT_SPECIFIED;
    private long framesCount = NOT_SPECIFIED;
    private int streamSpeed = NOT_SPECIFIED;
    private byte[] data = null;  // массив байт представляющий аудио-данные
    private AudioInputStream ais = null;
    private AudioFormat af = null;

    /**
     * Create object of said wave-file
     *
     * @param file - wave-файл
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    WaveFile(File file) throws UnsupportedAudioFileException, IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        // get audio data stream
        ais = AudioSystem.getAudioInputStream(file);

        // get audio format info
        af = ais.getFormat();

        // get quantity of channels
        channels = af.getChannels();

        // get sample rate
        sampleRate = af.getSampleRate();

        // get quantity frames og audio file
        framesCount = ais.getFrameLength();

        // середня кількість біт на секунду, яку повинен обробляти аудіопрогравач,
        // щоб програвати цей звук у реальному часі
        streamSpeed = af.getFrameSize() * af.getChannels() * (int) af.getFrameRate();

        // розмір семпла в байтах
        sampleSize = af.getSampleSizeInBits() / 8;

        // розмір семпла в бітах
        sampleSizeInBits = af.getSampleSizeInBits();

        // data size in bites
        long dataLength = framesCount * af.getSampleSizeInBits() * af.getChannels() / 8;

        // read all data in memory in one time
        data = new byte[(int) dataLength];
        ais.read(data);
    }

    /**
     * Create object from int array
     *
     * @param sampleSize - кількість байт займаємих семплом
     * @param sampleRate - частота
     * @param channels   - кількість каналів
     * @param samples    - массив значень (дані)
     * @throws Exception якщо розмір семпла меньше чим необхідно для збереження змінної int
     */
    WaveFile(int sampleSize, float sampleRate, int channels, int[] samples) throws Exception {

        if (sampleSize < INT_SIZE) {
            throw new Exception("sample size < int size");
        }

        this.sampleSize = sampleSize;
        this.af = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, false);
        this.data = new byte[samples.length * sampleSize];

        // заповнення даних
        for (int i = 0; i < samples.length; i++) {
            setSampleInt(i, samples[i]);
        }

        framesCount = data.length / (sampleSize * af.getChannels());
        ais = new AudioInputStream(new ByteArrayInputStream(data), af, framesCount);
    }

    /**
     * Вертає формат аудіо-диних
     *
     * @return формат
     */
    public AudioFormat getAudioFormat() {
        return af;
    }

    /**
     * Вертає копію масиву даних wave-файлу у вигляді байтів
     *
     * @return масив байт
     */
    public byte[] getData() {
//        return Arrays.copyOf(data, data.length);
        return data.clone();
    }

    /**
     * Вертає кількість байт яке займає один семпл
     *
     * @return розмір семплу в байтах
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Вертає кількість біт яке займає один семпл
     *
     * @return розмір семплу в бітах
     */
    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    /**
     * Вертає duration сигнала в секундах
     *
     * @return duration сигнала
     */
    public double getDurationTime() {
        return getFramesCount() / getAudioFormat().getFrameRate();
    }

    /**
     * Вертає кількість фреймів (кадрів) в файлі
     *
     * @return кількість фреймів
     */
    public long getFramesCount() {
        return framesCount;
    }

    /**
     * Вертає кількість каналів
     *
     * @return кількість каналв
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Вертає частоту дискретизації
     *
     * @return частоту дискретизації
     */
    public float getSampleRate() {
        return sampleRate;
    }

    /**
     * Вертає середню кількість біт на секунду, яку повинен обробляти аудіопрогравач,
     * щоб програвати цей звук у реальному часі
     *
     * @return кількість біт на секунду
     */
    public int getStreamSpeed() {
        return streamSpeed;
    }

    /**
     * Зберігає об"єкт WaveFile в стандартний файл формата WAVE
     *
     * @param file Шлях для збереження файлу
     * @throws IOException
     */
    public void saveFile(File file, byte[] data) throws IOException {
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(data),
                af, framesCount), AudioFileFormat.Type.WAVE, file);
    }

    /**
     * Вертає значення семплу по порядковому номеру.Якщо дані записані в 2 канали,
     * то необхідно враховувати, що семпли лівого і правого каналу чергуються.
     * Наприклад, семпл під номером один це перший семпл лівого каналу,
     * семпл номер два це перший семпл правого каналу, семпл номер три це другий семпл
     * лівого каналу і т. д..
     *
     * @param sampleNumber - номер семпла, починаючи з 0
     * @return значення семпла
     */
    public int getSampleInt(int sampleNumber) {

        if (sampleNumber < 0 || sampleNumber >= data.length / sampleSize) {
            throw new IllegalArgumentException(
                    "sample number is can't be < 0 or >= data.length/"
                            + sampleSize);
        }

        // масив байт для представлення семплу
        // (в даному випадку цілого числа)
        byte[] sampleBytes = new byte[sampleSize];

        // читаемо із даних байти які відповідають
        // вказаному номеру семплу

        for (int i = 0; i < sampleSize; i++) {
            sampleBytes[i] = data[sampleNumber * sampleSize + i];
        }
        //TODO: BufferUnderflowException... fucking little smelly bug
        // претворюємо байти в ціле число
        int sample = ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN).getInt();

        return sample;
    }

    /**
     * Встановлює значення семплу
     *
     * @param sampleNumber - номер семплу
     * @param sampleValue  - значення семплу
     */
    public void setSampleInt(int sampleNumber, int sampleValue) {

        // представляємо ціле число у вигляді масива байт
        //TODO: BufferUnderflowException... fucking little smelly bug
        byte[] sampleBytes = ByteBuffer.allocate(sampleSize).
                order(ByteOrder.LITTLE_ENDIAN).putInt(sampleValue).array();

        // послідовно записуємо отримані байти
        // в місце, яке відповідає вказаному
        // номеру семплу
        for (int i = 0; i < sampleSize; i++) {
            data[sampleNumber * sampleSize + i] = sampleBytes[i];
        }
    }

    /**
     * Вертає перший (малозначущий) біт семплу, кількість каналів невраховується
     *
     * @param sampleNumber - номер семпла
     * @return - малозначущий біт семпла
     */
    public byte getLitteleBitOfSample(int sampleNumber) {
        return data[sampleNumber * sampleSize];
    }


    /**
     * Вствновлює значення першого (малозначущого) біту сумпла
     *
     * @param sampleNumber - номер семпла
     * @param byteValue    - значення першого (малозначущого) біту сумпла
     */
    public void setLitteleBitOfSample(int sampleNumber, byte byteValue) {
        data[sampleNumber * sampleSize] = byteValue;
    }


}