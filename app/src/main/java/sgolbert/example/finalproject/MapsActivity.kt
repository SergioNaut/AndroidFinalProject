package sgolbert.example.finalproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import sgolbert.example.finalproject.databinding.ActivityMapsBinding
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    //Current location
    private lateinit var currLocation: LatLng

    private val REQUEST_LOCATION_PERMISSION = 1

    // Used for selecting the Current Places
    private var mPlacesClient: PlacesClient? = null
    private val M_MAX_ENTRIES = 5
    private lateinit var mLikelyPlaceNames: ArrayList<String>
    private lateinit var mLikelyPlaceAddresses: ArrayList<String>
    private lateinit var mLikelyPlaceLatLngs: ArrayList<LatLng>

    //Array to pass location strings
    private lateinit var  fullAddresses: ArrayList<String>

    private var valuesLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Get current location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Initialize Places
        val apiKey = getString(R.string.api_key)
        Places.initialize(applicationContext,apiKey)
        mPlacesClient = Places.createClient(this)
        mLikelyPlaceNames = ArrayList<String>(5)
        mLikelyPlaceAddresses = ArrayList<String>(5)
        mLikelyPlaceLatLngs = ArrayList<LatLng>(5)

        fullAddresses = ArrayList<String>(5)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Enable my current location
        enableMyLocation()

        //Shows zoom controls
        mMap.uiSettings.setZoomControlsEnabled(true)

        //Current Places
        getCurrentPlaceLikelihoods()

        //add marker to current location
        getCurrentLocation()


    }

    private fun getAddress(loc:LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(loc!!.latitude, loc!!.longitude, 1)
        } catch (e1: IOException) {
            Log.e("Geocoding", getString(R.string.problem), e1)
        } catch (e2: IllegalArgumentException) {
            Log.e("Geocoding", getString(R.string.invalid)+
                    "Latitude = " + loc!!.latitude +
                    ", Longitude = " +
                    loc!!.longitude, e2)
        }
        // If the reverse geocode returned an address
        Log.i("Snippet Test Addresses","${addresses} ")
        if (addresses != null) {
            // Get the first address
            val address = addresses[0]
            val addressText = String.format(
                "%s",
                address.getAddressLine(0))
            Log.i("Snippet Test","Snippet Test")
            return addressText
        }
        else
        {
            Log.e("Geocoding", getString(R.string.noaddress))
            return ""
        }
    }

    // Location services permission
    // this will result in a blue dot being your current location
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(
            this
        ) { location: Location? ->
            if (location != null) {
                currLocation = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions()
                    .position(currLocation)
                    .title("Sergio Current Location")
                    .snippet(getAddress(currLocation))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15f))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        val placeFields = Arrays.asList(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        val placeResponse: Task<FindCurrentPlaceResponse> =
            mPlacesClient!!.findCurrentPlace(request)
        placeResponse.addOnCompleteListener(this,
            OnCompleteListener<FindCurrentPlaceResponse?> { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    // Set the count, handling cases where less than 5 entries are returned.
                    val count: Int
                    if (response.placeLikelihoods.size < M_MAX_ENTRIES) {
                        count = response.placeLikelihoods.size
                    } else {
                        count = M_MAX_ENTRIES
                    }
                    println("Found a place")
                    var i = 0
                    for (placeLikelihood: PlaceLikelihood in response.placeLikelihoods) {
                        val currPlace = placeLikelihood.place
                        mLikelyPlaceNames.add(currPlace.name)
                        Log.i("Places",currPlace.name)
                        mLikelyPlaceAddresses.add(currPlace.address)
                        mLikelyPlaceLatLngs.add(currPlace.latLng)

                        //Add full address to array
                        fullAddresses.add(String.format(currPlace.name + ", "+ currPlace.address))
                        val currLatLng =
                            if (mLikelyPlaceLatLngs[i] == null) "" else mLikelyPlaceLatLngs[i].toString()
                        Log.i(
                            "Places", String.format(
                                "Place " + currPlace.name
                                        + " has likelihood: " + placeLikelihood.likelihood
                                        + " at " + currLatLng
                            )
                        )
                        i++
                        if (i > (count - 1)) {
                            break
                        }
                    }

                    // Populate and refresh the RecyclerView
                    println("Aaaaaaaaaa")
                    println(mLikelyPlaceNames)
                    println("Aaaaaaaaaa")
                    println(mLikelyPlaceAddresses)
                    println("Aaaaaaaaaa")
                    println(mLikelyPlaceLatLngs)
                    println("BBBBBBBB")
                    println(fullAddresses)

                    valuesLoaded = true


                } else {
                    val exception: Exception? = task.getException()
                    if (exception is ApiException) {
                        Log.e("Places", "Place not found: " + exception.statusCode)
                    }
                }
            }
        )
    }

    //Create a Menu Button
    override fun onCreateOptionsMenu(menu: Menu?):Boolean{
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        //Change page based on user's selection
        R.id.maps_button ->{
            //Go back to map screen
            true
        }
        R.id.places_button ->{
            //Only change screens if place have been loaded
            if(valuesLoaded){
                //Go to recycler page
                val intent = Intent(this,PlacesActivity::class.java).apply{
                    putExtra("key1",fullAddresses)
                }
                startActivity(intent)
            }

            true
        }
        R.id.email_button ->{
            //Go to email page
            true
        }
        R.id.about_button ->{
            //Go to about page
            val intent = Intent(this,AboutActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}