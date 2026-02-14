package com.example.flamingcoding.androidTrials

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.flamingcoding.R
import java.io.File

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
        sendNotificationNormal()
        sendNotificationImportant()
        useSystemCamera()
        chooseImageFromAlbum()
    }


    fun sendNotificationNormal() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val sendNotificationButton = findViewById<Button>(R.id.sendNotice)
        sendNotificationButton.setOnClickListener {
            // 设置intent，点击通知打开相应界面
            val intent = Intent(this, NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, "normal")
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setSmallIcon(R.drawable.board_icon)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.board_icon_nine
                    )
                )
                // 设置点击自动关闭
                .setAutoCancel(true)
                // 设置intent到notification
                .setContentIntent(pi)
                // 显示长文字
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("hfdjkhsahjfkdafdkjahfjkdahfdjkahfdjkahfdjkahfkjhvcixouvkcxhvbvk")
                )
                // 显示大图
                .setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(
                        BitmapFactory.decodeResource(resources, R.drawable.board_icon_nine)
                    )
                )
                .build()

            manager.notify(1, notification)
        }
    }

    fun sendNotificationImportant() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel2 = NotificationChannel(
                "important", "Important",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel2)
        }
        val sendNotificationButton = findViewById<Button>(R.id.sendNoticeImportant)
        sendNotificationButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(this, "important")
                .setContentTitle("This is content title")
//                .setContentText("This is content text")
                .setSmallIcon(R.drawable.board_icon)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.board_icon_nine
                    )
                )
                // 设置点击自动关闭
                .setAutoCancel(true)
                // 设置intent到notification
                .setContentIntent(pi)
                // 显示长文字
//                .setStyle(
//                    NotificationCompat.BigTextStyle()
//                        .bigText("hfdjkhsahjfkdafdkjahfjkdahfdjkahfdjkahfdjkahfkjhvcixouvkcxhvbvk")
//                )
                // 显示大图
                .setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(
                        BitmapFactory.decodeResource(resources, R.drawable.board_icon_nine)
                    )
                )
                .build()
            manager.notify(1, notification)
        }
    }

    val takePhoto = 1
    lateinit var imageUri: Uri
    lateinit var outputImage: File

    private fun useSystemCamera() {
        val cameraButton = findViewById<Button>(R.id.cameraButton)
        cameraButton.setOnClickListener { v ->
            // 创建File对象，用于存储拍照后的图片
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            // fileprovider需要在Manifest中注册，并提供provider_paths!!!
            imageUri = FileProvider.getUriForFile(
                this,
                "com.example.flamingcoding.androidTrials.fileprovider",
                outputImage
            )
            // 启动相机程序
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val cameraImage = findViewById<ImageView>(R.id.cameraImage)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(imageUri)
                    )
                    cameraImage.setImageBitmap(rotateIfRequired(bitmap))
                }
            }

            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)
                        cameraImage.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height,
            matrix, true
        )
        bitmap.recycle() // 将不再需要的Bitmap对象回收
        return rotatedBitmap
    }


    val fromAlbum = 2
    private fun chooseImageFromAlbum() {
        val chooseImageButon = findViewById<Button>(R.id.chooseImageButton)
        val cameraImage = findViewById<ImageView>(R.id.cameraImage)
        chooseImageButon.setOnClickListener { v ->
            // 打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 指定只显示图片
            intent.type = "image/ *"
            startActivityForResult(intent, fromAlbum)
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

}