/* 
THis code is written to recieve and send data from an arduino to Minized


#include <stdio.h>
#include "platform.h"
#include "xil_printf.h"
#include "xuartps.h"

#define NUM_OF_BYTE 1

XUartPs_Config *Config_0;
XUartPs Uart_PS_0;
XUartPs_Config *Config_1;
XUartPs Uart_PS_1;

int main()
{
    init_platform();
	int Status;
 char BufferPtr_rx[12]={"hello world"};
	int count=0;
	/*********
	 * UART 0 initialization *
	 *********/
	Config_0 = XUartPs_LookupConfig(XPAR_XUARTPS_0_DEVICE_ID);
	if (NULL == Config_0) {
		return XST_FAILURE;
	}
	Status = XUartPs_CfgInitialize(&Uart_PS_0, Config_0, Config_0->BaseAddress);
	if (Status != XST_SUCCESS) {
		return XST_FAILURE;
	}
	Config_1 = XUartPs_LookupConfig(XPAR_XUARTPS_1_DEVICE_ID);
	if (NULL == Config_1) {
		return XST_FAILURE;
	}
	Status = XUartPs_CfgInitialize(&Uart_PS_1, Config_1, Config_1->BaseAddress);
	if (Status != XST_SUCCESS) {
		return XST_FAILURE;
	}
	while(count < (sizeof(BufferPtr_rx)-1)){
		count+=XUartPs_Send(&Uart_PS_0, BufferPtr_rx,12);
		while (Status < 12) {
					Status +=	XUartPs_Recv(&Uart_PS_0, BufferPtr_rx, (12 - Status));
				}

	}
    cleanup_platform();
    return 0;
}
