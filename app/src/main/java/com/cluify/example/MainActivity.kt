package com.cluify.example

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.cluify.example.databinding.ActivityMainBinding
import com.cluify.sdk.manager.CluifyManager
import com.cluify.sdk.model.ConsentStatus
import com.cluify.sdk.view.GdprActivity
import com.cluify.sdk.view.PrivacyPolicyActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupViews()
        /*
        *  Alternatively, could be used listener CluifyManager.setGdprStatusListener (remember to remove it after finish Activity)
        *  or you can get current GDPR status by invoking CluifyManager.getGdprStatus
        */
        CluifyManager.getGdprStatusLiveData(this).observe(this, Observer<ConsentStatus> { status ->
            when (status) {
                ConsentStatus.ACCEPTED -> {
                    Toast.makeText(
                        this,
                        R.string.activity_main_accept_privacy_policy_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ConsentStatus.NOT_ACCEPTED -> {
                    Toast.makeText(
                        this,
                        R.string.activity_main_not_accept_privacy_policy_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> { // otherwise we should ask user for granting GDPR permission / MISSING or NEED_REVOKE
                    CluifyManager.showGdprActivity(this)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GdprActivity.GDPR_REQUEST_CODE) {
            requestGrantingPermissions()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CluifyManager.PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, R.string.activity_main_permissions_granted_toast, Toast.LENGTH_SHORT).show()
                requestEnablingLocationService()
            } else {
                Toast.makeText(this, R.string.activity_main_permissions_not_granted_toast, Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun requestGrantingPermissions() {
        if (!CluifyManager.isPermissionsGranted(this)) {
            CluifyManager.requestPermissions(this)
        } else {
            requestEnablingLocationService()
        }
    }

    private fun requestEnablingLocationService() {
        if (!CluifyManager.isLocationServiceEnabled(this)) {
            CluifyManager.requestEnablingLocationService(this)
        }
    }

    private fun setupViews() {
        binding.grantLocationPermissions.setOnClickListener {
            if (!CluifyManager.isPermissionsGranted(this)) {
                CluifyManager.requestPermissions(this)
            } else {
                Toast.makeText(this, R.string.activity_main_permissions_granted_toast, Toast.LENGTH_SHORT).show()
            }
        }

        binding.turnOnLocationService.setOnClickListener {
            if (!CluifyManager.isLocationServiceEnabled(this)) {
                CluifyManager.requestEnablingLocationService(this)
            } else {
                Toast.makeText(this, R.string.activity_main_location_service_enabled_toast, Toast.LENGTH_SHORT).show()
            }
        }

        binding.showPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        binding.showGdprMessage.setOnClickListener {
            CluifyManager.showGdprActivity(this)
        }

        binding.showFragmentView.setOnClickListener {
            startActivity(Intent(this, FragmentActivity::class.java))
        }
    }
}
