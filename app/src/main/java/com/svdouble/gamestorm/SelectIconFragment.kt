package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_select_icon.*

interface SelectIconBinder {
    var tPlayer: TPlayer
    fun checkIconFree(iconId: Int): Boolean
    fun updateIcon()
}

class SelectIconFragment() : Fragment(), SelectIconBinder {
    override lateinit var tPlayer: TPlayer
    lateinit var tParent: ExpandableMenuHeaderItem
    private var listener: OnSelectIconFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_icon, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelectIconFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onStart() {
        super.onStart()
        view?.background = ColorDrawable(Color.argb(100, 0, 0, 0))
        si_footer?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        val adapter = IconAdapter(context!!, this, TIcons)
        si_content.adapter = adapter
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnSelectIconFragmentInteractionListener {
        fun checkIconFree(iconId: Int): Boolean
    }

    override fun checkIconFree(iconId: Int) =
            listener!!.checkIconFree(iconId)

    override fun updateIcon() {
        for (i in 0 until TIcons.size)
            if (TIcons[i] != tPlayer.chipId)
                (si_content.getChildAt(i) as ImageButton).setBackgroundColor(Color.WHITE)
        Picasso.get().load(tPlayer.chipId).fit().centerInside().into(tParent.imageBtn)
    }

    companion object {
        @JvmStatic
        fun newInstance(prt: ExpandableMenuHeaderItem, player: TPlayer) =
                SelectIconFragment().apply {
                    tPlayer = player
                    tParent = prt
                }
    }
}

class IconAdapter(private val context: Context, private val caller: SelectIconBinder, private val icons: Array<Int>) : BaseAdapter() {
    override fun getCount() = icons.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var image: ImageButton? = null
        if (convertView == null) {
            image = ImageButton(context)
            image.layoutParams = AbsListView.LayoutParams(300, 300)
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            if (caller.tPlayer.chipId == TIcons[position])
                image.setBackgroundColor(Color.GREEN)
            else
                image.setBackgroundColor(Color.WHITE)
            image.setOnClickListener {
                if (caller.tPlayer.chipId != TIcons[position] && caller.checkIconFree(TIcons[position])) {
                    it.setBackgroundColor(Color.GREEN)
                    caller.tPlayer.chipId = TIcons[position]
                    caller.updateIcon()
                }
            }
        } else
            image = convertView as ImageButton
        image.setImageResource(icons[position])
        return image
    }

    override fun getItem(p0: Int) = icons[p0]

    override fun getItemId(p0: Int) = p0.toLong()
}
