// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-05-2018

/*	Low-Level driver for the TLC5940.
	Receives string of bytes for the PWM duty cycle data and handles the process of
	sending the bytes to the TLC5940 and having the TLC5940 display them correctly.
*/

#ifndef _TLC5940_h
#define _TLC5940_h


#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif




class tlc5940 {
public:
	tlc5940();
	~tlc5940();

	void setup(char vprg, char sdin, char sclk, char xlat, char blank, char dcprg, char gsclk);
	void display();
	void clear();
	void update(char data[]);

private:
	void load_gs_bytes(char data[]);
	void do_gs_cycle();
	void shift_out (char data);
	
	char mVPRG, mSDIN, mSCLK, mXLAT, mBLANK, mDCPRG, mGSCLK, mSOUT;
	char data_bytes[]; 
	char numbytes;

	char * gs_data_8b;
};


#endif

