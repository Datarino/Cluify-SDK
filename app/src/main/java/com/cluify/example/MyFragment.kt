package com.cluify.example

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cluify.example.databinding.FragmentBinding
import com.cluify.sdk.manager.CluifyManager
import com.cluify.sdk.model.ConsentStatus
import com.cluify.sdk.view.GdprActivity
import com.cluify.sdk.view.PrivacyPolicyActivity

class MyFragment : Fragment() {

    private lateinit var binding: FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBinding.inflate(inflater, container, false)
        setupViews()
        /*
        *  Alternatively, could be used listener CluifyManager.setGdprStatusListener (remember to remove it after finish Activity)
        *  or you can get current GDPR status by invoking CluifyManager.getGdprStatus
        */
        CluifyManager.getGdprStatusLiveData(context!!).observe(this, Observer<ConsentStatus> { status ->
            when (status) {
                ConsentStatus.ACCEPTED -> {
                    Toast.makeText(
                        context,
                        R.string.activity_main_accept_privacy_policy_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ConsentStatus.NOT_ACCEPTED -> {
                    Toast.makeText(
                        context,
                        R.string.activity_main_not_accept_privacy_policy_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> { // otherwise we should ask user for granting GDPR permission / MISSING or NEED_REVOKE
                    CluifyManager.showGdprActivity(this)
                }
            }
        })
        return binding.root
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
                Toast.makeText(context, R.string.activity_main_permissions_granted_toast, Toast.LENGTH_SHORT).show()
                requestEnablingLocationService()
            } else {
                Toast.makeText(context, R.string.activity_main_permissions_not_granted_toast, Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun requestGrantingPermissions() {
        if (!CluifyManager.isPermissionsGranted(context!!)) {
            CluifyManager.requestPermissions(this)
        } else {
            requestEnablingLocationService()
        }
    }

    private fun requestEnablingLocationService() {
        if (!CluifyManager.isLocationServiceEnabled(context!!)) {
            CluifyManager.requestEnablingLocationService(this)
        }
    }

    private fun setupViews() {
        binding.grantLocationPermissions.setOnClickListener {
            if (!CluifyManager.isPermissionsGranted(context!!)) {
                CluifyManager.requestPermissions(this)
            } else {
                Toast.makeText(context, R.string.activity_main_permissions_granted_toast, Toast.LENGTH_SHORT).show()
            }
        }

        binding.turnOnLocationService.setOnClickListener {
            if (!CluifyManager.isLocationServiceEnabled(context!!)) {
                CluifyManager.requestEnablingLocationService(this)
            } else {
                Toast.makeText(context, R.string.activity_main_location_service_enabled_toast, Toast.LENGTH_SHORT).show()
            }
        }

        binding.showPrivacyPolicy.setOnClickListener {
            val intent = Intent(context, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        binding.showGdprMessage.setOnClickListener {
            CluifyManager.showGdprActivity(this)
        }

    }
}