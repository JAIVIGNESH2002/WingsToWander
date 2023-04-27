package com.example.wanderwings

import android.R.id
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.location.LocationListener
import android.opengl.Visibility
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private val PICK_CONTACT_REQUEST_CODE = 1
    private lateinit var pickContactLauncher: ActivityResultLauncher<Void?>
    private lateinit var  contactPickerContract : ActivityResultLauncher<Void?>

    private lateinit var location:MyLocationListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter

    private lateinit var lotAnime : LinearLayoutCompat

    private val CON_NAME: Int = 0
    private val CON_ID: Int = 1


    fun getContactNumber(context: Context, contactId: Long): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
        var phoneNumber: String? = null
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            phoneNumber = cursor.getString(columnIndex)
            cursor.close()
        }
        return phoneNumber
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.BODY_SENSORS),
                101)
        }

//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//
//            // Permission is not granted, request it
//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
//                101)
//        }

        //start shake detector service
        val intent = Intent(this, ShakeDetectorService::class.java)
        startService(intent)


        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.app_bar_gradient))
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setTitle("\uD83D\uDEE1️Wander Wings")

        //Recycler view
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lotAnime = findViewById<LinearLayoutCompat>(R.id.radarLinear)
        lotAnime.visibility = View.VISIBLE


        val items = ArrayList<String>()
        adapter = RecyclerViewAdapter(items)
        recyclerView.adapter = adapter


        //Checking the location service
        location = MyLocationListener(this)
        location.requestLocationUpdates()

        // Check if the app has permission to access the contacts
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
               101)
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                101)
        }


        contactPickerContract = registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            if (contactUri != null) {
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID,ContactsContract.Contacts.LOOKUP_KEY)
                val cursor = contentResolver.query(
                    contactUri,
                    projection,
                    null,
                    null,
                    null
                )

                if (cursor != null && cursor.moveToFirst()) {
//                    val nameIndex = cursor.getColumnIndex("0")
//                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    val name = cursor.getString(0)
                    val contactId = cursor.getLong(1)
                    val mContactKey = cursor.getString(2)
                    val contactUri = ContactsContract.Contacts.getLookupUri(contactId, mContactKey)
//                    val number = cursor.getString(numberIndex)

                    items.add(name)
                    lotAnime.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }

                cursor?.close()
            }
        }


        val addPeer = findViewById<FloatingActionButton>(R.id.addPeer)
        addPeer.setOnClickListener {
                contactPickerContract.launch(null)
                location.stopRequestingLocationUpdates()
        }

        //Notification setup
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "fake_call_channel",
                "Fake Call Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Fake incoming call notifications"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    findViewById<FloatingActionButton>(R.id.addPeer).visibility = View.VISIBLE
                    val fragmentManager = supportFragmentManager
                    for (fragment in fragmentManager.fragments) {
                        fragmentManager.beginTransaction().remove(fragment).commit()
                    }

                    true
                }
                R.id.placeHolder -> {
                    // Create a new instance of MyFragment
                    val fragment = SosFragment()
                    // Add the fragment to the fragment_container
                    addPeer.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .add(R.id.container, fragment).commit()
                    true
                }
                R.id.profile -> {
                    // Create a new instance of MyFragment
                    addPeer.visibility = View.GONE
                    val fragment = EmergencyContacts()
                    // Add the fragment to the fragment_container
                    supportFragmentManager.beginTransaction()
                        .add(R.id.container, fragment).commit()
                    true
                }
                else -> false
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val settingsItem = menu.findItem(R.id.settingMenu)
        settingsItem.setOnMenuItemClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        val aboutItem = menu.findItem(R.id.settingAbout)
        aboutItem.setOnMenuItemClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            true
        }
        return true

    }

    fun animateFloatingActionButton(fab: FloatingActionButton, show: Boolean) {
        val translationY = if (show) 0f else fab.height.toFloat() + 100f // adjust 100f to desired offset
        val animator = ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, translationY)
        animator.interpolator = FastOutSlowInInterpolator()
        animator.duration = 5000 // adjust duration as desired
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!show) fab.visibility = View.GONE
            }
        })
        if (show) fab.visibility = View.VISIBLE
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, ShakeDetectorService::class.java)
        stopService(intent)

    }

}