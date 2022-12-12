package sgolbert.example.finalproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlacesActivity: AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewManager: RecyclerView.LayoutManager
    lateinit var addressList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        //get data bundle from intent
        val bundle = intent.extras

       var fullAddresses = bundle!!.getStringArrayList("key1")
        //Addresses received
        println(fullAddresses)

        addressList = ArrayList<String>(5)
        for(x in 0..4)
        {
            addressList.add(fullAddresses!![x].toString())
        }



        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewManager = LinearLayoutManager(applicationContext)
        recyclerView.setLayoutManager(recyclerViewManager)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = RecyclerAdapter(addressList)

    }

    fun onButtonClose(view: View){
        finish()
    }
}