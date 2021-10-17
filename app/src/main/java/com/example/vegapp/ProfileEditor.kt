package com.example.profileeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.massivedisaster.widget.ArcToolbarView
import com.massivedisaster.widget.extensions.setAppBarLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import android.net.Uri
import android.util.Log
import com.example.vegapp.R
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.renzvos.profileeditor.*


class ProfileEditor(){
    var Isfragment = false
    var activity : AppCompatActivity? = null
    var fragment : Fragment? = null
    var callback : ProfileCallback? = null
    var objects : ArrayList<ProfileObjects> = ArrayList()
    var headings : ArrayList<Heading>  = ArrayList()
    var profileImageView : ImageView? = null
    var dploader : ProgressBar? = null
    var inflator : LayoutInflater? = null
    var containew : ViewGroup? = null
    private var savedInstanceStater : Bundle? = null
    var rootviewr : View? = null
    var rootofheading: LinearLayout? = null
    var layoutDesign : ProfileLayoutDesign? = null

    var dpeditedcallback : DpEditedCallback? = null

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



    public fun ProduceLayoutForActivity(layoutDesign: ProfileLayoutDesign,inflater: LayoutInflater)
    {
       // activity?.supportActionBar?.hide()  - TODO Try Adding this
        this.layoutDesign = layoutDesign
        layoutDesign.Render(activity!!)
        inflator = inflater
        rootviewr = activity!!.window.decorView.rootView
        ProduceLayout(rootviewr!!)

    }

    public fun ProduceLayoutForFragment(layoutDesign: ProfileLayoutDesign): View? {
        val view = layoutDesign.Render(fragment!!,this)
        rootviewr = view
        ProduceLayout(view)
        return view
    }

     fun ProduceLayout(view : View)
    {
         rootofheading = view.findViewById(R.id.profilecontent)
    }

    public fun setName(Title: String)
    {
        val namecol = rootviewr?.findViewById<TextView>(R.id.namecol)
        namecol?.setText(Title)




    }


    public fun NewObject(profileObject: ProfileObjects)
    {
        val attachview = GetAttachView(profileObject)
        profileObject.id = objects.size
        objects.add(profileObject)

        val msg_view: View = inflator!!.inflate(R.layout.texteditor, attachview, false)
        profileObject.rootview = msg_view
        val editor: TextInputLayout = msg_view.findViewById(R.id.texter)
        profileObject.editorlayoutview = editor
        profileObject.editorview = editor.editText
        val valuedisplay : TextView = msg_view.findViewById(R.id.viewer)
        profileObject.displayview = valuedisplay
        val labeler : TextView = msg_view.findViewById(R.id.labeler)
        profileObject.labelview = labeler
        val EditButton : ImageButton = msg_view.findViewById(R.id.editbuttton)
        profileObject.editbutton = EditButton


        profileObject.Init()
        attachview.addView(msg_view)

    }


    public fun setDP( url : String)
    {
        profileImageView = rootviewr?.findViewById(R.id.profiledp)
        dploader = rootviewr?.findViewById(R.id.dploader)
        Picasso.get().load(url).into(profileImageView,object : Callback
        {
            override fun onSuccess()
            {
                dploader!!.visibility = View.INVISIBLE
                profileImageView!!.visibility = View.VISIBLE
            }

            override fun onError(e: Exception?) {
                TODO("Not yet implemented")
            }
        })

    }

    public fun EmptyFields()
    {

        rootofheading?.removeAllViews()
        objects = ArrayList()
        headings = ArrayList()

    }

     fun AttachDpeditor(buttonColor : String, dpEditedCallback: DpEditedCallback)
    {
        this.dpeditedcallback = dpEditedCallback
        val msg_view: View = inflator!!.inflate(R.layout.dpeditor, rootofheading, false)
        rootofheading?.addView(msg_view)
        val dpeditbutton = msg_view.findViewById<ImageView>(R.id.dpeditbutton)
        dpeditbutton!!.backgroundTintList = (ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.parseColor(buttonColor))))
        dpeditbutton!!.backgroundTintMode = PorterDuff.Mode.SRC_OVER

        dpeditbutton.setOnClickListener(View.OnClickListener {

            val i = Intent()
            i.setType("image/*")
            i.setAction(Intent.ACTION_GET_CONTENT)



            if (Isfragment)
            {
                fragment?.activity?.startActivityForResult(Intent.createChooser(i, "Select Picture"), 3256);
            }
            else
            {
                activity?.startActivityForResult(Intent.createChooser(i, "Select Picture"), 3256);
            }


        })
    }

    fun getRootView(): View? {
        return rootviewr?.findViewById<View>(R.id.parent)
    }


    private fun AddHeadingandReturnView(rootofheading: LinearLayout,heading : String): LinearLayout
    {
        Log.i("RZP", "AddHeadingandReturnView: ")
        val headingrootview = inflator!!.inflate(R.layout.heading, rootofheading, false)
        val attachview = headingrootview.findViewById<LinearLayout>(R.id.headingcontent)
        val headingtext : TextView = headingrootview.findViewById(R.id.headingheader)
        headingtext.setText(heading)
        rootofheading.addView(headingrootview)
        val heading = Heading(heading, headingrootview , attachview);
        headings.add(heading)
        return attachview
    }


    private fun GetAttachView(profileObject: ProfileObjects):LinearLayout
    {
        val attachview:LinearLayout
        if(profileObject.heading == null)
        {
            attachview = rootofheading!!
        }
        else
        {
            val headingid = CheckForHeading(profileObject.heading!!)
            if( headingid == 0)
            {
                attachview = AddHeadingandReturnView(rootofheading!!, profileObject.heading!!)
            }
            else
            {
                attachview = headings.get(headingid - 1).contentview
            }
        }

        return attachview;
    }

    fun EditAll(callback : EditAllCallback)
    {
        var editingall = false
        val Button = layoutDesign?.EditAllButton
        Button?.visibility = View.VISIBLE
        Button?.setOnClickListener(View.OnClickListener {
            if(editingall)
            {
                if(callback.OnSaved(objects))
                {Button?.setText("EDIT")
                    for (obj in objects)
                    {obj.DisplayMode()}
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
     fun StopLoadingBar()
    {
        var progressBar  = layoutDesign?.horizontalloading
        progressBar?.visibility = View.INVISIBLE
    }

    private fun CheckForHeading(heading : String): Int
    {
        if(headings.size >= 1) {
            for (i in 0..headings.size - 1) {
                if (headings.get(i).HeadingString == heading) {
                    return i + 1
                }
            }
        }
        return 0
    }



     interface ProfileCallback
    {
        //callbacks on creating the class object
    }

    interface EditAllCallback
    {
        fun OnSaved(objects: ArrayList<ProfileObjects>): Boolean
    }

    interface DpEditedCallback
    {
        fun OnEdit(uri: Uri)
    }

    public class Heading(headingstring: String, rootview : View, contentview : LinearLayout)
    {
        val HeadingString = headingstring
        val rootview  = rootview
        val contentview = contentview
    }







}









