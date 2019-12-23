package com.android.dynamiclink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    TextInputEditText editTextDomain,editTextPrefix;
    AppCompatButton btnCreateDeepLink;
    AppCompatTextView  showLongLink,showShortLink,createShortLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextDomain = findViewById(R.id.etWriteDomain);
        editTextPrefix = findViewById(R.id.et_prefix);
        btnCreateDeepLink = findViewById(R.id.createLink);
        showLongLink = findViewById(R.id.show_long_link);
        showShortLink = findViewById(R.id.show_short_link);
        createShortLink = findViewById(R.id.create_short_link);
    }

    public void createDeeplink(View view) {
        generateDeepLinkWhatsapp(getApplicationContext(),"https://"+editTextDomain.getText()+"/"+editTextPrefix.getText().toString(),"Enter Title","Enter Description","");
    }

    public void generateDeepLinkWhatsapp(Context context, String url, String title, String desc, String image){

        String dynamicLinkUri = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDynamicLinkDomain("demockv.page.link")
                .setLink(Uri.parse(url))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build()) //com.melardev.tutorialsfirebase
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(title)
                        .setDescription(desc)
                        .setImageUrl(Uri.parse("https://www.foop.com/assets/img/home_page_banner.png"))
                        .build())
                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder()
                        .setSource("AndroidApp")
                        .build())
                .buildDynamicLink().getUri().toString();

        Log.d(TAG,"DynamicLinkUrl "+dynamicLinkUri);
        showLongLink.setText(dynamicLinkUri);
        createShortLink.setVisibility(View.VISIBLE);
        createShortLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareShortDynamicLink(showLongLink.getText().toString());
            }
        });



    }

    public void shareShortDynamicLink(String buildDynamicLink) {
        Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDynamicLink))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink(); //flowchart link is a debugging URL

                            showShortLink.setText(shortLink.toString());
                            Log.d(TAG,"ShortLink Url "+ shortLink.toString());
                            Log.d("TAG", flowchartLink.toString());
                            Intent intent = new Intent();
                            String msg = "visit my awesome website: " + shortLink.toString();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                            intent.setType("text/plain");
                            startActivity(intent);

                        } else {
                            Log.w("Error", "getDynamicLink:onFailure", task.getException());
                        }
                    }
                });
    }

}
