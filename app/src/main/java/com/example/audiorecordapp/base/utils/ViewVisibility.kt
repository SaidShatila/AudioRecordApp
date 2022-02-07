package com.example.audiorecordapp.base.utils

import android.view.View

object ViewVisibility {

    fun viewShow(view: View) {
        view.visibility = View.VISIBLE
    }

    fun viewHide(view: View) {
        view.visibility = View.INVISIBLE
    }
}