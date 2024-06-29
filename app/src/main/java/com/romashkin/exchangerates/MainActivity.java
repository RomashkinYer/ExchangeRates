package com.romashkin.exchangerates;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Document document;
    private Thread secondThread;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Вызываем парсинг во втором потоке
        init();
    }
    //Создаем поток для парсинга валют с сайта
    private void init(){
        runnable = new Runnable() {
            @Override
            public void run() {
                parseWeb();
            }
        };
        secondThread = new Thread(runnable);
        secondThread.start();
    }

    //Метод для парсинга сайта
    private void parseWeb(){
        try {
            //Парсим сайт ЦБ
            document = Jsoup.connect("https://www.cbr.ru/currency_base/daily/").get();
            Log.i("MyLog", "Title : " + document.title());
            //Достаем оттуда таблицу
            Element table = document.select("table.data").first();
            //Проверка на наличие таблицы
            if (table != null) {
                //Получаем строки с таблицы
                Elements rows = table.select("tr");
                //Перебериаем строки таблицы
                for (Element row : rows){
                    //Получаем колонки
                    Elements columns = row.select("td");
                    if (columns.size() == 6) {
                        String currencyName = columns.get(2).text();
                        String rate = columns.get(4).text();
                        String course = columns.get(5).text();
                        Log.i("MyLog", "Валюта : " + currencyName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}