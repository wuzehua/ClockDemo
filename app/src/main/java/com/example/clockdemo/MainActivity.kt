package com.example.clockdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ref = getSharedPreferences("data", Context.MODE_PRIVATE)
        val show = ref.getBoolean("ShowBoard",true)
        clock.setShowBoard(show)

        clock.setOnClickListener(object: View.OnClickListener
        {
            override fun onClick(v: View?) {
                clock.changeView()
                clock.invalidate()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editor.putBoolean("ShowBoard",clock.getShowBoard())
        editor.apply()
    }
}
