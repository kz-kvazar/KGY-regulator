package com.add.vpn;

import androidx.appcompat.app.AppCompatActivity;
import com.add.vpn.roomDB.DatabaseManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ReportBot {
    private static final String BOT_TOKEN = "6604238506:AAE6ckDJVAkjN00sq1NMHzTXVMldnKyuq7A";
    private static final String BASE_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/";
    private static final String CHAT_ID = "-4014080129";

    public ReportBot(AppCompatActivity activity) {
        DatabaseManager.getInstance(activity).reportDao().getLastLiveData().observe(activity, reportItem ->
                sendTextMessage(reportItem.toString()));
    }

    public void sendTextMessage(String text) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "sendMessage");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                String jsonInputString = String.format("{\"chat_id\": \"%s\", \"text\": \"%s\"}", CHAT_ID, text);
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("Сообщение отправлено успешно.");
                } else {
                    System.out.println("Ошибка при отправке сообщения. Код ответа: " + responseCode);
                }

                conn.disconnect();
            } catch (IOException ignored) {
            }
        }).start();

    }
    public static void sendReport(String text) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "sendMessage");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                String jsonInputString = String.format("{\"chat_id\": \"%s\", \"text\": \"%s\"}", CHAT_ID, text);
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(input, 0, input.length);
                }
                conn.disconnect();
            } catch (IOException ignored) {
            }
        }).start();
    }
}
