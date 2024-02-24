// #include <Adafruit_GFX.h>  // подключаем библиотеки
// #include <Adafruit_SSD1306.h>

// #ifndef Adafruit_GFX_h
//   #define Adafruit_GFX_h
// #endif  // Adafruit_GFX_h

// #ifndef Adafruit_SSD1306_h
//   #define Adafruit_SSD1306_h
// #endif  // Adafruit_SSD1306_h
#include <OLED_I2C.h>
OLED  display(SDA, SCL, 8);

class Eye {

private:
  enum { FORWARD,
         LOOK_RIGHT,
        LOOK_LEFT } eyesLook;

 // Adafruit_SSD1306 display;
  // OLED  display;
  fillCircle(int x, int y, int r){
    for (int x1 = x-r+1; x1<x+r; x1++){
      for(int y1 = y-r+1; y1<y+r; y1++){
        if ((pow(x-x1, 2)+pow(y-y1, 2))<r*r){
          display.setPixel(x1, y1);
        }
      }
    }
  }

    clearCircle(int x, int y, int r){
    for (int x1 = x-r+1; x1<x+r; x1++){
      for(int y1 = y-r+1; y1<y+r; y1++){
        if ((pow(x-x1, 2)+pow(y-y1, 2))<r*r){
          display.clrPixel(x1, y1);
        }
      }
    }
  }

  void drawEye(int x, int y, int r, int look = FORWARD) {
    // display.fillCircle(x, y, r, WHITE);
    display.drawCircle(x, y, r);
    fillCircle(x, y, r);
    if (look == FORWARD) {
    //   display.fillCircle(x, y, r / 2, BLACK);
    //   display.fillCircle(x, y, r / 6, WHITE);
      display.clrCircle(x, y, r / 2);
      clearCircle(x, y, r / 2);
      display.drawCircle(x, y, r / 6);
      fillCircle(x, y, r / 6);
    }
    if (look == LOOK_RIGHT) {
    //   display.fillCircle(x - r / 2, y, r / 2, BLACK);
    //   display.fillCircle(x - r / 2, y, r / 6, WHITE);
        display.drawCircle(x - r / 2, y, r / 2);
        clearCircle(x-r/2, y, r / 2);
        display.drawCircle(x - r / 2, y, r / 6);
        fillCircle(x-r/2, y, r / 6);
    }
    if (look == LOOK_LEFT) {
    //   display.fillCircle(x + r / 2, y, r / 2, BLACK);
    //   display.fillCircle(x + r / 2, y, r / 6, WHITE);
      display.drawCircle(x + r / 2, y, r / 2);
      clearCircle(x+r/2, y, r / 2);
      display.drawCircle(x + r / 2, y, r / 6);
      fillCircle(x+r/2, y, r / 6);
    }
    // display.fillRect(x - r, y - r, 2 * r, r / 4, BLACK);
    // display.fillRect(x - r, y + r * 3 / 4, 2 * r, r / 2, BLACK);
    // display.drawCircle(64, 32, 10);
    // display.update();
  }
public:
  Eye() {
    display.begin();
    // display = disp;
  }

  void drawLeftEye(int look = FORWARD) {
    drawEye(64 / 2, 32, 30, look);
  }

  void drawRightEye(int look = FORWARD) {
    drawEye(64 + 32, 32, 30, look);
  }

  void lookForward() {
    display.clrScr();
    drawLeftEye();
    drawRightEye();
    //display.display();
    display.update();
  }

  void lookLeft() {
    drawLeftEye(LOOK_LEFT);
    drawRightEye(LOOK_LEFT);
    //display.display();
    display.update();
  }

  void lookRight() {
    display.clrScr();
    drawLeftEye(LOOK_RIGHT);
    drawRightEye(LOOK_RIGHT);
    //display.display();
    display.update();
  }

  void lookInside(){
    drawLeftEye(LOOK_LEFT);
    drawRightEye(LOOK_RIGHT);
    //display.display();
    display.update();
  }

  void lookOutside(){
    drawLeftEye(LOOK_RIGHT);
    drawRightEye(LOOK_LEFT);
    //display.display();
    display.update();
  }
  void lookUp(){

  }

  void lookDown(){

  }

  void drawEyes() {
    lookForward();
  }
};