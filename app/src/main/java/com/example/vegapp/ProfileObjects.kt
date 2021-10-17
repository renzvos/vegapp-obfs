package com.renzvos.profileeditor

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import android.R.attr.maxLength
import android.text.InputFilter


const val Profile_TEXTOBJECT = 1
const val Profile_PHONE = 2
const val Profile_NUMBER = 3

class ProfileObjects (type : Int, Label : String, callbacks : OnObjectCallbacks){

     var id = 0
     val type = type
     var editing  = false
     var heading : String? = null
     var rootview : View? = null
     var labelview : TextView? = null
     var editorlayoutview : TextInputLayout? = null
     var editorview : EditText? = null
     private var editicon : Drawable? = null
     private var editbuttontint : String? = null
     var displayview : TextView? = null
     var editbutton : ImageButton? = null
     val label = Label
     var stringvalue : String? = null
     private  var editable = false
     var callbacks = callbacks

     fun SetEditable(editicon : Drawable,  editbuttontint : String)
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
          editing = true
     }

     fun DisplayMode()
     {
          labelview!!.visibility = View.VISIBLE
          labelview!!.setText(label)
          displayview!!.visibility = View.VISIBLE
          displayview!!.setText(stringvalue)
          editorlayoutview!!.visibility = View.INVISIBLE
     }

     fun Init()
     {
          DisplayMode()
          if(type == Profile_PHONE)
          {
               editorview!!.inputType = InputType.TYPE_CLASS_PHONE
               editorview!!.setEms(10)
               editorview!!.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(13)))
          }
          else if(type == Profile_NUMBER)
          {
               editorview!!.inputType = InputType.TYPE_CLASS_NUMBER
               editorview!!.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(6)))
          }

          if(editable == true)
          {
               editbutton!!.visibility = View.VISIBLE
               editbutton!!.backgroundTintList = (ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.parseColor(editbuttontint))))
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

     interface OnObjectCallbacks
     {
          fun OnObjectEditingFinished(profileObjects: ProfileObjects)

     }

}