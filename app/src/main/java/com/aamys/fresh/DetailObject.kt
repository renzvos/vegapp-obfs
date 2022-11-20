package com.renzvos.ecommercecheckout

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.aamys.fresh.R
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text

const val CHECKOUT_TEXTOBJECT = 1
const val CHECKOUT_PHONE = 2
const val CHECKOUT_NUMBER = 3


class DetailObject (type : Int, Label: String , callbacks: OnObjectCallbacks){
    var id = 0
    var type = type
    var editing  = false
    var rootview : View? = null
    var labelview : TextView? = null
    var editorlayoutview : TextInputLayout? = null
    var editorview : EditText? = null
    private var editicon : Drawable? = null
    private var editbuttontint : String? = null
    var displayview : TextView? = null
    var editbutton : ImageButton? = null
    val label = Label
    var phoneCodeview : TextView?= null
    var phoneCode : String?= null
    var stringvalue : String? = null
    private  var editable = false
    var callbacks = callbacks

    fun SetEditable(editicon : Drawable, editbuttontint : String)
    {
        this.editicon = editicon
        this.editable = true
        this.editbuttontint = editbuttontint

    }

    fun EditMode()
    {
        labelview!!.visibility = View.GONE
        editorlayoutview!!.visibility = View.VISIBLE
        editorlayoutview!!.hint = label
        editorview!!.setText(stringvalue)
        displayview!!.visibility = View.INVISIBLE
        editbutton!!.setImageDrawable(editicon)
        editbutton!!.setImageDrawable(editicon)
        if(type == CHECKOUT_PHONE)
        phoneCodeview!!.visibility = View.VISIBLE
        editing = true
    }

    fun EditMode(editicon : Drawable)
    {
        labelview!!.visibility = View.GONE
        editorlayoutview!!.visibility = View.VISIBLE
        editorlayoutview!!.hint = label
        editorview!!.setText(stringvalue)
        displayview!!.visibility = View.INVISIBLE
        editbutton!!.setImageDrawable(editicon)
        editbutton!!.setImageDrawable(editicon)
        if(type == CHECKOUT_PHONE)
        phoneCodeview!!.visibility = View.VISIBLE
        editing = true
    }

    fun DisplayMode()
    {
        editorlayoutview!!.visibility = View.GONE
        labelview!!.visibility = View.VISIBLE
        labelview!!.setText(label)
        displayview!!.visibility = View.VISIBLE
        displayview!!.setText(stringvalue)
        editorlayoutview!!.visibility = View.INVISIBLE
        if(type == CHECKOUT_PHONE)
        phoneCodeview!!.visibility = View.GONE
        editing = false
    }

    fun StartUp()
    {
        DisplayMode()
        if(type == CHECKOUT_PHONE)
        {
            editorview!!.inputType = InputType.TYPE_CLASS_PHONE
            editorview!!.setEms(10)
            editorview!!.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(13)))
            phoneCodeview?.setText(phoneCode)
        }
        else if(type == CHECKOUT_NUMBER)
        {
            editorview!!.inputType = InputType.TYPE_CLASS_NUMBER
            editorview!!.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(6)))

        }

        if(editable == true)
        {
            editbutton!!.visibility = View.VISIBLE
            editbutton!!.backgroundTintList = (ColorStateList(arrayOf(intArrayOf()), intArrayOf(
                Color.parseColor(editbuttontint))))
            editbutton!!.backgroundTintMode = PorterDuff.Mode.SRC_OVER
            editbutton?.setOnClickListener(View.OnClickListener {
                if(editing == false)
                {
                    EditMode()
                }
                else
                {
                    editbutton!!.backgroundTintList = (ColorStateList(arrayOf(intArrayOf()), intArrayOf(
                        Color.parseColor("#808080"))))
                    editbutton!!.backgroundTintMode = PorterDuff.Mode.SRC_OVER
                    callbacks.OnObjectEditingFinished(this)
                }


            })
        }




    }

    fun SetStringValue(Value : String)
    {
        stringvalue = Value
        displayview?.setText(Value)
    }



    fun getLayout(): Int
    {
        if(type == CHECKOUT_PHONE)
            return R.layout.c_texteditor_phone
        else
            return R.layout.c_texteditor
    }

    fun InitialisationFromView(view: View)
    {
        rootview = view
        val editor: TextInputLayout = view.findViewById(R.id.texter)
        editorlayoutview = editor
        editorview = editor.editText
        val valuedisplay : TextView = view.findViewById(R.id.viewer)
        displayview = valuedisplay
        val labeler : TextView = view.findViewById(R.id.labeler)
        labelview = labeler
        val EditButton : ImageButton = view.findViewById(R.id.editbuttton)
        editbutton = EditButton

        if(type == CHECKOUT_PHONE)
        {
            phoneCodeview = view.findViewById<TextView>(R.id.code)
        }

    }

    fun Startup()
    {

    }



    interface OnObjectCallbacks
    {
        fun OnObjectEditingFinished(profileObjects: DetailObject)

    }


}
