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

        // ������� ������� �� �� �������, ��� ������� ��������� �������������,
        // ��� ���������� ��� ���� � ��������� ���
        //TODO: need to check this...
        streamSpeed = (int) (af.getFrameSize() * af.getChannels() * af.getFrameRate());

        // ����� ������ � ������
        sampleSize = af.getSampleSizeInBits() / 8;

        // ����� ������ � ����
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
     * @param sampleSize - ������� ���� �������� �������
     * @param sampleRate - �������
     * @param channels   - ������� ������
     * @param samples    - ������ ������� (���)
     * @throws Exception ���� ����� ������ ������ ��� ��������� ��� ���������� ����� int
     */

    private WaveStorage(int sampleSize, float sampleRate, int channels, int[] samples) throws Exception {

        if (sampleSize < INT_SIZE) {
            throw new Exception("sample size < int size");
        }

        this.sampleSize = sampleSize;
        this.af = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, false);
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
        return data.clone();
    }

    /**
     * ����� ������� ���� ��� ����� ���� �����
     *
     * @return ����� ������ � ������
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * ����� ������� �� ��� ����� ���� �����
     *
     * @return ����� ������ � ����
     */
    public int getSampleSizeInBits() {
        return sampleSizeInBits;
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
     * ����� ������� ������
     *
     * @return ������� ������
     */
    public int getChannels() {
        return channels;
    }

    /**
     * ����� ������� �������������
     *
     * @return ������� �������������
     */
    public float getSampleRate() {
        return sampleRate;
    }

    /**
     * ����� ������� ������� �� �� �������, ��� ������� ��������� �������������,
     * ��� ���������� ��� ���� � ��������� ���
     *
     * @return ������� �� �� �������
     */
    public int getStreamSpeed() {
        return streamSpeed;
    }

    /**
     * ������ ��"��� WaveStorage � ����������� ���� ������� WAVE
     *
     * @param file ���� ��� ���������� �����
     * @throws IOException
     */
    public void saveFile(File file, byte[] data) throws IOException {
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(data),
                af, framesCount), AudioFileFormat.Type.WAVE, file);
    }

    /**
     * ����� ������ (������������) �� ������, ������� ������ �������������
     *
     * @param sampleNumber - ����� ������
     * @return - ������������ �� ������
     */
    public byte getLSByteOfSample(int sampleNumber) {
        return data[sampleNumber * sampleSize];
    }


    /**
     * ���������� �������� ������� (�������������) ��� ������
     *
     * @param sampleNumber - ����� ������
     * @param byteValue    - �������� ������� (�������������) ��� ������
     */
    public void setLSByteOfSample(int sampleNumber, byte byteValue) {
        data[sampleNumber * sampleSize] = byteValue;
    }

    /**
     * ����� �������� ������ �� ����������� ������.���� ��� ������� � 2 ������,
     * �� ��������� �����������, �� ������ ����� � ������� ������ ����������.
     * ���������, ����� �� ������� ���� �� ������ ����� ����� ������,
     * ����� ����� ��� �� ������ ����� ������� ������, ����� ����� ��� �� ������ �����
     * ����� ������ � �. �..
     *
     * @param sampleNumber - ����� ������, ��������� � 0
     * @return �������� ������
     */
    private int getSampleInt(int sampleNumber) {

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
        //TODO: BufferUnderflowException... fucking little smelly bug
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
    private void setSampleInt(int sampleNumber, int sampleValue) {

        // ������������� ���� ����� � ������ ������ ����
        //TODO: BufferUnderflowException... fucking little smelly bug
        byte[] sampleBytes = ByteBuffer.allocate(sampleSize).
                order(ByteOrder.LITTLE_ENDIAN).putInt(sampleValue).array();

        // ��������� �������� ������� �����
        // � ����, ��� ������� ���������
        // ������ ������
        for (int i = 0; i < sampleSize; i++) {
            data[sampleNumber * sampleSize + i] = sampleBytes[i];
        }
    }




}

