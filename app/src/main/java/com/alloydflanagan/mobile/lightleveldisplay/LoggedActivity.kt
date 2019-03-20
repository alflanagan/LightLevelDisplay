package com.alloydflanagan.mobile.lightleveldisplay

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.ContextMenu
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class LoggedActivity: AppCompatActivity() {
    override fun onPause() {
        Timber.d("onPause()")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        Timber.d("onSaveInstanceState(Bundle?, PersistableBundle?)")
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Timber.d("onTouchEvent(MotionEvent?)")
        return super.onTouchEvent(event)
    }

    override fun onUserInteraction() {
        Timber.d("onUserInteraction()")
        super.onUserInteraction()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        Timber.d("onRestoreInstanceState(Bundle?)")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        Timber.d("onRestoreInstanceState(Bundle?, PersistableBundle?)")
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        Timber.d("onAttachFragment(Fragment)")
        super.onAttachFragment(fragment)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Fragment is deprecated", ReplaceWith("androidx.fragment.app.Fragment"))
    override fun onAttachFragment(fragment: android.app.Fragment?) {
        Timber.d("deprecated onAttachFragment(android.app.Fragment)")
        super.onAttachFragment(fragment)
    }

    override fun onDestroy() {
        Timber.d("onDestroy()")
        super.onDestroy()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        Timber.d("onCreateContextMenu")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        Timber.d("onActivityReenter")
        super.onActivityReenter(resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate(Bundle)")
        super.onCreate(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        Timber.d("onCreate(Bundle, PersistableBundle)")
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onStart() {
        Timber.d("onStart()")
        super.onStart()
    }

    override fun onResume() {
        Timber.d("onResume()")
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Timber.d("onSaveInstanceState()")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Timber.d("onStop()")
        super.onStop()
    }

    override fun onRestart() {
        Timber.d("onRestart()")
        super.onRestart()
    }
}
