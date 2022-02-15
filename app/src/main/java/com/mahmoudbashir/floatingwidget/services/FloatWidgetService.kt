package com.mahmoudbashir.floatingwidget.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import com.mahmoudbashir.floatingwidget.R
import android.widget.ImageView
import android.widget.RelativeLayout
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.mahmoudbashir.floatingwidget.MainActivity


class FloatWidgetService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingWidget: View

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        Log.d("ServiceCreation: ","Created")
        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        )

        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingWidget, params)
        val collapsedView = mFloatingWidget.findViewById<View>(R.id.collapse_view)
        val expandedView = mFloatingWidget.findViewById<View>(R.id.expanded_container)
        val closeButtonCollapsed: ImageView =
            mFloatingWidget.findViewById<View>(R.id.close_btn) as ImageView

        closeButtonCollapsed.setOnClickListener { stopSelf() }

        val closeButton = mFloatingWidget.findViewById<View>(R.id.close_button) as ImageView
        closeButton.setOnClickListener {
            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE
        }

        var initialX:Int =0
        var initialY:Int=0
        var initialTouchX:Float=0f
        var initialTouchY:Float=0f
        mFloatingWidget.findViewById<RelativeLayout>(R.id.root_container).setOnTouchListener(
            object :View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when(event!!.action){
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
                            val Xdiff = (event.rawX - initialTouchX).toInt()
                            val  Ydiff = (event.rawY - initialTouchY).toInt()
                            if (Xdiff < 10 && Ydiff < 10) {
                                if (isViewCollapsed()) {
                                    collapsedView.setVisibility(View.GONE)
                                    expandedView.setVisibility(View.VISIBLE)
                                }
                            }
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (event.getRawX() - initialTouchX).toInt()
                            params.y = initialY + (event.getRawY() - initialTouchY).toInt()
                            mWindowManager.updateViewLayout(mFloatingWidget, params)
                            return true
                        }
                        else -> return false
                    }
                }
            }
        )


    }

    private fun isViewCollapsed(): Boolean {
        return mFloatingWidget == null || mFloatingWidget.findViewById<View>(R.id.collapse_view).visibility == View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingWidget != null) mWindowManager.removeView(mFloatingWidget)
    }


}