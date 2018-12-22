
/** Ryan Bentz and Ram Bhattarai
 *  ECE 558
 *  Project 3
 */
package ryanb_ramb.project3.ece558.hardwaredev;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.List;

/** Manager to handle the interface with the I2C peripheral
 *
 */
public class I2cManager {

    // private class members
    private String TAG = "I2C";

    PeripheralManager peripheralManager;
    private I2cDevice i2cDevice;


    /** Constructor checks that the peripheral exists and initializes it for use.
     *
     * @param manager the peripheral manager from the calling activity
     * @param name the name of the I2C bus to use
     * @param address the address of the peripheral device connected to the bus
     */
    public I2cManager (PeripheralManager manager, String name, int address) {
        // set the peripheral manager
        peripheralManager = manager;

        // open the I2C Device
        try {
            i2cDevice = peripheralManager.openI2cDevice(name, address);
        }
        catch (IOException ioe) {
            Log.w(TAG, "Unable to open I2C device");
        }
    }

    /** Free the device resources when closing down the application.
     *  Checks that we can close the device and does so if possible.
     */
    public void closeDevice() {
        if (i2cDevice != null) {
            try {
                i2cDevice.close();
                i2cDevice = null;
            }
            catch (IOException ioe) {
                Log.w(TAG, "Unable to close device");
            }
        }
    }


    /** Writes a byte to the specified address on the peripheral
     *
     * @param address of the register to write to
     * @param data to write to the device
     * @return true if successful, false otherwise
     */
    public boolean writeByteToDevice(byte address, byte data) {

        try {
            i2cDevice.writeRegByte(address, data);
        }
        catch (IOException ioe) {
            Log.w(TAG, "Unable to write to I2C bus.");

            // toggle GPIO
            return false;
        }

        return true;
    }


    /** Reads a byte from the device connected to the I2C bus
     *
     * @param address the register address to access
     * @return the byte that was read from the device
     */
    public int [] readByteFromDevice(int address) {
        int data [] = new int [1];

        try {
            data[0] = i2cDevice.readRegByte(address);
        }
        catch (IOException ioe) {
            Log.w(TAG, "Unable to write to I2C bus.");

            // toggle GPIO
            return null;
        }

        return data;
    }
}
