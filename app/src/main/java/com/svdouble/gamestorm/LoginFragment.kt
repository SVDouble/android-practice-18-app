package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*


/*
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
*/

class LoginFragment : Fragment() {
    /*
    private var param1: String? = null
    private var param2: String? = null
    */
    private var listener: OnLoginFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        */
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        view?.background = ColorDrawable(Color.argb(100, 0, 0, 0))
        lf_footer_cancel?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        lf_footer_apply.setOnClickListener {
            val name = lf_content_nick_field.text
            if (!name.isNullOrEmpty())
                listener?.onNewPlayer(TPlayer(BasePlayer.generatePlayerId(),
                        -1, temp++ % 2), name.toString())
        }
        /*
        view?.isFocusableInTouchMode = true
        view?.requestFocus() */
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnLoginFragmentInteractionListener {
        fun onNewPlayer(newPlayer: BasePlayer, name: String)
    }

    companion object {
        var temp = 0

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LoginFragment()
                        /* .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                } */
    }
}
