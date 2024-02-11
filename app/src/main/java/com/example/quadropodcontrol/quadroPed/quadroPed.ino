#include <Servo.h>

//номера сервомоторов   0  1  2  3   4  5  6  7
int servoPins[] = {2, 3, 4, 5, 10, 7, 8, 9};
Servo servos[8];
int delayBetweenMoves = 200;

void forward() {
    //подготовка
    servos[0].write(100);
    servos[4].write(100);
    servos[2].write(50);
    servos[6].write(50);
    delay(delayBetweenMoves);
//legs 3 and 7 up
    servos[3].write(120);
    servos[7].write(120);
    delay(delayBetweenMoves);
//arms 2 and 6 forward
    servos[2].write(100);
    servos[6].write(5);
    delay(delayBetweenMoves);
    //legs 3 and 7 down
    servos[3].write(155);
    servos[7].write(155);
    delay(delayBetweenMoves);
//legs 1 and 5 up
    servos[1].write(70);
    servos[5].write(70);
    delay(delayBetweenMoves);
    servos[0].write(50);
    servos[4].write(150);
// arms 2 and 6 turn
    servos[2].write(50);
    servos[6].write(50);
    delay(delayBetweenMoves);
//legs 1 and 5 down
    servos[1].write(35);
    servos[5].write(35);
    delay(delayBetweenMoves);
    //legs 3 and 7 up
    servos[3].write(120);
    servos[7].write(120);
    delay(delayBetweenMoves);
    servos[0].write(100);
    servos[4].write(100);
    servos[2].write(50);
    servos[6].write(50);
    delay(delayBetweenMoves);
//legs 3 and 7 down
    servos[3].write(155);
    servos[7].write(155);
}

void back() {

}

void turnLeft() {
    //подготовка
    servos[0].write(100);
    servos[4].write(100);
    servos[2].write(50);
    servos[6].write(50);
    delay(delayBetweenMoves);
//legs 1 and 5 up
    servos[1].write(70);
    servos[5].write(70);
    delay(delayBetweenMoves);
//arms 0 and 4 turn left
    servos[0].write(10);
    servos[4].write(10);
    delay(delayBetweenMoves);
    //legs 1 and 5 down
    servos[1].write(35);
    servos[5].write(35);
    delay(delayBetweenMoves);
//legs 3 and 7 up
    servos[3].write(120);
    servos[7].write(120);
    delay(delayBetweenMoves);
// arms 0 and 4 turn
    servos[0].write(100);
    servos[4].write(100);
    delay(delayBetweenMoves);
//legs 3 and 7 down
    servos[3].write(155);
    servos[7].write(155);
}

void turnRight() {
    //подготовка
    servos[0].write(100);
    servos[4].write(100);
    servos[2].write(50);
    servos[6].write(50);
    delay(delayBetweenMoves);
//legs 3 and 7 up
    servos[3].write(120);
    servos[7].write(120);
    delay(delayBetweenMoves);
//arms 2 and 6 turn right
    servos[2].write(140);
    servos[6].write(140);
    delay(delayBetweenMoves);
    //legs 3 and 7 down
    servos[3].write(155);
    servos[7].write(155);
    delay(delayBetweenMoves);
//legs 1 and 5 up
    servos[1].write(70);
    servos[5].write(70);
    delay(delayBetweenMoves);
// arms 2 and 6 turn
    servos[2].write(50);
    servos[6].write(50);
    delay(delayBetweenMoves);
//legs 1 and 5 down
    servos[1].write(35);
    servos[5].write(35);
}


void servoCalibration() {
    // servos[7].write(0);
    // delay(3000);

    servos[1].write(110);
    servos[5].write(110);
    servos[3].write(20);
    servos[7].write(20);
    //body servos along
    delay(2000);
    //body servos across
    servos[0].write(100);
    servos[4].write(100);
    servos[2].write(50);
    servos[6].write(50);

    delay(2000);
    //leg servos - down
    // servos[1].write(10); //35
    // servos[5].write(10);//35
    // servos[3].write(170); //100
    // servos[7].write(170);//100
    servos[1].write(35); //35
    servos[5].write(35);//35
    servos[3].write(155); //155
    servos[7].write(155);//155
    delay(2000);
    //body servos along
    // servos[0].write(120); //100
    // servos[4].write(120);//100
    // servos[2].write(60);//50
    // servos[6].write(60);//50
    servos[0].write(50); //50
    servos[4].write(50);//50
    servos[2].write(100);//100
    servos[6].write(100);//100
    // for (int i = 1; i<8; i+=2) {
    //   servos[i].write(120);
    // }
    // servos[4].write(120);
    // servos[2].write(60);
    // servos[6].write(60);
    // delay(1000);
    // Serial.println("3-0");
    // servos[3].write(0);
    delay(2000);
    turnRight();
    delay(2000);
    turnLeft();
    delay(2000);
}

String message = "";
String curMessage = "";

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
    if (cmd[0] == 'f') forward();
    if (cmd[0] == 'b') back();
    if (cmd[0] == 'l') {
      turnLeft();
      // delay(100);
    }
    if (cmd[0] == 'r') {
      turnRight();
      // delay(100);
    }
}
long time = millis();
void loop() {
    if (Serial.available()) {
        char a = Serial.read();
        if (a == '\n') {
            // Serial.print("message = ");
            // Serial.println(message);
            // runCmd(message);
            curMessage = message;
            message = "";
        } else message += a;
    }
    if (curMessage[0] >= '0' && curMessage[0] <= '9') runCmd(curMessage);
    else if (millis() - time>=50) {
      runCmd(curMessage);
      time = millis();
    }
    // delay(50);
}

// void loop() {
//     if (Serial.available()) {
//         char a = Serial.read();
//         if (a == '\n') {
//             // Serial.print("message = ");
//             // Serial.println(message);
//             runCmd(message);
//             message = "";
//         } else message += a;
//     }
// }
