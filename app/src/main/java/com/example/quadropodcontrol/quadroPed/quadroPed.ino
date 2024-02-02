#include <Servo.h>
//номера сервомоторов   0  1  2  3   4  5  6  7
int servoPins[] = { 2, 3, 4, 5, 10, 7, 8, 9 };
Servo servos[8];

void servoCalibration() {
    //скорректировать начальные углы
  //body servos along
  servos[0].write(120);
  servos[4].write(120);
  servos[2].write(60);
  servos[6].write(60);
  delay(2000);
  //body servos across
  servos[0].write(40);
  servos[4].write(40);
  servos[2].write(140);
  servos[6].write(140);

  //leg servos - up
  servos[1].write(160);
  servos[5].write(160);
  servos[3].write(20);
  servos[7].write(20);
  delay(2000);
  //leg servos - down
  servos[1].write(10);
  servos[5].write(10);
  servos[3].write(170);
  servos[7].write(170);
  delay(2000);
  //body servos along
  servos[0].write(120);
  servos[4].write(120);
  servos[2].write(60);
  servos[6].write(60);
  // for (int i = 1; i<8; i+=2) {
  //   servos[i].write(120);
  // }
  // servos[4].write(120);
  // servos[2].write(60);
  // servos[6].write(60);
}

String message = "";

void setup() {
  Serial.begin(9600);
  for (int i = 0; i < 8; i++) {
    pinMode(servoPins[i], OUTPUT);
    servos[i].attach(servoPins[i]);
  }
  delay(1000);
  servoCalibration();
  Serial.println("Servo ready!");
}

void rotateServo(String cmd) {
  // Serial.print("rotate = ");
  // Serial.println(cmd);
  int servoNumber = cmd[0] - '0';
  // Serial.print("servoNumber = ");
  // Serial.print(servoNumber);
  int angle = cmd.substring(2).toInt();
  // Serial.print(", angle = ");
  // Serial.println(angle);
  if (angle >= 0 && angle <= 180) servos[servoNumber].write(angle);
  // else Serial.println("Wrong angle");
}

void runCmd(String cmd) {  //ф-ия для разбора поступившей из порта команды
  // Serial.print("cmd = ");
  // Serial.println(cmd);
  if (cmd[0] >= '0' && cmd[0] <= '9') rotateServo(cmd);
}

void loop() {
  if (Serial.available()) {
    char a = Serial.read();
    if (a == '\n') {
      // Serial.print("message = ");
      // Serial.println(message);
      runCmd(message);
      message = "";
    } else message += a;
  }
  // put your main code here, to run repeatedly:
}
