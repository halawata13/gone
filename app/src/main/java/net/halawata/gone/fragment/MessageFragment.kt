package net.halawata.gone.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_message.*
import net.halawata.gone.R

class MessageFragment : Fragment() {
    private var message: String? = null
    private var useOkButton: Boolean? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            message = it.getString("message")
            useOkButton = it.getBoolean("useOkButton")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        message?.let {
            messageTextView.text = message
        }

        useOkButton?.let {
            okButton.setOnClickListener {
                onOkButtonPressed()
            }

            okButton.visibility = View.VISIBLE

        } ?: run {
            okButton.visibility = View.GONE
        }

        return view
    }

    private fun onOkButtonPressed() {
        listener?.onOkButtonPressed()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onOkButtonPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance(message: String, useOkButton: Boolean) =
                MessageFragment().apply {
                    arguments = Bundle().apply {
                        putString("message", message)
                        putBoolean("useOkButton", useOkButton)
                    }
                }
    }
}
