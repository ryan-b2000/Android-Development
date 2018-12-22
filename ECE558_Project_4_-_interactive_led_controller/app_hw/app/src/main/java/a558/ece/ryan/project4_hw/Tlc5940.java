// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package a558.ece.ryan.project4_hw;

import android.os.Handler;

import com.google.android.things.pio.PeripheralManager;


/** Class implements the high level driver for controlling the TLC5940. Class takes the RGB color data
 *  and converts the data into the byte stream that will ultimately be sent to the TLC5940 via the
 *  Arduino.
 */
public class Tlc5940 {

    // TLC5940 Defines
    private final int RED = 0;
    private final int GREEN = 1;
    private final int BLUE = 2;

    private final int NUM_OF_DEVICES = 3;
    private final int NUM_CHANNELS = 16;
    private final int NUM_GS_VALUES = NUM_CHANNELS * NUM_OF_DEVICES;
    private final int NUM_GS_BYTES = 24 * NUM_OF_DEVICES;

    private static final int WRITE_INTERVAL_MS = 100;

    // Peripheral Defines
    private Handler mWriteHandler;
    private String I2C_BUS = "I2C1";
    private int ARDUINO_ADDR = 0x04;

    // Private Data Members
    private int colors_12b [][];
    private int gs_data_12b [];		// the 12-bit grayscale color values (for simulation)
    private byte gs_data_8b [];		// converted 12-bit grayscale values
    I2cManager mI2cManager;
    private int mTransferCount;

    // Class Constructor
    public Tlc5940(PeripheralManager manager)
    {
        // initialize the data arrays
        gs_data_8b = new byte [NUM_GS_BYTES];
        gs_data_12b = new int [NUM_GS_VALUES];

        for(int i = 0; i < NUM_GS_BYTES; ++i)
            gs_data_8b[i]= 0;

        for(int i = 0; i < NUM_GS_VALUES; ++i)
            gs_data_12b[i]= 0;

        colors_12b = new int[3][NUM_CHANNELS];
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < NUM_CHANNELS; ++j)
                colors_12b[i][j] = 0;

        // initialize the I2C manager
        mI2cManager = new I2cManager(manager, I2C_BUS, ARDUINO_ADDR);

        mTransferCount = 0;

        mWriteHandler = new Handler();
        mWriteHandler.post(writeDataOut);
    }

    /** Runnable that handles the process of sending the byte stream data out to the Arduino in
     *  in periodic bursts.
     */
    private Runnable writeDataOut = new Runnable() {
        @Override
        public void run() {

            byte [] data = new byte [24];

            // copy the gs bytes to the local buffer for sending
            for (int i = 0; i < 24; ++i)
                data[i] = gs_data_8b[i + mTransferCount];

            // send I2C data to the Arduino
            mI2cManager.writeBufferToDevice(data, 24);

            ++mTransferCount;
            if (mTransferCount == 3) {
                mTransferCount = 0;
            }
            else {
                // set runnable to run again after interval expires
                mWriteHandler.postDelayed(writeDataOut, WRITE_INTERVAL_MS);
            }
        }
    };


    /** Method implements the overall algorithm for taking the 8-bit RGB values, converting them to
     *  12-bit PWM values, building the channel data, and converting that into a byte stream
     * @param color
     */
    public void updateColorDisplay(int color []) {

        // convert 8-bit color values to 12-bit PWM values
        int red = convertTo12bit(color[0]);
        int green = convertTo12bit(color[1]);
        int blue = convertTo12bit(color[2]);

        // fill in the color values for each red/green/blue channel
        build_channel_data(colors_12b[RED], red);
        build_channel_data(colors_12b[GREEN], green);
        build_channel_data(colors_12b[BLUE], blue);

        // sort the colors according to connection order: RGB RGB RGB...
        gs_data_12b = sort_channel_data(colors_12b);

        // convert 12-bit values to 8-bit byte stream
        convert_gsData(gs_data_8b, gs_data_12b);

        // write to i2c slave
        mWriteHandler.post(writeDataOut);
    }


    public int convertTo12bit(int convert) { return convert * 16; }


    /** Method converts (16) 12-bit scaled Gray Scale color values to (24) byte stream for shifting
     *  out (2) 12-bit numbers to (3) 8-bit bytes in the data
     * @param PWM the 12-bit PWM data
     * @param data the 8-bit byte stream data
     */
    public void convert_gsData(byte [] PWM, int [] data)
    {
        int j = 0;

        for(int i = 0; i < NUM_GS_VALUES; i+=3)
        {
            PWM[i]   = (byte) ((data[j]>>4) &  0xFF);
            PWM[i+1] = (byte) ((data[j]<<4) & 0xF0);
            PWM[i+1] |= (byte) ((data[j+1]>>8) & 0x0F);
            PWM[i+2] = (byte) ((data[j+1]) & 0xFF);
            j+=2;
        }
    }

    // Sort the 12-bit colors into RGB-RGB-RGB order
    private int[] sort_channel_data(int data[][]) {
        int [] sorted = new int [NUM_GS_VALUES];

        int c = 0;
        for (int i = 0; i < NUM_GS_VALUES; i+=3) {
            sorted[i + 0] = data[BLUE][c];
            sorted[i + 1] = data[GREEN][c];
            sorted[i + 2] = data[RED][c];
            ++c;
        }

        return sorted;
    }


    /** Method implements the building of the channel data based on the RGB values received
     * @param data the 12-bit PWM data
     * @param color the color to build in the channel
     */
    private void build_channel_data(int data[], int color)
    {
        for(int i = 0; i < NUM_CHANNELS; ++i)
        {
            data[i] = color;
        }
    }
}
