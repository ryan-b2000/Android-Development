// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-05-2018

/*	Low-Level driver for the TLC5940.
	Receives string of bytes for the PWM duty cycle data and handles the process of
	sending the bytes to the TLC5940 and having the TLC5940 display them correctly.
*/


#include "tlc5940.h"
#include <Wire.h>

#define PIN_GSCLK 7
#define PIN_DCPRG 8
#define PIN_BLANK 9
#define PIN_XLAT 10
#define PIN_SCLK 11
#define PIN_SIN 12
#define PIN_VPRG 13

#define NUM_COLORS 8
#define TIMER_PERIOD_MS 1000
#define TIMER_PERIOD_MULTIPLIER 15.5		// 15.5 for getting intervals in milliseconds

unsigned char rowIndex;

#define NUM_DEVICES 3
#define NUM_GS_BYTES (24 * NUM_DEVICES)
#define NUM_GS_VALUES (16 * NUM_DEVICES)

tlc5940 tlc;

// the gray scale bytes received from the master controller
char gs_bytes[NUM_GS_BYTES];


// Set up for the Arduino and TLC5940
void setup ()
{
	// set the initial states of the TLC5940 control pins
	tlc.setup(PIN_VPRG, PIN_SIN, PIN_SCLK, PIN_XLAT, PIN_BLANK, PIN_DCPRG, PIN_GSCLK);

	// initialize I2C
	Wire.begin(4);
	Wire.onReceive(receiveEvent);

	tlc.clear();
}


// Main loop displays the selected color until we get new data
void loop ()
{
	tlc.display();
}


// I2C receive event simulates SM Bus protocol
void receiveEvent (int numBytes) {
	static char count = 0;
	static char reg;

	// read the register address
	reg = Wire.read();

	// read the register data
	while (Wire.available())
	{
		gs_bytes[count] = Wire.read();
		++count;

		if (count == NUM_GS_BYTES)
		{
			tlc.update(gs_bytes);
			count = 0;
		}
	}
}

