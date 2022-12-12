package sgolbert.example.finalproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class PlacesActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        //get data bundle from intent
        val bundle = intent.extras

        var fullAddresses = bundle!!.get("key1")
        //Addresses received
        println(fullAddresses)
    }

    fun onButtonClose(view: View){
        finish()
    }
}