package com.add.vpn.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataReceiver {
      public Short getPowerConstant() throws IOException, InterruptedException {
        String host = "172.30.40.50"; // IP-адрес устройства
        int port = 502; // Порт Modbus
        int transactionId = (int) (Math.random() * 100);
        int protocolId = 0; // ID протокола (нули для Modbus/TCP)
        int dataLength = 6; // Длина данных в пакете (байты)
        int slaveAddress = 1; // Адрес устройства
        byte[] request = new byte[]{(byte) (transactionId >> 8), // Старший байт ID транзакции
                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
                (byte) (protocolId >> 8), // Старший байт ID протокола
                (byte) (protocolId & 0xFF), // Младший байт ID протокола
                (byte) (dataLength >> 8), // Старший байт длины данных
                (byte) (dataLength & 0xFF), // Младший байт длины данных
                (byte) slaveAddress, // Адрес устройства
                (byte) 3, // Код функции
                (byte) (8639 >> 8), // Старший байт начального регистра
                (byte) (8639 & 0xFF), // Младший байт начального регистра
                (byte) (1 >> 8), // Старший байт количества регистров
                (byte) (1 & 0xFF) // Младший байт количества регистров
        };
        try (Socket socket2 = new Socket(host, port); DataInputStream inputStream = new DataInputStream(socket2.getInputStream()); DataOutputStream outputStream = new DataOutputStream(socket2.getOutputStream())) {
            outputStream.write(request);
            outputStream.flush();

            Thread.sleep(100);

            long skipped = inputStream.skip(9);

            byte[] result = new byte[inputStream.available()];
            int bytesCount = inputStream.read(result);

            ByteBuffer byteBuffer = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN);
            return byteBuffer.getShort();
        }

    }

    public Short getPowerActive() throws IOException, InterruptedException {
        String host = "172.30.40.50"; // IP-адрес устройства
        int port = 502; // Порт Modbus
        int transactionId = (int) (Math.random() * 100);
        int protocolId = 0; // ID протокола (нули для Modbus/TCP)
        int dataLength = 6; // Длина данных в пакете (байты)
        int slaveAddress = 1; // Адрес устройства
        byte[] request = new byte[]{
                (byte) (transactionId >> 8), // Старший байт ID транзакции
                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
                (byte) (protocolId >> 8), // Старший байт ID протокола
                (byte) (protocolId & 0xFF), // Младший байт ID протокола
                (byte) (dataLength >> 8), // Старший байт длины данных
                (byte) (dataLength & 0xFF), // Младший байт длины данных
                (byte) slaveAddress, // Адрес устройства
                (byte) 3, // Код функции
                (byte) (8202 >> 8), // Старший байт начального регистра
                (byte) (8202 & 0xFF), // Младший байт начального регистра
                (byte) (1 >> 8), // Старший байт количества регистров
                (byte) (1 & 0xFF) // Младший байт количества регистров
        };
        try (Socket socket2 = new Socket(host, port); DataInputStream inputStream = new DataInputStream(socket2.getInputStream()); DataOutputStream outputStream = new DataOutputStream(socket2.getOutputStream())) {
            outputStream.write(request);
            outputStream.flush();

            Thread.sleep(100);

            long skipped = inputStream.skip(9);

            byte[] result = new byte[inputStream.available()];
            int bytesCount = inputStream.read(result);

            ByteBuffer byteBuffer = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN);
            return byteBuffer.getShort();
        }
    }


    public Double getOpPressure() throws IOException, InterruptedException {
        String host = "10.70.0.28"; // IP-адрес устройства
        int port = 502; // Порт Modbus
        int transactionId = 123; // ID транзакции
        int protocolId = 0; // ID протокола (нули для Modbus/TCP)
        int dataLength = 6; // Длина данных в пакете (байты)
        int slaveAddress = 1; // Адрес устройства
        int functionCode = 4; // Код функции чтения регистров
        int startRegister = 0; // Начальный регистр чтения
        int numRegisters = 12; // Количество регистров


        // Формирование запроса Modbus
        byte[] request = {(byte) (transactionId >> 8), // Старший байт ID транзакции
                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
                (byte) (protocolId >> 8), // Старший байт ID протокола
                (byte) (protocolId & 0xFF), // Младший байт ID протокола
                (byte) (dataLength >> 8), // Старший байт длины данных
                (byte) (dataLength & 0xFF), // Младший байт длины данных
                (byte) slaveAddress, // Адрес устройства
                (byte) functionCode, // Код функции
                (byte) (startRegister >> 8), // Старший байт начального регистра
                (byte) (startRegister & 0xFF), // Младший байт начального регистра
                (byte) (numRegisters >> 8), // Старший байт количества регистров
                (byte) (numRegisters & 0xFF) // Младший байт количества регистров
        };


        try (Socket socket = new Socket(host, port); DataInputStream inputStream = new DataInputStream(socket.getInputStream()); DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {


            outputStream.write(request);
            outputStream.flush();
            Thread.sleep(200);

            inputStream.skip(9);

            byte[] read = new byte[inputStream.available()];
            int bytesCount = inputStream.read(read);
            Thread.sleep(100);
            ByteBuffer byteBuffer = ByteBuffer.wrap(read).order(ByteOrder.BIG_ENDIAN);
            return Math.round(byteBuffer.getFloat() * 100) / 100.0;

        }
    }


    public Float getThrottlePosition() throws IOException, InterruptedException {
        String host = "172.30.40.50"; // IP-адрес устройства
        int port = 502; // Порт Modbus
        int transactionId = (int) (Math.random() * 100);
        int protocolId = 0; // ID протокола (нули для Modbus/TCP)
        int dataLength = 6; // Длина данных в пакете (байты)
        int slaveAddress = 1; // Адрес устройства
        byte[] request = new byte[]{(byte) (transactionId >> 8), // Старший байт ID транзакции
                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
                (byte) (protocolId >> 8), // Старший байт ID протокола
                (byte) (protocolId & 0xFF), // Младший байт ID протокола
                (byte) (dataLength >> 8), // Старший байт длины данных
                (byte) (dataLength & 0xFF), // Младший байт длины данных
                (byte) slaveAddress, // Адрес устройства
                (byte) 3, // Код функции
                (byte) (9204 >> 8), // Старший байт начального регистра
                (byte) (9204 & 0xFF), // Младший байт начального регистра
                (byte) (1 >> 8), // Старший байт количества регистров
                (byte) (1 & 0xFF) // Младший байт количества регистров
        };
        try (Socket socket2 = new Socket(host, port); DataInputStream inputStream = new DataInputStream(socket2.getInputStream()); DataOutputStream outputStream = new DataOutputStream(socket2.getOutputStream())) {
            outputStream.write(request);
            outputStream.flush();

            Thread.sleep(100);

            long skipped = inputStream.skip(9);

            byte[] result = new byte[inputStream.available()];
            int bytesCount = inputStream.read(result);

            ByteBuffer byteBuffer = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN);
            float throttlePosition = byteBuffer.getShort();

            return throttlePosition / 10;
        }
    }
    public Float getCH4Concentration() throws IOException, InterruptedException {
        String host = "172.30.40.50"; // IP-адрес устройства
        int port = 502; // Порт Modbus
        int transactionId = (int) (Math.random() * 100);
        int protocolId = 0; // ID протокола (нули для Modbus/TCP)
        int dataLength = 6; // Длина данных в пакете (байты)
        int slaveAddress = 1; // Адрес устройства
        byte[] request = new byte[]{(byte) (transactionId >> 8), // Старший байт ID транзакции
                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
                (byte) (protocolId >> 8), // Старший байт ID протокола
                (byte) (protocolId & 0xFF), // Младший байт ID протокола
                (byte) (dataLength >> 8), // Старший байт длины данных
                (byte) (dataLength & 0xFF), // Младший байт длины данных
                (byte) slaveAddress, // Адрес устройства
                (byte) 3, // Код функции
                (byte) (9156 >> 8), // Старший байт начального регистра
                (byte) (9156 & 0xFF), // Младший байт начального регистра
                (byte) (1 >> 8), // Старший байт количества регистров
                (byte) (1 & 0xFF) // Младший байт количества регистров
        };
        try (Socket socket2 = new Socket(host, port); DataInputStream inputStream = new DataInputStream(socket2.getInputStream()); DataOutputStream outputStream = new DataOutputStream(socket2.getOutputStream())) {
            outputStream.write(request);
            outputStream.flush();

            Thread.sleep(100);

            long skipped = inputStream.skip(9);

            byte[] result = new byte[inputStream.available()];
            int bytesCount = inputStream.read(result);

            ByteBuffer byteBuffer = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN);
            float CH4concentration = byteBuffer.getShort();

            return CH4concentration / 10;

        }
    }
}
