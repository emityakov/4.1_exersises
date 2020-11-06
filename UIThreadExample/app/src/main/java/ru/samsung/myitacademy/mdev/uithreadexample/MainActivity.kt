package ru.samsung.myitacademy.mdev.uithreadexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // элементы разметки
        val tv = findViewById<TextView>(R.id.tv1)
        val btn = findViewById<Button>(R.id.btnStart)
        val msg1 = "First message"
        val msg2 = "Second message"

        // слушатель для кнопки
        btn.setOnClickListener{
            // объявляем главный поток
            Thread(Runnable {
                while (true) {
                    // обновление TextView
                    runOnUiThread{ tv.text = msg1 }
                    // останавливаем поток на одну секунду
                    Thread.sleep(1000)

                    // обновление TextView
                    runOnUiThread{ tv.text = msg2 }

                    // останавливаем поток на одну секунду
                    Thread.sleep(1000)
                }
            }).start()
        }
    }
}