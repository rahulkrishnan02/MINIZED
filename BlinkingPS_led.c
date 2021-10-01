/* 
This code is written to blink the PS(processing system) LED on the AVNET MINIZED FPGA.
*/




#include <stdio.h>
#include "platform.h"
#include "xil_printf.h"
#include "xparameters.h"
#include "xgpiops.h"

#define LOOP_DELAY 		50000000U

int main()
{

	XGpioPs_Config *XGPIO_Config;
	XGpioPs my_Gpio;

	int Status;

	init_platform();


     XGPIO_Config = XGpioPs_LookupConfig(XPAR_PS7_GPIO_0_DEVICE_ID);

     Status = XGpioPs_CfgInitialize(&my_Gpio, XGPIO_Config, XGPIO_Config->BaseAddr);

	XGpioPs_SetDirectionPin(&my_Gpio, 52, 1);
	XGpioPs_SetDirectionPin(&my_Gpio, 53, 1);

	XGpioPs_SetOutputEnablePin(&my_Gpio, 52, 1);
	XGpioPs_SetOutputEnablePin(&my_Gpio, 53, 1);

	u32 idx = 0;

	// main code
	for (;;)
	{

		// RED
		XGpioPs_WritePin(&my_Gpio, 52, 1);
		XGpioPs_WritePin(&my_Gpio, 53, 0);


		// CRUDE DELAY
		for (idx = 0; idx <= LOOP_DELAY; idx++)
			{}

		// GREEN
		XGpioPs_WritePin(&my_Gpio, 52, 0);
		XGpioPs_WritePin(&my_Gpio, 53, 1);

		// CRUDE DELAY
		for (idx = 0; idx <= LOOP_DELAY; idx++)
			{}

	}

	// Should never get here.
    cleanup_platform();

    return 0;

}
