package com.hnhy.socketdemokotlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hnhy.socketdemokotlin.R

class SelectTypeActivity : BaseSocketActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_type)
        initData()
    }

    private fun initData() {
        findViewById<Button>(R.id.btn_client).setOnClickListener {
            jumpActivity(ClientPlusActivity::class.java)
        }

        findViewById<Button>(R.id.btn_server).setOnClickListener {
            jumpActivity(ServerPlusActivity::class.java)
        }
    }
}