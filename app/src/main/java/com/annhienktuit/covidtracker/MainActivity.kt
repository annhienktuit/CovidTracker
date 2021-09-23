package com.annhienktuit.covidtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val date = getCurrentDateTime()
        tvDate.text = date.toString("M/dd/yyyy")
        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.onItemSelectedListener = this
    }
    private fun fetchData(country: String){
        val url = "https://disease.sh/v3/covid-19/countries/$country"
        val queue = Volley.newRequestQueue(this)
        val tvCases = findViewById<TextView>(R.id.tvCases)
        val tvDeaths = findViewById<TextView>(R.id.tvDeaths)
        val tvRecovered = findViewById<TextView>(R.id.tvRecovered)
        val imgFlag = findViewById<ImageView>(R.id.imgFlag)
        lateinit var flagURL:String
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                var jsonObj = JSONObject(response.toString())
                tvCases.text = jsonObj.getString("cases")
                tvRecovered.text = jsonObj.getString("recovered")
                tvDeaths.text = jsonObj.getString("deaths")
                try {
                    var countryInfo = jsonObj.getJSONObject("countryInfo")
                    flagURL = countryInfo.getString("flag")
                    imgFlag.tag = flagURL
                    val download = DownloadImageTask()
                    download.execute(imgFlag)
                }
                catch (e:Exception){
                    Log.e("error: ",e.toString())
                }

            },
            { tvCases.text = "That didn't work!" })
        queue.add(stringRequest)
    }

    private fun fetchTodayData(country: String){
        val url = "https://coronavirus-19-api.herokuapp.com/countries/$country"
        val queue = Volley.newRequestQueue(this)
        val tvTodayCases = findViewById<TextView>(R.id.tvTodayCases)
        val tvTodayDeaths = findViewById<TextView>(R.id.tvTodayDeaths)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                var jsonObj = JSONObject(response.toString())
                val case = jsonObj.getString("todayCases")
                val death = jsonObj.getString("todayDeaths")
                tvTodayCases.text = "+$case"
                tvTodayDeaths.text = "+$death"
            },
            { tvTodayCases.text = "That didn't work!" })
        queue.add(stringRequest)
    }

    private fun fetchVaccination(country: String){
        val url = "https://disease.sh/v3/covid-19/vaccine/coverage/countries/$country?lastdays=1&fullData=true"
        val queue = Volley.newRequestQueue(this)
        val tvVaccination = findViewById<TextView>(R.id.tvVaccination)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val date = getCurrentDateTime()
                var jsonObj = JSONObject(response.toString())
                var vaccinationInfo = jsonObj.getJSONArray("timeline")
                for(i in 0 until vaccinationInfo.length()){
                    var jsonInner:JSONObject = vaccinationInfo.getJSONObject(i)
                    tvVaccination.text = jsonInner.get("total").toString()
                }
            },
            { tvVaccination.text = "That didn't work!" })
        queue.add(stringRequest)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val countryName = p0!!.getItemAtPosition(p2).toString()
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        fetchData(countryName)
        fetchTodayData(countryName)
        fetchVaccination(countryName)
        tvTitle.text = "$countryName Statistics"
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

}