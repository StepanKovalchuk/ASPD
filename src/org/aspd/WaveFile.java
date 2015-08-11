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
    public final int NOT_SPECIFIED = -1;
    private int sampleSize = NOT_SPECIFIED;
    private long framesCount = NOT_SPECIFIED;
    private byte[] data = null;  // ������ ���� �������������� �����-������
    private AudioInputStream ais = null;
    private AudioFormat af = null;

    /**
     * Create object of said wave-file
     *
     * @param file - wave-����
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

        // get quantity frames og audio file
        framesCount = ais.getFrameLength();

        // ����� ������ � ������
        sampleSize = af.getSampleSizeInBits() / 8;

        // data size in bites
        long dataLength = framesCount * af.getSampleSizeInBits() * af.getChannels() / 8;

        // read all data in memory in one time
        data = new byte[(int) dataLength];
        ais.read(data);
    }

    /**
     * Create object from int array
     *
     * @param sampleSize - ������� ���� �������� �������
     * @param sampleRate - �������
     * @param channels   - ������� ������
     * @param samples    - ������ ������� (���)
     * @throws Exception ���� ����� ������ ������ ��� ��������� ��� ���������� ����� int
     */
    WaveFile(int sampleSize, float sampleRate, int channels, int[] samples) throws Exception {

        if (sampleSize < INT_SIZE) {
            throw new Exception("sample size < int size");
        }

        this.sampleSize = sampleSize;
        this.af = new AudioFormat(sampleRate, sampleSize * 8, channels, true, false);
        this.data = new byte[samples.length * sampleSize];

        // ���������� �����
        for (int i = 0; i < samples.length; i++) {
            setSampleInt(i, samples[i]);
        }

        framesCount = data.length / (sampleSize * af.getChannels());
        ais = new AudioInputStream(new ByteArrayInputStream(data), af, framesCount);
    }

    /**
     * ����� ������ ����-�����
     *
     * @return ������
     */
    public AudioFormat getAudioFormat() {
        return af;
    }

    /**
     * ����� ���� ������ ����� wave-����� � ������ �����
     *
     * @return ����� ����
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * ����� ������� ������� ����� ���� �����
     *
     * @return ����� ������
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * ����� duration ������� � ��������
     *
     * @return duration �������
     */
    public double getDurationTime() {
        return getFramesCount() / getAudioFormat().getFrameRate();
    }

    /**
     * ����� ������� ������ (�����) � ����
     *
     * @return ������� ������
     */
    public long getFramesCount() {
        return framesCount;
    }

    /**
     * ������ ��"��� WaveFile � ����������� ���� ������� WAVE
     *
     * @param file ���� ��� ���������� �����
     * @throws IOException
     */
    public void saveFile(File file) throws IOException {
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(data),
                af, framesCount), AudioFileFormat.Type.WAVE, file);
    }

    /**
     * ����� �������� ������ �� ����������� ������.���� ��� ������� � 2 ������,
     * �� ��������� �����������, �� ������ ����� � ������� ������ ����������.
     * ���������, ���� �� ������� ���� �� ������ ����� ����� ������,
     * ����� ����� ��� �� ������ ����� ������� ������, ����� ����� ��� �� ������ �����
     * ����� ������ � �. �..
     *
     * @param sampleNumber - ����� ������, ��������� � 0
     * @return �������� ������
     */
    public int getSampleInt(int sampleNumber) {

        if (sampleNumber < 0 || sampleNumber >= data.length / sampleSize) {
            throw new IllegalArgumentException(
                    "sample number is can't be < 0 or >= data.length/"
                            + sampleSize);
        }

        // ����� ���� ��� ������������� ������
        // (� ������ ������� ������ �����)
        byte[] sampleBytes = new byte[sampleSize];

        // ������� �� ����� ����� �� ����������
        // ��������� ������ ������
        for (int i = 0; i < sampleSize; i++) {
            sampleBytes[i] = data[sampleNumber * sampleSize + i];
        }

        // ����������� ����� � ���� �����
        int sample = ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN).getInt();

        return sample;
    }

    /**
     * ���������� �������� ������
     *
     * @param sampleNumber - ����� ������
     * @param sampleValue  - �������� ������
     */
    public void setSampleInt(int sampleNumber, int sampleValue) {

        // ������������� ���� ����� � ������ ������ ����
        byte[] sampleBytes = ByteBuffer.allocate(sampleSize).
                order(ByteOrder.LITTLE_ENDIAN).putInt(sampleValue).array();

        // ��������� �������� ������� �����
        // � ����, ������� ������� ���������
        // ������ ������
        for (int i = 0; i < sampleSize; i++) {
            data[sampleNumber * sampleSize + i] = sampleBytes[i];
        }
    }
}