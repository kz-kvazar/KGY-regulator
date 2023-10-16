//package com.add.vpn.model;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//
//
//public class DataSender {
//    public void setPowerConstant(int powerConstant) throws IOException, InterruptedException {
//
//        String host = "172.30.40.50"; // IP-адрес устройства
//        int port = 502; // Порт Modbus
//        int transactionId = (int) (Math.random() * 100);
//        // Адрес устройства
//        int slaveAddress = 1;
//        // Длина данных в пакете (байты)
//        int dataLength = 6;
//        // ID протокола (нули для Modbus/TCP)
//        int protocolId = 0;
//        byte[] setPower = new byte[]{(byte) (transactionId >> 8), // Старший байт ID транзакции
//                (byte) (transactionId & 0xFF), // Младший байт ID транзакции
//                (byte) (protocolId >> 8), // Старший байт ID протокола
//                (byte) (protocolId & 0xFF), // Младший байт ID протокола
//                (byte) (dataLength >> 8), // Старший байт длины данных
//                (byte) (dataLength & 0xFF), // Младший байт длины данных
//                (byte) slaveAddress, // Адрес устройства
//                (byte) 16, // Код функции
//                (byte) (8639 >> 8), // Старший байт начального регистра
//                (byte) (8639 & 0xFF), // Младший байт начального регистра
//                (byte) (1 >> 8), // Старший байт количества регистров для записи
//                (byte) (1 & 0xFF), // Младший байт количества регистров для записи
//                (byte) (2), // колличество байт данных
//                (byte) (powerConstant >> 8), // Младший байт заданного значения
//                (byte) (powerConstant & 0xFF) // Младший байт заданного значения
//        };
//        try (Socket socket = new Socket(host, port);
//             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
//             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
//            outputStream.write(setPower);
//            outputStream.flush();
//            Thread.sleep(200);
//             byte[] result = new byte[inputStream.available()];
//             int bytesCount = inputStream.read(result);
//        }
//
//    }
//
//}
