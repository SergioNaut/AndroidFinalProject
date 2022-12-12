package sgolbert.example.finalproject

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class EmailActivity: AppCompatActivity() {
    lateinit var emailEditText: EditText
    lateinit var myAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        //get data bundle from intent
        val bundle = intent.extras

        //Get saved email
        val prefsEditor = getSharedPreferences("mySettings",Context.MODE_PRIVATE)

        emailEditText = findViewById(R.id.editTextEmail)

        emailEditText.setText(prefsEditor.getString("email",""))

        myAddress = bundle?.getString("myAddress")!!

        checkPhonePermissions()
    }

    fun onButtonClose(view: View){
        finish()
    }

    fun onButtonSend(view: View){
        //Send email
        if(emailEditText.text.isNotEmpty()){
            val email = emailEditText.text.toString()

            var emailArray = arrayOf("")
            emailArray[0] = email

            val prefsEditor = getSharedPreferences("mySettings",Context.MODE_PRIVATE).edit()
            prefsEditor.putString("email", email)
            prefsEditor.apply()

            composeEmail(emailArray,"My Current Address", myAddress)
        }
    }

    fun composeEmail(address: Array<String>, subject: String, body: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }
    }

    private fun checkPhonePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("This app needs phone call permissions")
                builder.setMessage("Please grant phone call permissions so this app can work normally.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    requestPermissions(
                        arrayOf(Manifest.permission.CALL_PHONE),
                        101)
                }
                builder.show()
            }
        }
    }
}