

void setup(){
  pinMode(13, OUTPUT);
  Serial.begin(9600); 
}

void loop(){
  int val = analogRead(0);
  Serial.println(val);  
  delay(10);
}
