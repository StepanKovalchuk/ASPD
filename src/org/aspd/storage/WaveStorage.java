package org.aspd.storage;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

/**
 * @author olozynskyy
 * @since 3.7.0
 */
public class WaveStorage implements Storage {

    private String fileExtension = null;
    private String fileType = null;
    private String fileFormat = null;
    private File file;

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
    public WaveStorage(File file) throws UnsupportedAudioFileException, IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        // get file type
        fileType = Files.probeContentType(file.toPath());

        //get file extension
        fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

        // get audio data stream
        ais = AudioSystem.getAudioInputStream(file);

        // get audio format info
        af = ais.getFormat();

        // get quantity of channels
        channels = af.getChannels();

        // get sample rate
        sampleRate = af.getSampleRate();

        // get quantity frames of audio file
        framesCount = ais.getFrameLength();

        // середня кількість біт на секунду, яку повинен обробляти аудіопрогравач,
        // щоб програвати цей звук у реальному часі
        //TODO: need to check this...
        streamSpeed = (int) (af.getFrameSize() * af.getChannels() * af.getFrameRate());

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

    private WaveStorage(int sampleSize, float sampleRate, int channels, int[] samples) throws Exception {

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
     * Зберігає об"єкт WaveStorage в стандартний файл формата WAVE
     *
     * @param file Шлях для збереження файлу
     * @throws IOException
     */
    public void saveFile(File file, byte[] data) throws IOException {
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(data),
                af, framesCount), AudioFileFormat.Type.WAVE, file);
    }

    /**
     * Вертає перший (малозначущий) біт семплу, кількість каналів невраховується
     *
     * @param sampleNumber - номер семпла
     * @return - малозначущий біт семпла
     */
    public byte getLSByteOfSample(int sampleNumber) {
        return data[sampleNumber * sampleSize];
    }


    /**
     * Вствновлює значення першого (малозначущого) біту сумпла
     *
     * @param sampleNumber - номер семпла
     * @param byteValue    - значення першого (малозначущого) біту сумпла
     */
    public void setLSByteOfSample(int sampleNumber, byte byteValue) {
        data[sampleNumber * sampleSize] = byteValue;
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
    private int getSampleInt(int sampleNumber) {

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
    private void setSampleInt(int sampleNumber, int sampleValue) {

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




}

