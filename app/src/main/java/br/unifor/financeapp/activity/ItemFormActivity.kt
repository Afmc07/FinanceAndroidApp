package br.unifor.financeapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.*
import br.unifor.financeapp.R
import br.unifor.financeapp.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ItemFormActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mItemFormTitle:TextView
    private lateinit var mItemFormName:EditText
    private lateinit var mItemFormAm:EditText
    private lateinit var mItemFormReceita:RadioButton
    private lateinit var mItemFormDespesa:RadioButton
    private lateinit var mItemFormButton:Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private val handler= Handler(Looper.getMainLooper())

    private var mItemId=""
    private var mType=-1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_form)

        mAuth= FirebaseAuth.getInstance()
        mDatabase= FirebaseDatabase.getInstance()

        mItemFormTitle= findViewById(R.id.item_add_textView_form_name)
        mItemFormName= findViewById(R.id.item_add_editText_name)
        mItemFormAm= findViewById(R.id.item_add_editText_amount)
        mItemFormReceita= findViewById(R.id.item_add_radioButton_receita)
        mItemFormDespesa= findViewById(R.id.item_add_radioButton_despesa)

        mItemFormButton= findViewById(R.id.item_add_Button_save)
        mItemFormButton.setOnClickListener(this)

        mItemId= intent.getStringExtra("itemId") ?:""
        mType= intent.getIntExtra("type", -1)

        if(mItemId.isNotEmpty()){

            if(mType==1){
                val queryR= mDatabase.reference.child("users/${mAuth.uid}/items/receita/${mItemId}").orderByChild("id")

                queryR.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val item= snapshot.getValue(Item::class.java)

                        handler.post{
                            mItemFormTitle.text= "Edit Item"
                            mItemFormName.text= Editable.Factory.getInstance().newEditable(item?.name)
                            mItemFormAm.text= Editable.Factory.getInstance().newEditable(item?.amm)

                            when(item?.type){
                                "receita"->{
                                    mItemFormReceita.toggle()
                                }
                                "despesa"->{
                                    mItemFormDespesa.toggle()
                                }
                            }
                            val rof= mDatabase.getReference("/users/${mAuth.uid!!}/items/receita/$mItemId")
                            rof.removeValue()
                        }
                    }
                })
            }

            else if(mType==0){
                val queryD= mDatabase.reference.child("users/${mAuth.uid}/items/despesa/${mItemId}").orderByChild("id")

                queryD.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val item= snapshot.getValue(Item::class.java)

                        handler.post{
                            mItemFormTitle.text= "Edit Item"
                            mItemFormName.text= Editable.Factory.getInstance().newEditable(item?.name)
                            mItemFormAm.text= Editable.Factory.getInstance().newEditable(item?.amm)

                            when(item?.type){
                                "receita"->{
                                    mItemFormReceita.toggle()
                                }
                                "despesa"->{
                                    mItemFormDespesa.toggle()
                                }
                            }

                            val rof= mDatabase.getReference("/users/${mAuth.uid!!}/items/despesa/$mItemId")
                            rof.removeValue()
                        }
                    }
                })
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.item_add_Button_save->{

                val name= mItemFormName.text.toString()
                val amount= mItemFormAm.text.toString()
                val receita= mItemFormReceita.isChecked
                val despesa= mItemFormDespesa.isChecked

                if(name.isEmpty()){
                    mItemFormName.error= "A name must be provided"
                    return
                }
                if(amount.isEmpty()){
                    mItemFormAm.error= "An amount must be provided"
                    return
                }
                if(!receita && !despesa){
                    handler.post{
                        Toast.makeText(applicationContext, "Please mark one of the given options", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                var type= ""
                if(receita){
                    type="receita"
                }
                else if(despesa){
                    type="despesa"
                }

                if(mItemId.isEmpty()){
                    val itemId= mDatabase.reference.child("/users/${mAuth.uid!!}/items/$type").push().key
                    val ref= mDatabase.getReference("/users/${mAuth.uid!!}/items/$type/$itemId")

                    val item= Item(itemId!!, name, amount, type)
                    ref.setValue(item)
                    finish()
                }
                else{
                    val ref= mDatabase.getReference("/users/${mAuth.uid!!}/items/$type/$mItemId")
                    val item= Item(mItemId, name, amount, type)
                    ref.setValue(item)
                    finish()
                }
            }
        }
    }
}