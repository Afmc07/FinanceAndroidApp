package br.unifor.financeapp.adapter

import android.view.View

interface SingleItemListener {
    
    fun onClick(v: View, position:Int)

    fun onLongClick(v: View, position: Int)
}