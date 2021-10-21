package com.example.getandpostlocation


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class MainActivity : AppCompatActivity() {
    lateinit var edName: EditText
    lateinit var edLocation: EditText
    lateinit var btSave: Button
    lateinit var edNameGetLocation: EditText
    lateinit var btGetLocation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edName = findViewById(R.id.edName)
        edLocation = findViewById(R.id.edLocation)
        btSave = findViewById(R.id.btSave)
        edNameGetLocation = findViewById(R.id.edNameGetLocation)
        btGetLocation = findViewById(R.id.btGetLocation)
        btSave.setOnClickListener {
            val name = edName.text.toString()
            val location = edLocation.text.toString()
            if (name.isNotEmpty() && location.isNotEmpty()) {
                addData(name, location)
            } else {
                Toast.makeText(this, "Enter a name and location", Toast.LENGTH_SHORT).show()
            }
        }
        btGetLocation.setOnClickListener {
            val searchName = edNameGetLocation.text.toString()

            if (searchName.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
                    val detailsCall = apiInterface?.getDetails()?.awaitResponse()
                    if (detailsCall != null) {
                        if(detailsCall.isSuccessful) {

                            for (detail in detailsCall.body()!!)
                            {
                                if(detail.name?.toLowerCase().equals(searchName.toLowerCase()))
                                {
                                    withContext(Dispatchers.Main)
                                    {

                                        findViewById<TextView>(R.id.tvResults).text = detail.location

                                    }


                                }


                            }

                        }
                    }

                }
            } else {
                Toast.makeText(this@MainActivity, "Enter a name", Toast.LENGTH_SHORT).show()

            }
            edNameGetLocation.text.clear()
        }

    }
    fun addData(name: String, location: String) {

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        if (apiInterface != null) {
            apiInterface.addDetails(Details.Data(name, location))
                //apiInterface.addNames(etName.text.toString())
                .enqueue(object : Callback<Details.Data> {
                    override fun onResponse(
                        call: Call<Details.Data>,
                        response: Response<Details.Data>
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            "$name & $location are added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        edName.text.clear()
                        edLocation.text.clear()
                    }

                    override fun onFailure(call: Call<Details.Data>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "failed to add",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Failed oops", t.toString())

                        call.cancel()
                    }
                })

        }
    }

}