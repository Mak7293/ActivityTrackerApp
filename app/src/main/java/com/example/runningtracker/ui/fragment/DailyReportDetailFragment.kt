package com.example.runningtracker.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.runningtracker.R
import com.example.runningtracker.databinding.CancelRunDialogBinding
import com.example.runningtracker.databinding.FragmentDailyReportDetailBinding
import com.example.runningtracker.models.day.Day
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class DailyReportDetailFragment : Fragment() {

    private var binding: FragmentDailyReportDetailBinding? = null
    private lateinit var day: Day
    private val args: DailyReportDetailFragmentArgs by navArgs()
    private var menu: Menu? = null

    @Inject
    lateinit var sdf: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDailyReportDetailBinding.inflate(layoutInflater)
        return binding?.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        day = args.day
        setUpToolbar()
        Toast.makeText(requireContext(),day.toString(), Toast.LENGTH_SHORT).show()

    }
    private fun setUpToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding?.toolbar?.setNavigationIcon(
                ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_back
            ))
            binding?.toolbarTv?.text = "Date: ${sdf.format(day.day.values.first().first().date!!)}"
            binding?.toolbar?.setNavigationOnClickListener {
                val navOptions = NavOptions
                    .Builder()
                    .setPopUpTo(R.id.dailyReportDetailFragment, true)
                    .build()
                findNavController().navigate(
                    R.id.action_dailyReportDetailFragment_to_statisticsFragment,
                    null,
                    navOptions
                )
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.daily_report_menu,menu)
        this.menu = menu
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.delete_day  ->   {
                showDeleteDayDialog(
                    resources.getString(R.string.delete_day_header),
                    resources.getString(R.string.delete_day_content)
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDeleteDayDialog(header: String, content: String){
        val dialog: Dialog = Dialog(requireContext(),R.style.DialogTheme)
        val dialogBinding = CancelRunDialogBinding.inflate(layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            dialogBinding.apply {
                tvContent.text = content
                tvHeader.text = header
                btnYes.setOnClickListener {
                    Toast.makeText(requireContext(),"delete!!",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                btnNo.setOnClickListener {
                    dialog.dismiss()
                }
            }
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}