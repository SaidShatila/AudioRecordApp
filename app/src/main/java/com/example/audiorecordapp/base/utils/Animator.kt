package com.example.audiorecordapp.base.utils

import android.view.View

object Animator {

    fun scaleUp(view: View) {
        view.animate().apply {
            scaleX(1.08f)
            scaleY(1.08f)
        }.start()
    }

    fun scaleDown(view: View) {
        view.animate().apply {
            scaleX(1f)
            scaleY(1f)
        }.start()
    }

}