package com.renzvos.profileeditor

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.profileeditor.ProfileEditor
import com.example.vegapp.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.massivedisaster.widget.ArcToolbarView
import com.massivedisaster.widget.extensions.setAppBarLayout

const val ARCTITLEDESIGN  = 1
const val SIMPLELAYOUT_ACTIVITY = 2
const val SIMPLELAYOUT_FRAGMENT = 3

class ProfileLayoutDesign {
    private var Design = 0
    private var TitleBarColor : String?  =null
    private var Title: String? = null
    private var BackIcon : Boolean = false
    private var backPressed : OnBackPressed? = null

    public var EditAllButton : Button? = null
    public var horizontalloading : LinearProgressIndicator? = null



    fun ArcLayoutForActivity(TitleBarColor : String, Title : String)
    {
        Design = ARCTITLEDESIGN
        this.Title = Title
        this.TitleBarColor = TitleBarColor
    }

    fun SimpleLayoutForctivity(TitleBarColor : String, Title : String)
    {
        Design = SIMPLELAYOUT_ACTIVITY
        this.Title = Title
        this.TitleBarColor = TitleBarColor
        this.BackIcon = false
    }

    fun SimpleLayoutForctivity(TitleBarColor : String, Title : String, backPressed: OnBackPressed )
    {
        Design = SIMPLELAYOUT_ACTIVITY
        this.Title = Title
        this.TitleBarColor = TitleBarColor
        this.BackIcon = true
        this.backPressed = backPressed
    }

    fun SimpleLayoutForFragment()
    {
        Design = SIMPLELAYOUT_FRAGMENT
    }

    fun Render(Activity : AppCompatActivity)
    {
        if (Design == ARCTITLEDESIGN)
        {


        }
        else if(Design == SIMPLELAYOUT_ACTIVITY)
        {
            Activity?.supportActionBar?.hide()
            Activity?.setContentView(R.layout.profile_simple_activity)
            val toolbar: Toolbar = Activity?.findViewById<Toolbar>(R.id.customtoolbar)
            toolbar.setBackgroundColor(Color.parseColor(TitleBarColor))
            val title: TextView = Activity.findViewById<TextView>(R.id.toolbarTitle)
            title.text = Title
            EditAllButton = Activity?.findViewById<Button>(R.id.editallbutton)
            horizontalloading =Activity?.findViewById(R.id.profileloading)
            Log.i("RZP", "Render: " + "Rendering SImple Activity")


            if(BackIcon)
            { var BackIcon = Activity?.findViewById<ImageView>(R.id.titleleftbutton)
                BackIcon?.visibility = View.VISIBLE
                BackIcon?.setOnClickListener(View.OnClickListener {
                    backPressed?.OnClick()
                })
            }

        }

    }

    fun Render(Fragment : Fragment, profilecontext : ProfileEditor): View
    {
        val view = profilecontext.fragment?.layoutInflater?.inflate(R.layout.profileforftagment, profilecontext.containew, false);
        if(Design == SIMPLELAYOUT_FRAGMENT)
        {


        }

        return view!!
    }

     interface OnBackPressed{
        fun OnClick()
    }


}