package com.add.vpn.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AlarmReceiver {

    public boolean getAlarm() throws IOException, InterruptedException {
        String host = "172.30.40.50"; // IP-адрес устройства
        int port = 502; // Порт Modbus
        int transactionId = (int) (Math.random() * 100);
        int protocolId = 0; // ID протокола (нули для Modbus/TCP)
        int dataLength = 6; // Длина данных в пакете (байты)
        int slaveAddress = 1; // Адрес устройства
        int registerAddress = 8235;
        byte[] request = new byte[]{(byte) (transactionId >> 8), // Старший байт ID транзакции
                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
                (byte) (protocolId >> 8), // Старший байт ID протокола
                (byte) (protocolId & 0xFF), // Младший байт ID протокола
                (byte) (dataLength >> 8), // Старший байт длины данных
                (byte) (dataLength & 0xFF), // Младший байт длины данных
                (byte) slaveAddress, // Адрес устройства
                (byte) 3, // Код функции
                (byte) (registerAddress >> 8), // Старший байт начального регистра
                (byte) (registerAddress & 0xFF), // Младший байт начального регистра
                (byte) (1 >> 8), // Старший байт количества регистров
                (byte) (1 & 0xFF) // Младший байт количества регистров
        };
        try (Socket socket2 = new Socket(host, port);
             DataInputStream inputStream = new DataInputStream(socket2.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket2.getOutputStream())) {

            outputStream.write(request);
            outputStream.flush();

            Thread.sleep(100);

            long skipped = inputStream.skip(9);

            byte[] result = new byte[inputStream.available()];
            int bytesCount = inputStream.read(result);

            ByteBuffer byteBuffer = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN);
            short values = byteBuffer.getShort();
            boolean bit7 = ((values >> 7) & 1) == 0; // alarm bit
            boolean bit8 = ((values >> 8) & 1) == 0; // error bit

            return bit7 || bit8;
        }
    }
}
