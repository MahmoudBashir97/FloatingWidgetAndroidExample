package com.mahmoudbashir.floatingwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.net.Uri

import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mahmoudbashir.floatingwidget.services.FloatWidgetService


class MainActivity : AppCompatActivity() {

     private val APP_PERMISSION_REQUEST = 102;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult( intent,APP_PERMISSION_REQUEST)
        } else {

            initializeView()
        }
    }

    private fun initializeView() {
        val mButton: Button = findViewById<Button>(R.id.createBtn)

        mButton.setOnClickListener {
            startService(Intent(this,FloatWidgetService::class.java))
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_PERMISSION_REQUEST && resultCode == RESULT_OK) {
        initializeView()
        }else{Toast.makeText(this,"Draw over other app permission not enable.",Toast.LENGTH_LONG).show()}
    }
}