package br.unifor.financeapp.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unifor.financeapp.R
import br.unifor.financeapp.adapter.SingleItemListener
import br.unifor.financeapp.adapter.itemAdapter
import br.unifor.financeapp.model.Item
import br.unifor.financeapp.model.UserWithItems
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), View.OnClickListener, SingleItemListener{

    private lateinit var mItemRecycleView:RecyclerView
    private lateinit var mReceitaRecycleView: RecyclerView
    private lateinit var mItemAdd:FloatingActionButton
    private lateinit var itemAdapter:itemAdapter

    private val handler= Handler(Looper.getMainLooper())
    private var mItemList= mutableListOf<Item>()
    private var mReceitaList= mutableListOf<Item>()
    private var mReceitaFinal= mutableListOf<Item>()
    private var mDespesaFinal= mutableListOf<Item>()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth= FirebaseAuth.getInstance()
        mDatabase= FirebaseDatabase.getInstance()

        mItemRecycleView= findViewById(R.id.main_recycleView_items)
        mReceitaRecycleView= findViewById(R.id.main_recycleView_receita)
        mItemAdd= findViewById(R.id.main_floatingButton_add_item)
        mItemAdd.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        val dialog= ProgressDialog(this)
        dialog.setTitle("Finance Manager - Please Wait")
        dialog.isIndeterminate= true
        dialog.show()

        val queryR= mDatabase.reference.child("users/${mAuth.uid}/items/receita").orderByKey()
        val queryD= mDatabase.reference.child("users/${mAuth.uid}/items/despesa").orderByKey()

        queryR.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                mReceitaList.clear()
                mReceitaFinal.clear()
                snapshot.children.forEach{
                    val item= it.getValue(Item::class.java)
                    mReceitaList.add(item!!)
                }
                if(mReceitaList.isNotEmpty()){
                    for(a in mReceitaList.size-1 downTo mReceitaList.size-5){
                        mReceitaFinal.add(mReceitaList[a])
                        if(a==0){
                            break
                        }
                    }
                }

                handler.post{
                    dialog.dismiss()
                    itemAdapter = itemAdapter(mReceitaFinal)
                    itemAdapter.setSingleItemListener(this@MainActivity)
                    val llm= LinearLayoutManager(applicationContext)

                    mReceitaRecycleView.apply {
                        adapter= itemAdapter
                        layoutManager= llm
                    }
                }
            }

        })
        queryD.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                mItemList.clear()
                mDespesaFinal.clear()
                snapshot.children.forEach{
                    val item= it.getValue(Item::class.java)
                     mItemList.add(item!!)
                }

                if(mItemList.isNotEmpty()){
                    for(a in mItemList.size-1 downTo mItemList.size-5){
                        mDespesaFinal.add(mItemList[a])
                        if(a==0){
                            break
                        }
                    }
                }

                handler.post{
                    dialog.dismiss()
                    itemAdapter = itemAdapter(mDespesaFinal)
                    itemAdapter.setSingleItemListener(this@MainActivity)
                    val llm= LinearLayoutManager(applicationContext)

                    mItemRecycleView.apply {
                        adapter= itemAdapter
                        layoutManager= llm
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.main_floatingButton_add_item->{
                val it= Intent(applicationContext, ItemFormActivity::class.java)
                startActivity(it)
            }
        }
    }

    override fun onClick(v: View, position: Int) {
        val it= Intent(applicationContext, ItemFormActivity::class.java)
        val ItemView= v as CardView
        if(ItemView.parent==mItemRecycleView){
            it.putExtra("itemId", mDespesaFinal[position].id)
            it.putExtra("type", 0)
        }
        else{
            it.putExtra("itemId", mReceitaFinal[position].id)
            it.putExtra("type", 1)
        }

        startActivity(it)
    }

    override fun onLongClick(v: View, position: Int) {
        val ItemView= v as CardView

        if(ItemView.parent==mItemRecycleView){
            val dialog= AlertDialog.Builder(this)
                .setTitle("Finance App")
                .setMessage("Do you wish to delete this item?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    val ref= mDatabase.getReference("users/${mAuth.uid}/items/despesa/${mDespesaFinal[position].id}")
                    ref.removeValue().addOnCompleteListener{
                        if (it.isSuccessful){
                            handler.post{
                                dialog.dismiss()
                                itemAdapter.notifyItemRemoved(position)
                            }
                        }
                        else{
                            Toast.makeText(MainActivity@this, "There was an error, please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
        else{
            val dialog= AlertDialog.Builder(this)
                .setTitle("Finance App")
                .setMessage("Do you wish to delete this item?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    val ref= mDatabase.getReference("users/${mAuth.uid}/items/receita/${mReceitaFinal[position].id}")
                    ref.removeValue().addOnCompleteListener{
                        if (it.isSuccessful){
                            handler.post{
                                dialog.dismiss()
                                itemAdapter.notifyItemRemoved(position)
                            }
                        }
                        else{
                            Toast.makeText(MainActivity@this, "There was an error, please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }
}