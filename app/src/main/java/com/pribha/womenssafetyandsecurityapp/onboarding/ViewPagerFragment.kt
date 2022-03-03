package com.pribha.womenssafetyandsecurityapp.onboarding

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.onboarding.screens.*
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class ViewPagerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(

            FirstScreen(),
            SecondScreen(),
            ThirdScreen(),
            FourthScreen(),
            FifthScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val nextButton: MaterialButton = view.findViewById(R.id.materialButton)
        val skipButton: TextView = view.findViewById(R.id.textView2)


        viewPager.adapter = adapter


        var count: Int = 0;


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                count = position
                if(position == 4)
                {
                    nextButton.text = "Get Started"
                    nextButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_main));
                    skipButton.visibility = View.INVISIBLE

                }else{

                    nextButton.text = "Next"
                    nextButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
                    skipButton.visibility = View.VISIBLE

                }

            }
        })


        val wormDotsIndicator = view.findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        wormDotsIndicator.setViewPager2(viewPager)

        val navController = requireActivity().findNavController(R.id.fragment)


        nextButton.setOnClickListener(View.OnClickListener {

            count++
            viewPager.currentItem = count

            if (count == 5)
            {
                findNavController().navigate(R.id.action_viewPagerFragment_to_mobileNoScreen3)
            }

        })

        skipButton.setOnClickListener(View.OnClickListener {
            viewPager.currentItem = 4

        })

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}