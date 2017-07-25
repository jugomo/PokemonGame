package com.site11.jugomo.pokemongame

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var ACCESSLOCATION = 123
    var location: Location? = null
    var list = ArrayList<Pokemon>()
    var oldLocation: Location? = null
    var playerPower = 0.0


    //
    //
    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        loadPokemon()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // request the permission
        checkPermission()
    }

    fun checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)

                return
            }
        }
        getUserLocation()
    }

    fun getUserLocation(){
        Toast.makeText(this, "User loc access on", Toast.LENGTH_SHORT).show()

        var myLocation = MyLocationListener()

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)

        var myThread = MyThread()
        myThread.start()
    }

    fun loadPokemon() {
        list.add(Pokemon(R.drawable.charmander, "charmander", "from japan", 55.3, 36.7714, -4.223580))
        list.add(Pokemon(R.drawable.bulbasaur, "bulbasaur", "living in usa", 90.5, 36.7533, -4.483580))
        list.add(Pokemon(R.drawable.squirtle, "squirtle", "living in iraq", 33.5, 36.7456, -4.623580))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            ACCESSLOCATION->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "cannot get access to the location", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        location = Location("malaka")
        location!!.latitude = 36.717343
        location!!.longitude = -4.423580

        val loc = LatLng(location!!.latitude, location!!.longitude)
        mMap.addMarker(MarkerOptions()
                .position(loc)
                .title("o_O")
                .snippet("here I am")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
    }

    inner class MyLocationListener: LocationListener {
        constructor() {
            location = Location("malaka")
            location!!.latitude = 36.717343
            location!!.longitude = -4.423580
        }

        override fun onLocationChanged(p0: Location?) {
            location = p0
            Log.e("MapsActivity", "onLocationChanged")
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            Log.e("MapsActivity", "onStatusChanged")
        }

        override fun onProviderEnabled(p0: String?) {
            Log.e("MapsActivity", "onProviderEnabled")
        }

        override fun onProviderDisabled(p0: String?) {
            Log.e("MapsActivity", "onProviderDisabled")
        }
    }

    inner class MyThread: Thread {
        constructor(): super() {
            oldLocation = Location("start")
            oldLocation!!.longitude = 0.0
            oldLocation!!.latitude = 0.0
        }

        override fun run() {
            while(true) {
                try {

                    if(oldLocation!!.distanceTo(location) == 0f) {
                        continue
                    }
                    oldLocation = location

                    runOnUiThread {
                        mMap!!.clear()

                        // SHOW ME-------------------------------------------
                        val loc = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(loc)
                                .title("o_O")
                                .snippet("here I am")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                       // mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))

                        // SHOW POKEMONS -------------------------------------
                        for(i in 0..list.size - 1) {
                            var newPokemon = list[i]
                            if(!newPokemon.isCatch!!) {
                                val locPoc = LatLng(newPokemon.lat!!, newPokemon.lon!!)
                                mMap!!.addMarker(MarkerOptions()
                                        .position(locPoc)
                                        .title(newPokemon.name!!)
                                        .snippet(newPokemon.des!! + " POWER: " + newPokemon.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))
                            }

                            if (location!!.distanceTo(newPokemon.location) < 2) {
                                newPokemon.isCatch = true
                                list[i] = newPokemon
                                playerPower += newPokemon.power!!
                                Toast.makeText(applicationContext, "You catch a new pokemon, power: " +
                                        playerPower, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Thread.sleep(1000)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

}
