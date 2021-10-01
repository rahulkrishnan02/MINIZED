/*this code is written on the arduino to enable UART commmunication with MINIZED*/


char reciever[12]; //Initialized variable to store recieved data
char sender[12]="hello world";

void setup() {
  // Begin the Serial at 9600 Baud
  Serial.begin(115200);
}

void loop() {
  Serial.readBytes(reciever,12); //Read the serial data and store in var
  Serial.println(reciever); //Print data on Serial Monitor
  Serial.write(sender);
 
}
