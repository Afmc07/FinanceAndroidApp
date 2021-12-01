package br.unifor.financeapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unifor.financeapp.R
import br.unifor.financeapp.model.Item

class itemAdapter(val items:List<Item>):RecyclerView.Adapter<itemAdapter.itemViewHolder>(){

    var listener: SingleItemListener? = null

    class itemViewHolder(val thing:View, listener: SingleItemListener?):RecyclerView.ViewHolder(thing) {
        val itemName: TextView = thing.findViewById(R.id.single_item_textView_name)
        val itemAmount: TextView = thing.findViewById(R.id.single_item_textView_amount)
        val itemType:View= thing.findViewById(R.id.single_item_view_type)

        init {
            itemView.setOnClickListener{
                listener?.onClick(it, adapterPosition)
            }

            itemView.setOnLongClickListener{
                listener?.onLongClick(it, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false)
        val holder= itemViewHolder(itemView, listener)
        return holder
    }

    override fun getItemCount(): Int {
        return  items.size
    }

    fun setSingleItemListener(listener: SingleItemListener?){
        this.listener= listener
    }

    override fun onBindViewHolder(holder: itemViewHolder, position: Int) {

        holder.itemName.text= items[position].name
        holder.itemAmount.text= items[position].amm

        if(items[position].type=="receita"){
            holder.itemType.setBackgroundColor(Color.GREEN)
        }
        else if(items[position].type=="despesa"){
            holder.itemType.setBackgroundColor(Color.RED)
        }
    }
}