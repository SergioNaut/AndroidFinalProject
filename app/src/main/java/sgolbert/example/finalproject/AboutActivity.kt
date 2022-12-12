package sgolbert.example.finalproject

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_about)
    }

    fun onButtonClick(view: View){
        when(view.id){
            R.id.back_button->{
                onBackPressed()
            }
        }
    }
}