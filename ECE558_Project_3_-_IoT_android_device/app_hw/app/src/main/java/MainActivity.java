/** Ryan Bentz and Ram Bhattarai
 *  ECE 558
 *  Project 3
 *
 *  HW Control app to run on the raspberry pi.
 */

package ryanb_ramb.project3.ece558.hardwaredev;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {

    // private static members
    private static final String TAG = "MainActivity ";

    private static final int POLL_INTERVAL_MS = 100;
    private static final int LED_TOGGLE_INTERVAL_MS = 500;

    private static final byte PWM3_ADDR = 0x00;
    private static final byte PWM4_ADDR = 0x01;
    private static final byte PWM5_ADDR = 0x02;
    private static final byte PWM6_ADDR = 0x03;
    private static final byte DAC1_ADDR = 0x04;
    private static final byte ADA5_LSB_ADDR = 0x05;
    private static final byte ADA5_MSB_ADDR = 0x06;
    private static final byte ADC3_LSB_ADDR = 0x07;
    private static final byte ADC3_MSB_ADDR = 0x08;
    private static final byte ADC4_LSB_ADDR = 0x09;
    private static final byte ADC4_MSB_ADDR = 0x0A;
    private static final byte ADC5_LSB_ADDR = 0x0B;
    private static final byte ADC5_MSB_ADDR = 0x0C;

    // Thread members
    private byte mLedToggleCount;
    private Handler mLifeHandler;
    private Handler mPollHandler;
    private Handler mToggleHandler;

    // Peripheral members
    private String I2C_BUS = "I2C1";
    private int I2C_ADDRESS = 0x08;
    private String GPIO_PIN14 = "BCM14";
    private PeripheralManager manager;
    I2cManager i2cManager;
    GpioManager mPin14;

    //Declare the Firebase database refrences
    private DatabaseReference database;
    //private String timestamp=null;

    //ADA and ADC
    private int ADA5_VAL = 0;
    private int ADC3_VAL = 0;
    private int ADC4_VAL = 0;
    private int ADC5_VAL = 0;

    //mDacVal and PWM
    private int mRedVal = 0;
    private int mBlueVal = 0;
    private int mGreenVal = 0;
    private int mMotorVal = 0;
    private int mDacVal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize peripheral manager and resources
        manager = PeripheralManager.getInstance();
        i2cManager = new I2cManager(manager, I2C_BUS, I2C_ADDRESS);
        mPin14 = new GpioManager(manager, GPIO_PIN14);

        // set polling handler for getting data from PIC
        mPollHandler = new Handler();
        mPollHandler.post(mPollPicRunnable);

        // connect to the database
        //FirebaseApp.initializeApp(this);       // due to problems with starting the firebase instance
        database = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG,"Database: " + database.toString());

        // do a one-time read of the database to get the most up to date values
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRedVal = dataSnapshot.child("PWM3").getValue(int.class);
                mGreenVal = dataSnapshot.child("PWM4").getValue(int.class);
                mBlueVal = dataSnapshot.child("PWM5").getValue(int.class);
                mMotorVal = dataSnapshot.child("PWM6").getValue(int.class);
                mDacVal = dataSnapshot.child("DAC1OUT").getValue(int.class);

                Log.d("TAG", "Write to microcontroller.");

                writeToMicroController();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set up persistent listening for value changes
        Log.d(TAG, "Setup event listener.");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRedVal = dataSnapshot.child("PWM3").getValue(int.class);
                mGreenVal = dataSnapshot.child("PWM4").getValue(int.class);
                mBlueVal = dataSnapshot.child("PWM5").getValue(int.class);
                mMotorVal = dataSnapshot.child("PWM6").getValue(int.class);
                mDacVal = dataSnapshot.child("DAC1OUT").getValue(int.class);

                writeToMicroController();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /** Make sure we close the hardware resources when closing the app
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free I2C bus
        i2cManager.closeDevice();
        // free GPIO resource
        mPin14.close();
    }


    /** Runnable that handles the periodic polling of the microcontroller
     *
     */
    private Runnable mPollPicRunnable = new Runnable() {
        @Override
        public void run() {

            // read from the microcontroller. If there was a change, setDatabaseValues the database
            // otherwise toggle pin due to I2C error
            if (readFromMicroController()) {
                updateDatabase();
                setMotorDutyCycle(ADA5_VAL);
            }
            else
                toggleGPIO();

            // set runnable to run again after interval expires
            mPollHandler.postDelayed(mPollPicRunnable, POLL_INTERVAL_MS);
        }
    };


    /** Read from the microcontroller on the interval and check if any values changed.
     *  If anything changed, setDatabaseValues the value and return true to setDatabaseValues the database
     * @return true if we were able to read all values, false if there was an I2C exception
     */
    private boolean readFromMicroController() {
        int data[] = new int [1];

        // check ADC3_VAL MSB and LSB, toggle LED if no result
        data = CONVERT_ADC(i2cManager.readByteFromDevice(ADC3_MSB_ADDR),i2cManager.readByteFromDevice(ADC3_LSB_ADDR));
        if (data != null)
            ADC3_VAL = data[0];
        else
            return false;

        // check ADC4 Val MSB and LSB, toggle LED if no result
        data = CONVERT_ADC(i2cManager.readByteFromDevice(ADC4_MSB_ADDR),i2cManager.readByteFromDevice(ADC4_LSB_ADDR));
        if (data != null)
            ADC4_VAL = data[0];
        else
            return false;

        // check ADC5 Val MSB and LSB, toggle LED if no result
        data = CONVERT_ADC(i2cManager.readByteFromDevice(ADC5_MSB_ADDR),i2cManager.readByteFromDevice(ADC5_LSB_ADDR));
        if (data != null)
            ADC5_VAL = data[0];
        else
            return false;

        // check ADA5 Val MSB and LSB, toggle LED if no result
        data = CONVERT_ADC(i2cManager.readByteFromDevice(ADA5_MSB_ADDR),i2cManager.readByteFromDevice(ADA5_LSB_ADDR));
        if (data != null)
            ADA5_VAL = convertTempReading(data[0]);
        else
            return false;

        return true;
    }


    /**
     * Convert the raw values to ADC by combining MSB and LSB bytes and removing sign extension
     * @param lsb Least significant value of the address
     * @param msb Most significant value of the address
     * @return the masked value
     */
    private int [] CONVERT_ADC(int [] msb, int [] lsb)
    {
        int [] value = new int [1];

        // check if there was an I2C exception
        if (lsb == null || msb == null) {
            return null;
        }

        //Remove if there is any sign extension
        value[0] = ((msb[0] & 0xFF) << 8) | (lsb[0] & 0xFF);

        return value; //Shift msb by 8
    }


    private int convertTempReading(double ada5) {
        // 250 MV equals to 25 degree celsius
        // Temperatue is in Linear scale
        // So divide by 10 to get the temperature reading
        Long l = Math.round(ada5 / 10.0);
        return Integer.valueOf(l.intValue());
    }


    /**
     * Set the motor movement based on the ADA value
     * @param temperature the adjusted value for the temp sensor
     */
    private void setMotorDutyCycle(double temperature)
    {
        if(temperature<15.0)
            if (!i2cManager.writeByteToDevice(PWM6_ADDR, (byte)80.0))
                toggleGPIO();

        else if(temperature<18.0)
            if (!i2cManager.writeByteToDevice(PWM6_ADDR, (byte)30.0))
                toggleGPIO();

        else if(temperature<22.0)
            if (!i2cManager.writeByteToDevice(PWM6_ADDR, (byte)50.0))
                toggleGPIO();

        else if(temperature<25.0)
            if (!i2cManager.writeByteToDevice(PWM6_ADDR, (byte)70.0))
                toggleGPIO();

        else
            if (!i2cManager.writeByteToDevice(PWM6_ADDR, (byte)40.0))
                toggleGPIO();
    }


    /** Upload the values retrieved from the PIC to the database whenever there is a change
     *  addListenerForSingleValueEvent allows us to do a one-time database write
     */
    private void updateDatabase() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setDatabaseValues(dataSnapshot,"ADA5IN", ADA5_VAL);
                setDatabaseValues(dataSnapshot,"ADC5IN", ADC5_VAL);
                setDatabaseValues(dataSnapshot,"ADC3IN", ADC3_VAL);
                setDatabaseValues(dataSnapshot,"ADC4IN", ADC4_VAL);
                database.child("TIMESTAMP").setValue(getTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /** Write the PWM and DAC information out to the microcontroller. Will include the updated data
     *  retrieved from the database. If I2C bus returns an exception, toggle the GPIO pin on the LED.
     */
    private void writeToMicroController() {
        if (!(i2cManager.writeByteToDevice(PWM3_ADDR, (byte) mRedVal)))
            toggleGPIO();
        if (!(i2cManager.writeByteToDevice(PWM4_ADDR, (byte) mBlueVal)))
            toggleGPIO();
        if (!(i2cManager.writeByteToDevice(PWM5_ADDR, (byte) mGreenVal)))
            toggleGPIO();
        if (!(i2cManager.writeByteToDevice(PWM6_ADDR, (byte) mMotorVal)))
            toggleGPIO();
        if (!(i2cManager.writeByteToDevice(DAC1_ADDR, (byte) mDacVal)))
            toggleGPIO();
    }


    /** Initiates the runnable that will toggle the GPIO several times on an interval
     *
     */
    private void toggleGPIO() {
        mPin14.setPinState(!mPin14.getPinState());
    }


    /** Set the database value based on key argument
     *
     * @param dataSnapshot the database info
     * @param key the item to change
     * @param value the new value
     */
    private void setDatabaseValues(DataSnapshot dataSnapshot, String key, int value)
    {
        dataSnapshot.getRef().child(key).setValue(value);
    }


    /**
     * Get the Current Timestamp
     * When there is an update, this will be called to update the database timestamp
     * @return the current timestamp
     */
    private String getTime()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d,YYYY h:mm:ss a zzzz");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return simpleDateFormat.format(new Date());
    }
}