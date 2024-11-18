package com.example.modoomap.presentation.home.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.modoomap.R
import com.example.modoomap.databinding.FragmentMapBinding
import com.example.modoomap.model.LatLngEntity
import com.example.modoomap.model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MapFragment :
    Fragment(),
    OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initMaps(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        binding.floatingActionButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navItemGirl.setOnClickListener {
            setMarkersWithOption("여성청소년")
        }

        binding.navItemKid.setOnClickListener {
            setMarkersWithOption("아동")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.navItemOld.setOnClickListener {
            setMarkersWithOption("어르신")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.navItemPregnant.setOnClickListener {
            setMarkersWithOption("임산부")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun initMaps(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        checkLocationPermission()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE,
            )
        } else {
            enableMyLocation()
        }
    }

    private fun markLocation(
        location: Location,
        color: Int,
    ): Marker? {
        val positionLatLng =
            LatLng(location.latLngEntity.latitude!!, location.latLngEntity.longitude!!)
        val markerOption =
            MarkerOptions().apply {
                position(positionLatLng)
                title(location.title)
                snippet(location.snippet)
                icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            R.drawable.marker_custom,
                            color,
                        ),
                    ),
                )
            }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        return googleMap.addMarker(markerOption)
    }

    private fun getBitmapFromVectorDrawable(
        drawableId: Int,
        color: Int,
    ): Bitmap {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)!!
        drawable.setTint(color)
        val bitmap =
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888,
            )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("현재 위치"))
            }
        }
    }

    private fun setMarkersWithOption(option: String) {
        googleMap.clear()

        var color: Int = ContextCompat.getColor(requireContext(), R.color.marker_default)

        when (option) {
            "여성청소년" -> color = ContextCompat.getColor(requireContext(), R.color.marker_girl)
            "아동" -> color = ContextCompat.getColor(requireContext(), R.color.marker_kid)
            "어르신" -> color = ContextCompat.getColor(requireContext(), R.color.marker_old)
            "임산부" ->
                color =
                    ContextCompat.getColor(requireContext(), R.color.marker_pregnant)
        }

        val pointList = mutableListOf<Location>()
        db
            .collection("locations")
            .document("categories")
            .collection(option)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // map document data to Location
                    val lat = document.data["lat"] as String
                    val lng = document.data["lng"] as String
                    val name = document.data["name"] as String
                    val snippet = document.data["subCategory"] as String
                    val location =
                        Location(
                            LatLngEntity(lat.toDouble(), lng.toDouble()),
                            name,
                            snippet,
                        )
                    pointList.add(location)
                }
                pointList.forEach {
                    markLocation(it, color)
                }
            }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
