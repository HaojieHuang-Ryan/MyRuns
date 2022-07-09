package com.example.haojie_huang_myruns3.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.haojie_huang_myruns3.ViewModel.ManualViewModel


class CustomDialog : DialogFragment(), DialogInterface.OnClickListener{
    companion object{
        const val DIALOG_KEY = "dialog"
        const val TEST_DIALOG = 1
    }
    private lateinit var input: EditText
    private lateinit var title: String
    private val manualVM: ManualViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        if (dialogId == TEST_DIALOG)
        {
            val builder = AlertDialog.Builder(requireActivity())
            input = EditText(requireActivity())
            when(bundle.getString("title").toString())
            {
                "Duration"->{input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL}
                "Distance"->{input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL}
                "Calories"->{input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL}
                "Heart Rate"->{input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL}
            }
            input.setText(bundle.getString("input"))
            input.setHint(bundle.getString("hint"))
            title = bundle.getString("title").toString()
            builder.setTitle(title)
            builder.setView(input)
            builder.setPositiveButton("OK", this)
            builder.setNegativeButton("CANCEL", this)
            ret = builder.create()
        }

        if (savedInstanceState != null)
        {
            input.setText(savedInstanceState.getString("INPUT_TEXT"))
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int)
    {
        if (item == DialogInterface.BUTTON_POSITIVE)
        {
            if (input.text.toString() == "")
            {
                when(title)
                {
                    "Duration"->{manualVM.VMduration.value = 0.0}
                    "Distance"->{manualVM.VMdistance.value = 0.0}
                    "Calories"->{manualVM.VMcalories.value = 0.0}
                    "Heart Rate"->{manualVM.VMheartrate.value = 0.0}
                    "Comment"->{manualVM.VMcomment.value = input.text.toString()}
                }
            }
            else
            {
                when(title)
                {
                    "Duration"->{manualVM.VMduration.value = input.text.toString().toDouble()}
                    "Distance"->{manualVM.VMdistance.value = input.text.toString().toDouble()}
                    "Calories"->{manualVM.VMcalories.value = input.text.toString().toDouble()}
                    "Heart Rate"->{manualVM.VMheartrate.value = input.text.toString().toDouble()}
                    "Comment"->{manualVM.VMcomment.value = input.text.toString()}
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putString("INPUT_TEXT", input.text.toString())
    }
}