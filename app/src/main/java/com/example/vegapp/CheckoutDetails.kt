package com.renzvos.ecommercecheckout

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.vegapp.R
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout

class CheckoutDetails (){
    var Isfragment = false
    var activity : AppCompatActivity? = null
    var fragment : Fragment? = null
    var callback : ProfileCallback? = null
    var objects : ArrayList<DetailObject> = ArrayList()
    var profileImageView : ImageView? = null
    var dploader : ProgressBar? = null
    var inflator : LayoutInflater? = null
    var containew : ViewGroup? = null
    private var savedInstanceStater : Bundle? = null
    var rootviewr : View? = null
    var rootofdeliverycontent: LinearLayout? = null
    var editingall = false

    constructor(fragment : Fragment ,inflater : LayoutInflater,  container : ViewGroup, savedInstanceStater : Bundle? , callback: ProfileCallback) : this()
    {
        this.Isfragment = true
        this.inflator = inflater
        this.containew = container
        this.savedInstanceStater = savedInstanceStater
        this.callback = callback
        this.fragment = fragment
    }

    constructor(activity: AppCompatActivity,callback: ProfileCallback) : this()
    {
        this.activity = activity
        this.callback = callback
    }



    public fun ProduceLayoutForActivity(payimage : Drawable)
    {
        // activity?.supportActionBar?.hide()  - TODO Try Adding this
        inflator = activity?.layoutInflater
        activity?.setContentView(R.layout.chekoutsimple)
        rootviewr = activity!!.window.decorView.rootView
        ProduceLayout(rootviewr!!, payimage)



    }

    fun SetToolbar(color: String , Title : String , LeftButton : Boolean, callback: OnClickLeftButton)
    {
        activity?.supportActionBar?.hide()
        val toolbar = activity?.findViewById<Toolbar>(R.id.customtoolbar)
        toolbar?.setBackgroundColor(Color.parseColor(color))
        val title = activity?.findViewById<TextView>(R.id.toolbarTitle)
        title?.text = Title
        if (LeftButton) {
            val lb = activity?.findViewById<ImageView>(R.id.titleleftbutton)
            lb?.visibility = View.VISIBLE
            lb?.setOnClickListener {  callback.OnClick() }
        }
    }


    fun ProduceLayout(view : View , Payicon : Drawable)
    {
        rootofdeliverycontent = view.findViewById(R.id.deliverycontent)
        var PayImage = activity?.findViewById<ImageView>(R.id.pay)
        PayImage?.setImageDrawable(Payicon)
        var ProccedButton = view.findViewById<Button>(R.id.proceedbutton)
        ProccedButton.setOnClickListener(View.OnClickListener {
            callback?.ProceedtoPayment()
        })
    }


    public fun NewDeliveryField(detailObject: DetailObject)
    {
        val attachview = rootofdeliverycontent
        detailObject.id = objects.size
        objects.add(detailObject)
        val msg_view: View = inflator!!.inflate(R.layout.c_texteditor, attachview, false)
        detailObject.rootview = msg_view
        val editor: TextInputLayout = msg_view.findViewById(R.id.texter)
        detailObject.editorlayoutview = editor
        detailObject.editorview = editor.editText
        val valuedisplay : TextView = msg_view.findViewById(R.id.viewer)
        detailObject.displayview = valuedisplay
        val labeler : TextView = msg_view.findViewById(R.id.labeler)
        detailObject.labelview = labeler
        val EditButton : ImageButton = msg_view.findViewById(R.id.editbuttton)
        detailObject.editbutton = EditButton

        detailObject.Init()
        attachview?.addView(msg_view)

    }



    public fun EmptyFields()
    {

        rootofdeliverycontent?.removeAllViews()
        objects = ArrayList()

    }

    fun getRootView(): View? {
        return rootviewr?.findViewById<View>(R.id.parent)
    }


    fun EditAll(callback : EditAllCallback)
    {
        val Button = rootviewr?.findViewById<Button>(R.id.editbutton)
        Button?.visibility = View.VISIBLE
        Button?.setOnClickListener(View.OnClickListener {
            if(editingall)
            {
                if(callback.OnSaved(objects))
                {Button.setText("EDIT")
                    editingall = false}
            }
            else
            {
                for( obj in objects)
                { obj.EditMode() }
                editingall = true
                Button?.setText("SAVE")
            }

        })





    }

    fun EditallCompleted()
    {
        editingall = false
        val Button = rootviewr?.findViewById<Button>(R.id.editbutton)
        Button?.setText("EDIT")
    }

    fun StopLoadingBar()
    {
        var progressBar  = rootviewr?.findViewById<LinearProgressIndicator>(R.id.checkoutloading)
        progressBar?.visibility = View.INVISIBLE
    }


    fun BillSubText(text : String)
    {
        var subbill = activity?.findViewById<TextView>(R.id.subbill)
        subbill?.setText(text)
    }
    fun BillMainText(text : String)
    {
        var TotalBill = activity?.findViewById<TextView>(R.id.totalbill)
        TotalBill?.setText(text)
    }




    interface ProfileCallback
    {
        fun ProceedtoPayment()

        //callbacks on creating the class object
    }

    interface OnClickLeftButton
    {
        fun OnClick()
    }


    interface EditAllCallback
    {
        fun OnSaved(objects: ArrayList<DetailObject>): Boolean
    }





}