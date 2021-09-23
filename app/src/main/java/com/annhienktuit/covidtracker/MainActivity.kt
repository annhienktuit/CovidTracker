package com.annhienktuit.covidtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchData()
    }
    private fun fetchData(){
        val url = "https://disease.sh/v3/covid-19/countries/vietnam"
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

}