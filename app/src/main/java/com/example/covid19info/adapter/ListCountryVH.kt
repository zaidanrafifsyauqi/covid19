package com.example.covid19info.adapter

import android.renderscript.ScriptGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.covid19info.POJO.CountriesItem
import com.example.covid19info.R
import com.example.covid19info.databinding.ListCountryBinding

class ListCountryVH(private val binding: ListCountryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    // fungsi bind digunakan untuk mendapatkan data dari recyclerview Adapter
    fun bind(data: CountriesItem, isWhite: Boolean) {

        binding.run {
            // ubah background ke warna putih
            if (isWhite) layGlobeHeader.setBackgroundResource(R.color.white) else layGlobeHeader.setBackgroundResource(R.color.dkgrey)

            // masukkan data ke pada tempatnya
            txtCountryName.text = data.country
            txtTotalCase.text = data.totalConfirmed.toString()
            txtTotalRecovered.text = data.totalRecovered.toString()
            txtTotalDeaths.text = data.totalDeaths.toString()

            // Glide menambah gambar online
            Glide.with(root.context) // with diisi oleh context dari viewholder
                .load("https://www.countryflags.io/${data.countryCode}/flat/16.png") // load diisi oleh link dari bendera
                .into(imgFlagCountry) // into diisi oleh imageView tujuan
        }
    }
}