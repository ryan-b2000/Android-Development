// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-05-2018

/*	Low-Level driver for the TLC5940.
	Receives string of bytes for the PWM duty cycle data and handles the process of
	sending the bytes to the TLC5940 and having the TLC5940 display them correctly.
*/

#include "tlc5940.h"

#define NUM_DEVICES 3								// number of TLC5940 devices
#define NUM_CHANNELS 16								// number of channels per devices
#define NUM_GS_VALUES (NUM_CHANNELS * NUM_DEVICES)	// number of GrayScale PWM numbers
#define NUM_GS_BYTES (24 * NUM_DEVICES)				// number of bytes in the bit stream to send
#define GS_CYCLE_DELAY 0							// delay between GrayScale cycles

// Class Constructor
tlc5940::tlc5940 ()
{
	gs_data_8b = new char [NUM_GS_BYTES];
}


// Class Destructor
tlc5940::~tlc5940()
{ }


// Set the TLC5940 control pins and initialize the GPIO
void tlc5940::setup(char vprg, char sdin, char sclk, char xlat, char blank, char dcprg, char gsclk)
{
	mVPRG = vprg;
	mSDIN = sdin;
	mSCLK = sclk;
	mXLAT = xlat;
	mBLANK = blank;
	mDCPRG = dcprg;
	mGSCLK = gsclk;

	// set the digital pins as outputs
	pinMode(mSCLK, OUTPUT);
	pinMode(mSDIN, OUTPUT);
	pinMode(mGSCLK, OUTPUT);
	pinMode(mVPRG, OUTPUT);
	pinMode(mXLAT, OUTPUT);
	pinMode(mBLANK, OUTPUT);
	pinMode(mDCPRG, OUTPUT);

	// set pins to their default state
	digitalWrite(mGSCLK, LOW);
	digitalWrite(mSCLK, LOW);
	digitalWrite(mDCPRG, LOW);	// data is read into the EEPROM
	//digitalWrite(mVPRG, HIGH);	// put device in Dot Correction mode
	digitalWrite(mXLAT, LOW);
	digitalWrite(mBLANK, LOW);		// turn on LEDs
}


// Display the LEDs
void tlc5940::display()
{
	// do GS PWM cycle
	do_gs_cycle();
}


// Set the bytes to all 0 and load into the TLC5940 to blank the LEDs
void tlc5940::clear()
{
	for (unsigned char i = 0; i < NUM_GS_BYTES; ++i)
		gs_data_8b[i] = 0;

	load_gs_bytes(gs_data_8b);	
}


// Update the TLC5940 with the received data
void tlc5940::update(char data[])
{
	// send bytes to TLC5940
	load_gs_bytes(data);
}


// Per the data sheet:
// We need to send in the gray scale data and latch it with the XLAT pin
void tlc5940::load_gs_bytes(char data[])
{
	digitalWrite(mXLAT, LOW);	// get ready to latch data when transfer is complete
	digitalWrite(mVPRG, LOW);	// put the device in 12-bit GS Mode
	digitalWrite(mDCPRG, LOW);	// data is read into the EEPROM
	
	for (int i = 0; i < NUM_GS_BYTES; ++i)
	{
		shift_out(data[i]);
	}

	// latch the GS data after sending
	digitalWrite(mXLAT, HIGH);
	digitalWrite(mXLAT, LOW);
}


// Do the 4096-bit gray scale cycle
// Turns the LEDs on for the time set by the GS value
void tlc5940::do_gs_cycle() {

	// toggle blank to start the GS cycle
	// BLANK = PIN 9 = PB1
	PORTB |= PORTB2;	// write blank pin high
	PORTB &= ~PORTB2;	// write blank pin low

	// Cycle through the GS counter
	// When the 12-bit GS value for a channel matches a counter, the channel is turned off
	for (int i = 0; i < 4096; i++)
	{
		// GS CLCK = PIN 7  = PD7
		PORTD |= PORTD7;	// write clock pin high
		PORTD &= ~PORTD7;	// write clock pin low

		//delay(GS_CYCLE_DELAY);	// add a delay to slow down the incrementing of the GS clock for testing
	}
}


// Shift out a single byte via bit-banging
void tlc5940::shift_out (char data)
{
	char bit;
	for (signed char i = 7; i >= 0; i--)
	{
		bit = (data >> i) & 0x01;
		// SDIN = PIN 12 = PB4
		digitalWrite(mSDIN, bit);
		
		// SCLK = PIN 11 = PB3
		PORTB |= PORTB3;	// write SCLK high
		PORTB &= ~PORTB3;	// write SCLK low
	}
}

