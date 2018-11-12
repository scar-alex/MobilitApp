package com.mobi.mobilitapp.CrashDetection.Acitivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mobi.mobilitapp.CrashDetection.DifferentTypes.PreferencesTypes;
import com.mobi.mobilitapp.CrashDetection.HandlersAndServices.AccelerometerServiceListener;
import com.mobi.mobilitapp.R;

public class SettingsCrashDetectionActivity extends Activity {

    private static final String MyTAG = "MyTAG2";

    public static final int PICK_CONTACT_REQUEST = 1;
    private Uri contactUri;

    Switch switchOnOffCrashDetection;
    Switch switchOnOffSmsEmergency;
    LinearLayout LayoutSettingsCrashDetectionOn;
    LinearLayout LayoutSettingsSmsEmergencyOn;
    RadioButton rblevel1, rblevel2, rblevel3;
    RadioGroup rglevels;

    TextView smsEmergencyName;
    TextView smsEmergencyNumber;
    Button saveContactButton;

    SharedPreferences prefs;
    String switchOnOff;
    int maxLengthSms;
    private final String MAXIMUMACCELTHRESHOLDLEVEL1 = "16";
    //private final String MAXIMUMACCELTHRESHOLDLEVEL1 = "5"; //TESTING
    private final String MAXIMUMACCELTHRESHOLDLEVEL2 = "23";
    //private final String MAXIMUMACCELTHRESHOLDLEVEL2 = "10"; //Testing
    private final String MAXIMUMACCELTHRESHOLDLEVEL3 = "30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_crash_detection);

        switchOnOffCrashDetection = (Switch) findViewById(R.id.switchCrashDetection);
        switchOnOffSmsEmergency = (Switch) findViewById(R.id.switchEmergencySms);

        LayoutSettingsCrashDetectionOn = (LinearLayout) findViewById(R.id.LayoutSettingsCrashDetectionOn);
        LayoutSettingsSmsEmergencyOn = (LinearLayout) findViewById(R.id.LayoutSettingsEmergencySms);

        //smsEmergencyNumber = (EditText) findViewById(R.id.editTextEmergencySmsPhone);
        smsEmergencyNumber = (TextView) findViewById(R.id.contactPhone);
        smsEmergencyName = (TextView)findViewById(R.id.contactName);
        saveContactButton = (Button) findViewById(R.id.selectionButton);

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        maxLengthSms = 150 - getString(R.string.possibleAlertMessageSMS).length() - 39;

        loadPreferences();

        //configureRadioButtonsCrashOptions(); Este metodo es para cargar la configuracion dle usuario con los radiobuttons


        switchOnOffCrashDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LayoutSettingsCrashDetectionOn.setVisibility(View.VISIBLE);
                    prefs.edit().putString(PreferencesTypes.switchCrashOnOff.toString(), PreferencesTypes.on.toString()).commit();
                    stopCrashDetectionService();
                    startCrashDetectionService();
                }
                else{
                    LayoutSettingsCrashDetectionOn.setVisibility(View.INVISIBLE);
                    prefs.edit().putString(PreferencesTypes.switchCrashOnOff.toString(), PreferencesTypes.off.toString()).commit();
                    //prefs.edit().putString(PreferencesTypes.switchSmsOnOff.toString(), PreferencesTypes.off.toString()).commit();
                    switchOnOffSmsEmergency.setChecked(false);
                    stopCrashDetectionService();
                }
            }
        });

        switchOnOffSmsEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LayoutSettingsSmsEmergencyOn.setVisibility(View.VISIBLE);
                    prefs.edit().putString(PreferencesTypes.switchSmsOnOff.toString(), PreferencesTypes.on.toString()).commit();
                    if(!prefs.getString(PreferencesTypes.emergencyPhoneNumberSms.toString().toString(),"")
                            .equalsIgnoreCase("")){
                        smsEmergencyNumber.setText(prefs.getString(PreferencesTypes.emergencyPhoneNumberSms.toString(),""));
                        smsEmergencyName.setText(prefs.getString(PreferencesTypes.emergencyPhoneNameSms.toString(),""));
                    }
                    else{
                        smsEmergencyName.setText("");
                        smsEmergencyNumber.setText(getString(R.string.no_phone_saved_remember));
                    }
                }
                else{
                    LayoutSettingsSmsEmergencyOn.setVisibility(View.INVISIBLE);
                    prefs.edit().putString(PreferencesTypes.switchSmsOnOff.toString(), PreferencesTypes.off.toString()).commit();
                }
            }
        });


    }

    private void configureRadioButtonsCrashOptions() {
        /*
        rglevels = (RadioGroup) findViewById(R.id.radioGroupCrashDetecctionLevels);
        rblevel1 = (RadioButton) findViewById(R.id.radioButtonCrashDetecctionLevel1);
        rblevel2 = (RadioButton) findViewById(R.id.radioButtonCrashDetecctionLevel2);
        rblevel3 = (RadioButton) findViewById(R.id.radioButtonCrashDetecctionLevel3);
        */

        String crashDetectionLevel = prefs.getString(PreferencesTypes.CrashDetectionLevel.toString(),
                PreferencesTypes.CDLevel2.toString());

        if(crashDetectionLevel.equalsIgnoreCase(PreferencesTypes.CDLevel1.toString())){
            rblevel1.setChecked(true);
            Log.v(MyTAG, "1");
        }

        else if(crashDetectionLevel.equalsIgnoreCase(PreferencesTypes.CDLevel2.toString())){
            rblevel2.setChecked(true);
            Log.v(MyTAG, "2");
        }

        else if(crashDetectionLevel.equalsIgnoreCase(PreferencesTypes.CDLevel3.toString())){
            rblevel3.setChecked(true);
            Log.v(MyTAG, "3");
        }

        else{
            rblevel2.setChecked(true);
            Log.v(MyTAG, "default");
            prefs.edit().putString(PreferencesTypes.CrashDetectionLevel.toString(),
                    PreferencesTypes.CDLevel2.toString()).commit();
            prefs.edit().putString(PreferencesTypes.sensitivityCrashMaximumThreshold.toString(),
                    MAXIMUMACCELTHRESHOLDLEVEL2).commit();
            stopCrashDetectionService();
            startCrashDetectionService();
        }

        /*

        rglevels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonCrashDetecctionLevel1){
                    prefs.edit().putString(PreferencesTypes.CrashDetectionLevel.toString(),
                            PreferencesTypes.CDLevel1.toString()).commit();
                    prefs.edit().putString(PreferencesTypes.sensitivityCrashMaximumThreshold.toString(),
                            MAXIMUMACCELTHRESHOLDLEVEL1).commit();
                    stopCrashDetectionService();
                    startCrashDetectionService();
                    Log.v(MyTAG, "Level 1");
                }else if (checkedId == R.id.radioButtonCrashDetecctionLevel2){
                    Log.v(MyTAG, "Level 2");
                    prefs.edit().putString(PreferencesTypes.CrashDetectionLevel.toString(),
                            PreferencesTypes.CDLevel2.toString()).commit();
                    prefs.edit().putString(PreferencesTypes.sensitivityCrashMaximumThreshold.toString(),
                            MAXIMUMACCELTHRESHOLDLEVEL2).commit();
                    stopCrashDetectionService();
                    startCrashDetectionService();
                }else if (checkedId == R.id.radioButtonCrashDetecctionLevel3){
                    Log.v(MyTAG, "Level 3");
                    prefs.edit().putString(PreferencesTypes.CrashDetectionLevel.toString(),
                            PreferencesTypes.CDLevel3.toString()).commit();
                    prefs.edit().putString(PreferencesTypes.sensitivityCrashMaximumThreshold.toString(),
                            MAXIMUMACCELTHRESHOLDLEVEL3).commit();
                    stopCrashDetectionService();
                    startCrashDetectionService();
                }
            }
        });

        */

    }

    private void configureSwitchSmsOptions() {
        if(prefs.getString(PreferencesTypes.switchSmsOnOff.toString(),
                PreferencesTypes.no_selection.toString()).
                equalsIgnoreCase(PreferencesTypes.on.toString())){
            LayoutSettingsSmsEmergencyOn.setVisibility(View.VISIBLE);
            switchOnOffSmsEmergency.setChecked(true);

            if(!prefs.getString(PreferencesTypes.emergencyPhoneNumberSms.toString().toString(),"")
                    .equalsIgnoreCase("")){
                smsEmergencyNumber.setText(prefs.getString(PreferencesTypes.emergencyPhoneNumberSms.toString(),""));
                smsEmergencyName.setText(prefs.getString(PreferencesTypes.emergencyPhoneNameSms.toString(),""));
            }
        }

        else{
            LayoutSettingsSmsEmergencyOn.setVisibility(View.INVISIBLE);
            switchOnOffSmsEmergency.setChecked(false);
            prefs.edit().putString(PreferencesTypes.switchSmsOnOff.toString(), PreferencesTypes.off.toString()).commit();
        }
    }

    private void configureSwitchCrashOptions() {
        switchOnOff = prefs.getString(PreferencesTypes.switchCrashOnOff.toString(), PreferencesTypes.no_selection.toString());

        if(switchOnOff.equalsIgnoreCase(PreferencesTypes.on.toString())){
            LayoutSettingsCrashDetectionOn.setVisibility(View.VISIBLE);
            switchOnOffCrashDetection.setChecked(true);
        }
        else{
            LayoutSettingsCrashDetectionOn.setVisibility(View.INVISIBLE);
            switchOnOffCrashDetection.setChecked(false);
            prefs.edit().putString(PreferencesTypes.switchSmsOnOff.toString(), PreferencesTypes.off.toString()).commit();
        }
    }

    private void stopCrashDetectionService()
    {
        stopService(new Intent(this, AccelerometerServiceListener.class));
    }

    private void startCrashDetectionService()
    {
        startService(new Intent(this, AccelerometerServiceListener.class));
    }

    /*
    public void onSaveSmSEmergencyNumberClick(View view) {
        try{
            String phoneEmergencyNumber = smsEmergencyNumber.getText().toString();
            //phoneEmergencyNumber = phoneEmergencyNumber.replaceAll(" ","");
            //phoneEmergencyNumber = phoneEmergencyNumber.replaceAll("\\+34","034");
            if(!phoneEmergencyNumber.isEmpty() && phoneEmergencyNumber != null){
                prefs.edit().putString(PreferencesTypes.emergencyPhoneNumberSms.toString(),phoneEmergencyNumber).commit();
                Toast.makeText(this,getString(R.string.numberSaved), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,getString(R.string.enterAPhoneNumber), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this,getString(R.string.failedToSaveTheNumber), Toast.LENGTH_SHORT).show();
        }
    }*/


    public void saveSmsEmergencyNumber(){
        try{
            String phoneEmergencyNumber = smsEmergencyNumber.getText().toString();
            String phoneEmergencyName = smsEmergencyName.getText().toString();
            //phoneEmergencyNumber = phoneEmergencyNumber.replaceAll(" ","");
            //phoneEmergencyNumber = phoneEmergencyNumber.replaceAll("\\+34","034");

            if(!phoneEmergencyNumber.isEmpty() && phoneEmergencyNumber != null){
                prefs.edit().putString(PreferencesTypes.emergencyPhoneNumberSms.toString(),phoneEmergencyNumber).commit();
                prefs.edit().putString(PreferencesTypes.emergencyPhoneNameSms.toString(),phoneEmergencyName).commit();
                Toast.makeText(this,getString(R.string.numberSaved), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,getString(R.string.enterAPhoneNumber), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this,getString(R.string.failedToSaveTheNumber), Toast.LENGTH_SHORT).show();
        }
    }



    /* On Permission Response */
    /* Check granted permissions - Mahmoud Elbayoumy 11/4/2017*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 2:  //Check for permissions grant
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("BAYO", "Contacts Permissions Granted");

                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(i, PICK_CONTACT_REQUEST);

                }
                else
                {
                    Log.d("BAYO", "User rejected to give permission for contacts");
                }
                break;
        }
    }


    public void initPickContacts(View v){

        //Request Permission to use contacts
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 2);
            }else{
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT_REQUEST);
            }
        }else{
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(i, PICK_CONTACT_REQUEST);
        }

    }

    private void renderContact(Uri uri) {

        TextView contactName = (TextView)findViewById(R.id.contactName);
        TextView contactPhone = (TextView)findViewById(R.id.contactPhone);

        contactName.setText(getName(uri));
        contactPhone.setText(getPhone(uri));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {

                contactUri = intent.getData();

                renderContact(contactUri);
                saveSmsEmergencyNumber();
            }
        }
    }

    private String getPhone(Uri uri) {

        String id = null;
        String phone = null;

        Cursor contactCursor = getContentResolver().query(
                uri,
                new String[]{ContactsContract.Contacts._ID},
                null,
                null,
                null);


        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0);
        }
        contactCursor.close();

        String selectionArgs =
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE+"= " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

        Cursor phoneCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                selectionArgs,
                new String[] { id },
                null
        );
        if (phoneCursor.moveToFirst()) {
            phone = phoneCursor.getString(0);
        }
        phoneCursor.close();

        return phone;
    }

    private String getName(Uri uri) {

        String name = null;

        ContentResolver contentResolver = getContentResolver();

        Cursor c = contentResolver.query(
                uri,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                null,
                null,
                null);

        if(c.moveToFirst()){
            name = c.getString(0);
        }

        c.close();

        return name;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(prefs.getString(PreferencesTypes.switchSmsOnOff.toString(),PreferencesTypes.no_selection.toString())
                    .equalsIgnoreCase(PreferencesTypes.on.toString())) {
                if (smsEmergencyNumber.getText().length() < 1 || smsEmergencyNumber.getText() == null ||
                        prefs.getString(PreferencesTypes.emergencyPhoneNumberSms.toString(), "")
                                .equalsIgnoreCase("")) {

                    dialogSwitchSmsOnAndNoPhoneAsking();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void dialogSwitchSmsOnAndNoPhoneAsking(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.r_u_sure_to_go_back))
                .setTitle(getString(R.string.no_phone_saved))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()  {
                    public void onClick(DialogInterface dialog, int id) {
                        switchOnOffSmsEmergency.setChecked(false);
                        LayoutSettingsSmsEmergencyOn.setVisibility(View.INVISIBLE);
                        prefs.edit().putString(PreferencesTypes.switchSmsOnOff.toString(), PreferencesTypes.off.toString()).commit();
                        prefs.edit().putString(PreferencesTypes.emergencyPhoneNumberSms.toString(), "").commit();
                        prefs.edit().putString(PreferencesTypes.emergencyPhoneNameSms.toString(), "").commit();
                        backButton();
                        Log.i("MyTAG", "Confirmacion Aceptada.");
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveContactButton.callOnClick();
                        Log.i("MyTAG", "Confirmacion Cancelada.");
                        dialog.cancel();
                    }
                }).show();
    }

    public void dialogAskMedicalInfo(){
        Context context = this;
        String medicalInfo = prefs.getString(PreferencesTypes.medical_info.toString(),"");
        final EditText txtMedicalInfo = new EditText(this);

        if(!medicalInfo.isEmpty() || !medicalInfo.equalsIgnoreCase("")){
            txtMedicalInfo.setText(medicalInfo);
        }

        txtMedicalInfo.setHint(getString(R.string.hint_medical_info));
        txtMedicalInfo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthSms)});

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.medical_info))
                .setMessage(getString(R.string.medical_info_message) +" "+ maxLengthSms +" "+ getString(R.string.medical_info_characters))
                .setView(txtMedicalInfo)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String medicalInfo = txtMedicalInfo.getText().toString();
                        if (!medicalInfo.isEmpty() || !medicalInfo.equalsIgnoreCase("")) {
                            prefs.edit().putString(PreferencesTypes.medical_info.toString(), medicalInfo).commit();
                            Toast.makeText(context, getString(R.string.medical_info_saved), Toast.LENGTH_SHORT).show();
                        } else {
                            prefs.edit().putString(PreferencesTypes.medical_info.toString(), medicalInfo).commit();
                            Toast.makeText(context, getString(R.string.medical_info_deleted), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void addMedicalInfo(View v){
        dialogAskMedicalInfo();
    }


    public void backButton(){
        super.onBackPressed();
    }

    private void loadPreferences(){
        configureSwitchCrashOptions();
        configureSwitchSmsOptions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }
}
