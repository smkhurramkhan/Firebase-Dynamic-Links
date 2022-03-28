package com.example.firebasedynamiclinks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.appinvite.FirebaseAppInvite
import com.google.firebase.dynamiclinks.DynamicLink.*
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink


class MainActivity : AppCompatActivity() {
    var dynamicLinkUri: Uri ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        createDynamicLink_Advanced()
        createDynamicLink_Basic()
        createShortLink()

        val btnShare= findViewById<Button>(R.id.btnShare)
        btnShare.setOnClickListener {
            dynamicLinkUri?.let { it1 -> shareLink(it1) }
        }
    }

    fun createDynamicLink_Basic() {
        // [START create_link_basic]
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.example.com/"))
            .setDomainUriPrefix("https://example.page.link") // Open links with this app on Android
            .setAndroidParameters(
                AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()
         dynamicLinkUri = dynamicLink.uri
        // [END create_link_basic]
    }

    fun createDynamicLink_Advanced() {
        // [START create_link_advanced]
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.example.com/"))
            .setDomainUriPrefix("https://example.page.link")
            .setAndroidParameters(
                AndroidParameters.Builder("com.example.android")
                    .setMinimumVersion(125)
                    .build()
            )
            .setIosParameters(
                IosParameters.Builder("com.example.ios")
                    .setAppStoreId("123456789")
                    .setMinimumVersion("1.0.1")
                    .build()
            )
            .setGoogleAnalyticsParameters(
                GoogleAnalyticsParameters.Builder()
                    .setSource("orkut")
                    .setMedium("social")
                    .setCampaign("example-promo")
                    .build()
            )
            .setItunesConnectAnalyticsParameters(
                ItunesConnectAnalyticsParameters.Builder()
                    .setProviderToken("123456")
                    .setCampaignToken("example-promo")
                    .build()
            )
            .setSocialMetaTagParameters(
                SocialMetaTagParameters.Builder()
                    .setTitle("Example of a Dynamic Link")
                    .setDescription("This link works whether the app is installed or not!")
                    .build()
            )
            .buildDynamicLink() // Or buildShortDynamicLink()
        // [END create_link_advanced]
    }

    fun createShortLink() {
        // [START create_short_link]
        val shortLinkTask: Task<ShortDynamicLink> =
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/"))
                .setDomainUriPrefix("https://example.page.link") // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink: Uri = task.result.shortLink!!
                        val flowchartLink: Uri = task.result.previewLink!!
                    } else {
                        // Error
                        // ...
                    }
                }
        // [END create_short_link]
    }

    fun shortenLongLink() {
        // [START shorten_long_link]
        val shortLinkTask: Task<ShortDynamicLink> =
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse("https://example.page.link/?link=https://www.example.com/&apn=com.example.android&ibn=com.example.ios"))
                .buildShortDynamicLink()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink: Uri = task.result.shortLink!!
                        val flowchartLink: Uri = task.result.previewLink!!
                    } else {
                        // Error
                        // ...
                    }
                }
        // [END shorten_long_link]
    }

    fun buildShortSuffix() {
        // [START ddl_short_suffix]
        val shortLinkTask: Task<ShortDynamicLink> =
            FirebaseDynamicLinks.getInstance().createDynamicLink() // ...
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
        // ...
        // [END ddl_short_suffix]
    }

    fun shareLink(myDynamicLink: Uri) {
        // [START ddl_share_link]
        val sendIntent = Intent()
        val msg = "Hey, check this out: $myDynamicLink"
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
        // [END ddl_share_link]
    }

    fun getInvitation() {
        // [START ddl_get_invitation]
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Handle error
                    // ...
                }
                val invite: FirebaseAppInvite = FirebaseAppInvite.getInvitation(task.result)
                if (invite != null) {
                    // Handle invite
                    // ...
                }
            }
        // [END ddl_get_invitation]
    }

    fun onboardingShare(dl: ShortDynamicLink) {
        // [START ddl_onboarding_share]
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Try this amazing app: " + dl.shortLink)
        startActivity(Intent.createChooser(intent, "Share using"))
        // [END ddl_onboarding_share]
    }
}