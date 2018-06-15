package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private var listener: OnLoginFragmentInteractionListener? = null

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
            if (!name.isNullOrEmpty()) {
                var newChipId: Int? = null
                for (i in TIcons)
                    if (listener!!.checkIconFree(i)) {
                        newChipId = i
                        break
                    }
                listener?.onNewPlayer(TPlayer(BasePlayer.generatePlayerId(),
                        -1, newChipId!!, name.toString()))
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }
        }
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
        fun onNewPlayer(newPlayer: BasePlayer)
        fun checkIconFree(iconId: Int): Boolean
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LoginFragment()
    }
}
