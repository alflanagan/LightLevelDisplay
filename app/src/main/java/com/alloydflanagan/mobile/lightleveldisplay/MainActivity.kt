package com.alloydflanagan.mobile.lightleveldisplay

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LoggedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }
}
