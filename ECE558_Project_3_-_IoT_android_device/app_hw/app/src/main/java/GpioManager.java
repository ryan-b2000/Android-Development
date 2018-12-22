/** Ryan Bentz and Ram Bhattarai
 *  ECE 558
 *  Project 3
 */
package ryanb_ramb.project3.ece558.hardwaredev;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/** Manager to interface with the GPIO pin
 *  Each instance manages a single GPIO pin
 */
public class GpioManager {

    // private class members
    private static final String TAG = "GPIO";
    private Gpio mPin;


    /** Checks that the pin can be initialized and does so
     *
     * @param manager the peripheral manager from the calling activity
     * @param pin the pin to initialize and use
     */
    public GpioManager (PeripheralManager manager, String pin){
        // initialize GPIO pin for I2C errors
        try {
            mPin = manager.openGpio(pin);
            mPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mPin.setValue(true);
        }
        catch (IOException ioe) {
            Log.w(TAG, "Unable to open GPIO.");
        }
    }


    /** Sets the pin state to the specified value
     *
     * @param value to set the pin
     */
    public void setPinState (boolean value) {
        try {
            mPin.setValue(value);
        }
        catch (IOException ioe) {
            Log.w(TAG, "Unable to set GPIO.");
            ioe.printStackTrace();
        }
    }


    /** Gets the current state of the pin and returns it
     *
     * @return the value of the pin state
     */
    public boolean getPinState () {
        try {
            return mPin.getValue();
        }
        catch (IOException ioe) {
            Log.w(TAG, "Unable to set GPIO.");
            ioe.printStackTrace();
            return false;
        }
    }


    /** Free the device resources when closing down the application.
     *
     */
    public void close() {
        // free gpio resources
        try {
            mPin.close();
        }
        catch (IOException ioe) {
            Log.w(TAG, "Error closing GPIO pins.");
            ioe.printStackTrace();
        }
        finally {
            mPin = null;
        }
    }

}
