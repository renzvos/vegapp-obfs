package com.aamys.fresh

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.internal.InternalTokenProvider
import com.aamys.fresh.SimpleListActivity
import com.aamys.fresh.SimpleListAdapter
import com.aamys.fresh.item

class CategoryActivity : AppCompatActivity()  {
    val firestore = FirebaseFirestore.getInstance()
    val callback : SimpleListActivity.SimpleListCallback =  object : SimpleListActivity.SimpleListCallback {
        override fun OnClick(item: item) {
            val intent  =  Intent(applicationContext,SearchActivity::class.java)
            intent.putExtra("category",item.Label)
            startActivity(intent)
        }}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_category)

        val simpleListActivity : SimpleListActivity = SimpleListActivity(this,callback)
        simpleListActivity.setTitle("Categories")

        simpleListActivity.setAppTitle("Aamy's Fresh","#9C11A9")
        simpleListActivity.SetBackButton(object: SimpleListActivity.OnBackPressed {
            override fun OnClick() {
                finish();
            }
        })

        firestore.collection("category").get().addOnSuccessListener {

            val newlist = ArrayList<item>()

            for(document in it.documents)
            {
                val name = document.data?.get("categoryName").toString()
                newlist.add(item(document.id,name,3))
            }

            simpleListActivity.Update(newlist)



        }




    }


}