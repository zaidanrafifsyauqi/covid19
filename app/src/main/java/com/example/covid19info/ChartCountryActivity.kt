package com.example.covid19info

import android.graphics.Color
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.covid19info.POJO.CountriesItem
import com.example.covid19info.POJO.ResponseCountry
import com.example.covid19info.databinding.ActivityChartCountryBinding
import com.example.covid19info.retrofit.CovidIntervace
import com.example.covid19info.retrofit.RetrofitService
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChartCountryActivity : AppCompatActivity() {
    //buat variabel data yang akan di terima dari main activity
    private lateinit var dataCountry: CountriesItem
    //buat variabel binding untuk view bindig
    private lateinit var binding: ActivityChartCountryBinding
    //buat variable untuk menyimpan nama sumbu x
    private val dayCases = mutableListOf<String>()

    //buat variable untuk menyimpan data kematian,sembuh,aktif,dan terkonfirmasi
    private val dataConfirmed = mutableListOf<BarEntry>()
    private val dataDeath = mutableListOf<BarEntry>()
    private val dataRecovered = mutableListOf<BarEntry>()
    private val dataActive = mutableListOf<BarEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deklarasikan inflater dan binding
        val inflater = layoutInflater
        binding = ActivityChartCountryBinding.inflate(inflater)
        //ubah setContentView menggunakan binding root
        setContentView(binding.root)

        dataCountry = intent.getParcelableExtra("DATA_COUNTRY") as CountriesItem

        binding.run {
            txtNewConfirmedCurrent.text = dataCountry.newConfirmed.toString()
            txtNewDeathsCurrent.text = dataCountry.newDeaths.toString()
            txtNewRecoveredCurrent.text = dataCountry.newRecovered.toString()
            txtTotalConfirmedCurrent.text = dataCountry.totalConfirmed.toString()
            txtTotalDeathsCurrent.text = dataCountry.totalDeaths.toString()
            txtTotalRecoveredCurrent.text = dataCountry.totalRecovered.toString()
            txtCurrent.text = dataCountry.countryCode
            txtCountryChart.text = dataCountry.country

            Glide.with(root)
                .load("https://www.countryflags.io/${dataCountry.countryCode}/flat/64.png")
                .into(imgFlagChart)
        }
        //setelah membuat fungsi getCountryData, maka panggil fungsi tersebut di oncreate
        //cek dulu apakah slug ada,jika ada baru deh buat fungsiya
        dataCountry.slug?.let { slug ->
            getContryData(slug)

        }
    }
    //buat fungsi getCountryData untuk mendapatkan data covid19 berdasarkan nama negara
    private fun getContryData(countryName : String) {
        //panggil retrofit interface (CovidIterface)
        val retrofit = RetrofitService.buildService(CovidIntervace::class.java)

        //membuat variabel format tanggal dari JSON
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T' HH:mm:SS'Z'", Locale.getDefault())
        //membuat variabel format output tanggal yang bisa dimengerti manusia
        val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        //buat androdid coroutines
        lifecycleScope.launch {
            //buat variable countrydata yang berisi dataCovid sesuai nama negara
            val countryData = retrofit.getCountryData(countryName)
            //jika data sukses diambil oleh retrofit
            if (countryData.isSuccessful) {
                //buat variable berisi data tersebut
                val dataCovid = countryData.body() as List<ResponseCountry>
                //lakukan perulangan item dari dataCovid
                dataCovid.forEachIndexed { index, responseCountry ->
                    val barConfirmed = BarEntry(index.toFloat(),responseCountry.Confirmed?.toFloat() ?:0f)
                    val barDeath = BarEntry(index.toFloat(),responseCountry.Deaths?.toFloat() ?:0f)
                    val barRecovered = BarEntry(index.toFloat(),responseCountry.Recovered?.toFloat() ?:0f)
                    val barActive = BarEntry(index.toFloat(),responseCountry.Active?.toFloat() ?:0f)

                    //tambahkan data bar di atas ke dalam dataConfirmed dll
                    dataConfirmed.add(barConfirmed)
                    dataDeath.add(barDeath)
                    dataRecovered.add(barRecovered)
                    dataActive.add(barActive)

                    // Jika ada tanggal / Date item
                    responseCountry.Date?.let { itemDate ->
                        // parse tanggal dan ubah ke bentuk yang telah diformat sesuai format output
                        val date = inputDateFormat.parse(itemDate)
                        val formattedDate = outputDateFormat.format(date as Date)
                        // tambahkan tanggal yang telah diformat ke dalam dayCases
                        dayCases.add(formattedDate)
                    }
                }


                binding.chartView.axisLeft.axisMinimum = 0f
                val labelSumbuX = binding.chartView.xAxis
                labelSumbuX.run {
                    valueFormatter = IndexAxisValueFormatter(dayCases)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setCenterAxisLabels(true)
                    isGranularityEnabled = true
                }
                val barDataConfirmed = BarDataSet(dataConfirmed,"Confirmed")
                val barDataRecorvered = BarDataSet(dataRecovered,"Recorved")
                val barDataDeath = BarDataSet(dataDeath,"Death")
                val barDataActive = BarDataSet(dataActive,"Active")

                barDataConfirmed.setColors(Color.parseColor("#F44336"))
                barDataRecorvered.setColors(Color.parseColor("#FFEB3B"))
                barDataDeath.setColors(Color.parseColor("#03DAC5"))
                barDataActive.setColors(Color.parseColor("#2196F3"))
                //membuat variabel  data berisi semua barData
                val dataChart =
                    BarData(barDataConfirmed, barDataRecorvered, barDataDeath, barDataActive)

                //buat variabel berisi spasi
                val barSpace = 0.02f
                val groupSpace = 0.3f
                val groupCount = 4f

                binding.chartView.run {
                    //Tambahkan dataChart kedalam chartview
                    data = dataChart
                    //invalidate untuk mengganti data sebelumnya
                    invalidate()
                    setNoDataTextColor(R.color.dkgrey)
                    setTouchEnabled(true)
                    description.isEnabled = false
                    xAxis.axisMinimum = 0f
                    setVisibleXRangeMaximum(0f + barData.getGroupWidth(groupSpace, barSpace) * groupCount)
                    groupBars(0f,groupSpace,barSpace)
                }

            }
        }
    }
}