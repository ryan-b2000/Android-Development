// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package a558.ece.ryan.project4_hw;

import android.util.Log;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;
import java.io.IOException;


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
     * @param busID the busID of the I2C bus to use
     * @param address the address of the peripheral device connected to the bus
     */
    public I2cManager (PeripheralManager manager, String busID, int address) {
        // set the peripheral manager
        peripheralManager = manager;

        // open the I2C Device
        try {
            i2cDevice = peripheralManager.openI2cDevice(busID, address);
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

    public boolean writeBufferToDevice(byte buffer [], int len) {

        try {
            i2cDevice.writeRegBuffer(0x00, buffer, len);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
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
