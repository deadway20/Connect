package com.coder_x.connect.profile
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.coder_x.connect.R
import com.coder_x.connect.databinding.FragmentEditServerBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class EditServerFragment : Fragment() {

    // هذا المتغير سيمنحنا وصولاً آمناً لكل عناصر الواجهة
    private var _binding: FragmentEditServerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditServerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSummaryCards()

    }

    private fun setupSummaryCards() {
        // يمكنك الآن الوصول للعناصر مباشرة
        binding.tvTotalHours.text = "120"
        binding.tvTotalLate.text = "45 د"
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
