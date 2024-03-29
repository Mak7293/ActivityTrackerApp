package com.example.activity_tracker.util

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.activity_tracker.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MaterialBottomSheet : BottomSheetDialogFragment() {

    private var bottomSheetBinding: BottomSheetBinding? = null

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetBinding = BottomSheetBinding.inflate(layoutInflater)
        return bottomSheetBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBinding?.ivActivityCycling?.setOnClickListener {
            sharedPref
                .edit()
                .putString(Constants.Activity_Type, Constants.ACTIVITY_CYCLING)
                .apply()
            dismiss()
        }
        bottomSheetBinding?.ivActivityRunning?.setOnClickListener {
            sharedPref
                .edit()
                .putString(Constants.Activity_Type, Constants.ACTIVITY_RUN_OR_WALK)
                .apply()
            dismiss()
        }

        dialog?.setCancelable(false)
        val modalBottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        modalBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED


    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }

}