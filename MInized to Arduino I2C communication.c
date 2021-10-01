/* 
Thsi code is written to Communicate with an arduino through I2C communication protocol as a master
*/


#include "xparameters.h"
#include "xiicps.h"
#include "xil_printf.h"


#define DeviceID		XPAR_XIICPS_0_DEVICE_ID //setting the ID of the minized

#define I2C_SLAVE		4 // defining the  slave address
#define I2C_SCLK	100000 // definig the clock rate



int IicPsMasterPolledExample(u16 DeviceId);
/********* Variable Definitions ***********/

XIicPs I2c;		/**< Instance of the IIC Device */


int main(void)
{
	int Status;
  
	Status = senddata(DeviceID);
	if (Status != XST_SUCCESS) {
		return XST_FAILURE;
	}
	return XST_SUCCESS;
}

int sendata(u16 DeviceId)
{
	int Status;
	XIicPs_Config *Config;
	int Index;

	/*
	 * Initialize the IIC driver so that it's ready to use
	 * Look up the configuration in the config table,
	 * then initialize it.
	 */
	Config = XIicPs_LookupConfig(DeviceId);
	if (NULL == Config) {
		return XST_FAILURE;
	}

	Status = XIicPs_CfgInitialize(&I2c, Config, Config->BaseAddress);
	if (Status != XST_SUCCESS) {
		return XST_FAILURE;
	}

	/*
	 * Perform a self-test to ensure that the hardware was built correctly.
	 */
	Status = XIicPs_SelfTest(&I2c);
	if (Status != XST_SUCCESS) {
		return XST_FAILURE;
	}
	XIicPs_SetSClk(&I2c, I2C_SCLK);// setting thr clock rate

char data[11]="hello world";
	
	Status = XIicPs_MasterSendPolled(&I2c, data, 11, I2C_SLAVE); // sending data to the arduino with designated slave address
	if (Status != XST_SUCCESS) {
		return XST_FAILURE;}
	return XST_SUCCESS;
}
