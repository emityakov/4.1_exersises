package ru.samsung.myitacademy.mdev.coroutineexample

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this

        // URL изображения для загрузки
        val urlImage:URL = URL("https://github.com/emityakov/4.1_pictures/blob/master/pic_4.1.png?raw=true")

        // показать URL изображения в текстовом поле
        tvDownload.text = urlImage.toString()


        button.setOnClickListener {
            it.isEnabled = false
            progressBar.visibility = View.VISIBLE

            // асинхронная задача для получения / загрузки изображения с URL-адреса
            val result: Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }

            GlobalScope.launch(Dispatchers.Main) {
                // получаем загруженное изображение
                val bitmap : Bitmap? = result.await()

                // если скачалось, сохраняем во внутреннем хранилище
                bitmap?.apply {
                    // получаем сохраненное изображение
                    val savedUri : Uri? = saveToInternalStorage(context)

                    // отображаем изображение
                    imageView.setImageURI(savedUri)

                    // выводим сохраненный URI изображения в текстовом представлении
                    tvSaved.text = savedUri.toString()
                }

                it.isEnabled = true
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}


// функция расширения для получения / загрузки растрового изображения с URL-адреса
fun URL.toBitmap(): Bitmap?{
    return try {
        BitmapFactory.decodeStream(openStream())
    }catch (e:IOException){
        null
    }
}


// функция расширения для сохранения изображения во внутренней памяти
fun Bitmap.saveToInternalStorage(context : Context):Uri?{
    // получить экземпляр оболочки контекста
    val wrapper = ContextWrapper(context)

    // инициализация нового файла
    // нижняя строка возвращает каталог во внутренней памяти
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    // создаем файл для сохранения изображения
    file = File(file, "${UUID.randomUUID()}.jpg")

    return try {
        //получаем поток вывода файла
        val stream: OutputStream = FileOutputStream(file)

        // сжимаем растровое изображение
        compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // освобождаем поток
        stream.flush()

        // закрываем поток
        stream.close()

        // возвращаем сохраненное uri изображения
        Uri.parse(file.absolutePath)
    } catch (e: IOException){ // catch the exception
        e.printStackTrace()
        null
    }
}