import java.awt.Color;
import java.util.Random;
import tester.Tester;
import javalib.funworld.*;
import javalib.worldimages.*;


// Represents a game of Simon Says
class SimonWorld extends World {
  //add fields needed to keep track of the state of the world
  ILoButton clickedSequence;
  ILoButton flashSequence;
  ILoButton futureSequence;
  String click;
  Random rand;

  // constructor for gameplay
  SimonWorld() {
    this(new MtLoButton(), new MtLoButton(), new MtLoButton(), "", new Random());
  }

  // constructor with new randoms
  SimonWorld(ILoButton clickedSequence, ILoButton flashSequence, 
      ILoButton futureSequence, String click) {
    this.clickedSequence = clickedSequence;
    this.flashSequence = flashSequence;
    this.futureSequence = futureSequence;
    this.click = click;
    this.rand = new Random();
  }

  // constructor for testing
  SimonWorld(ILoButton clickedSequence, 
      ILoButton flashSequence, ILoButton futureSequence, String click, Random rand) {
    this.clickedSequence = clickedSequence;
    this.flashSequence = flashSequence;
    this.futureSequence = futureSequence;
    this.click = click;
    this.rand = rand;
  }

  // Draw the current state of the world
  public WorldScene makeScene() {
    Button greenButton = new Button(Color.green, 250, 250);
    Button redButton = new Button(Color.red, 250, 250);
    Button yellowButton = new Button(Color.yellow, 250, 250);
    Button blueButton = new Button(Color.blue, 250, 250);
    WorldScene background = new WorldScene(500, 500);
    WorldImage topSquares = new BesideImage(greenButton.drawDark(), 
        redButton.drawDark());
    WorldImage bottomSquares = new BesideImage(yellowButton.drawDark(),
        blueButton.drawDark());
    WorldImage combineSquares = new AboveImage(topSquares, bottomSquares);
    WorldScene shapesToBackground = background.placeImageXY(combineSquares, 250, 250);
    WorldScene flashColor = this.futureSequence.flashSequence(shapesToBackground);

    if (this.click.equals("Green")) {
      return flashColor.placeImageXY(greenButton.drawLit(), 125, 125);
    }
    else if (this.click.equals("Red")) {
      return flashColor.placeImageXY(redButton.drawLit(), 375, 125);
    }
    else if (this.click.equals("Blue")) {
      return flashColor.placeImageXY(blueButton.drawLit(), 375, 375);
    }
    else if (this.click.equals("Yellow")) {
      return flashColor.placeImageXY(yellowButton.drawLit(), 125, 375);
    }
    else {
      return flashColor;
    }
  }

  // handles ticking of the clock and updating the world if needed
  public World onTick() {
    if (this.flashSequence.startsWith(this.clickedSequence) 
        && this.clickedSequence.length() == this.flashSequence.length()) {
      Button randomButton = this.randomButton();
      return new SimonWorld(new MtLoButton(), 
          this.flashSequence.insert(randomButton), 
          this.flashSequence.insert(randomButton).unflash(), "", this.rand);
    }
    else if (!this.flashSequence.startsWith(this.clickedSequence)) {
      return this.endOfWorld("You lose :( Try Again!");
    }
    else {
      return new SimonWorld(this.clickedSequence, 
          this.flashSequence, this.futureSequence.remove(), this.click, this.rand);
    }
  }

  // Returns the final scene with the given message displayed
  public WorldScene lastScene(String msg) {
    WorldScene background = new WorldScene(500, 500);
    WorldScene numButtons = background.placeImageXY(new TextImage(this.flashSequence.length() 
        + " Buttons", 40, Color.black), 250, 350);
    return numButtons.placeImageXY(new TextImage(msg, 40, Color.black), 250, 250);
  }

  // handles mouse presses and is given the mouse location
  // lights up the color when pressed
  public SimonWorld onMousePressed(Posn pos) {
    if (pos.x <= 250 && pos.y <= 250) {
      return new SimonWorld(this.clickedSequence, this.flashSequence, 
          this.futureSequence, "Green", this.rand);
    }
    else if (pos.x >= 250 && pos.x <= 500 && pos.y <= 250) {
      return new SimonWorld(this.clickedSequence, this.flashSequence, 
          this.futureSequence, "Red", this.rand);
    }
    else if (pos.x >= 250 && pos.x <= 500 && pos.y >= 250 && pos.y <= 500) {
      return new SimonWorld(this.clickedSequence, this.flashSequence, 
          this.futureSequence, "Blue", this.rand);
    }
    else {
      return new SimonWorld(this.clickedSequence, this.flashSequence, 
          this.futureSequence, "Yellow", this.rand);
    }
  }

  // handles mouse releases and is given the mouse location
  // adds the clicked button to the clicked sequence and unflashes the color
  public SimonWorld onMouseReleased(Posn pos) {
    if (pos.x <= 250 && pos.y <= 250) {
      Button clickedButton = new Button(Color.green, 250, 250);
      return new SimonWorld(this.clickedSequence.insert(clickedButton), this.flashSequence, 
          this.futureSequence, "", this.rand);
    }
    else if (pos.x >= 250 && pos.x <= 500 && pos.y <= 250) {
      Button clickedButton = new Button(Color.red, 250, 250);
      return new SimonWorld(this.clickedSequence.insert(clickedButton), this.flashSequence, 
          this.futureSequence, "", this.rand);
    }
    else if (pos.x >= 250 && pos.x <= 500 && pos.y >= 250 && pos.y <= 500) {
      Button clickedButton = new Button(Color.blue, 250, 250);
      return new SimonWorld(this.clickedSequence.insert(clickedButton), this.flashSequence, 
          this.futureSequence, "", this.rand);
    }
    else {
      Button clickedButton = new Button(Color.yellow, 250, 250);
      return new SimonWorld(this.clickedSequence.insert(clickedButton), this.flashSequence, 
          this.futureSequence, "", this.rand);
    }
  }

  // generates a random button
  public Button randomButton() {
    int randomNum = this.rand.nextInt(4);
    if (randomNum == 0) {
      return new Button(Color.green, 250, 250);
    }

    else if (randomNum == 1) {
      return new Button(Color.red, 250, 250);
    }

    else if (randomNum == 2) {
      return new Button(Color.yellow, 250, 250);
    }

    else {
      return new Button(Color.blue, 250, 250);
    }
  }
} 

// Represents a list of buttons
interface ILoButton {

  // inserts the given button at the end of the list
  ILoButton insert(Button button);

  // counts the amount of items in the list
  int length();

  // removes the first button from the list
  ILoButton remove();

  // returns a button at the given index and count
  Button buttonIndex(int index, int count);

  // returns true if a list of buttons starts with the given list of buttons
  boolean startsWith(ILoButton buttons);

  // flashes the buttons in a sequence
  WorldScene flashSequence(WorldScene scene);

  // adds spaces to unflash buttons between sequences
  ILoButton unflash();
} 

// Represents an empty list of buttons
class MtLoButton implements ILoButton {
  MtLoButton() {}

  // inserts the given button at the end of the list
  public ILoButton insert(Button button) {
    return new ConsLoButton(button, this);
  }

  // counts the amount of items in the list
  public int length() {
    return 0;
  }

  // removes the first button from the list
  public ILoButton remove() {
    return this;
  }

  // returns a button at the given index and count
  public Button buttonIndex(int index, int count) {
    return new Button(Color.gray, 250, 250);
  }

  // returns true if a list of buttons starts with the given list of buttons
  public boolean startsWith(ILoButton buttons) {
    return buttons.length() == 0;
  }

  // flashes the buttons in a sequence
  public WorldScene flashSequence(WorldScene scene) {
    return scene;
  }

  // adds spaces to unflash buttons between sequences
  public ILoButton unflash() {
    return this;
  }
} 

// Represents a non-empty list of buttons
class ConsLoButton implements ILoButton {
  Button first;
  ILoButton rest;

  ConsLoButton(Button first, ILoButton rest) {
    this.first = first;
    this.rest = rest;
  }

  // inserts the given button at the end of the list
  public ILoButton insert(Button button) {
    return new ConsLoButton(this.first, this.rest.insert(button));
  }

  // counts the amount of items in the list
  public int length() {
    return 1 + this.rest.length();
  }

  // removes the first button from the list
  public ILoButton remove() {
    return this.rest;
  }

  // returns a button at the given index and count
  public Button buttonIndex(int index, int count) {
    if (index == count) {
      return this.first;
    }
    else {
      return this.rest.buttonIndex(index, count + 1);
    }
  }

  // returns true if a list of buttons starts with the given list of buttons
  public boolean startsWith(ILoButton buttons) {
    if (buttons.length() == 0) {
      return true;
    }
    else if (this.length() < buttons.length()) {
      return false;
    }
    else {
      return this.first.sameButton(buttons.buttonIndex(1, 1))
          && this.rest.startsWith(buttons.remove());
    }
  }

  // flashes the buttons in a sequence
  public WorldScene flashSequence(WorldScene scene) {
    if (this.first.sameColor(Color.green)) {
      return scene.placeImageXY(this.first.drawLit(), 125, 125);
    }
    else if (this.first.sameColor(Color.red)) {
      return scene.placeImageXY(this.first.drawLit(), 375, 125);
    }
    else if (this.first.sameColor(Color.blue)) {
      return scene.placeImageXY(this.first.drawLit(), 375, 375);
    }
    else if (this.first.sameColor(Color.yellow)) {
      return scene.placeImageXY(this.first.drawLit(), 125, 375);
    }
    else {
      return scene;
    }
  }

  // adds spaces to unflash buttons between sequences
  public ILoButton unflash() {
    return new ConsLoButton(new Button(Color.gray, 250, 250), new ConsLoButton(this.first, 
        this.rest.unflash()));
  }
}

// Represents one of the four buttons you can click
class Button {
  Color color;
  int x;
  int y;

  Button(Color color, int x, int y) {
    this.color = color;
    this.x = x;
    this.y = y;
  }

  // draws the button with a given color
  WorldImage drawButton(Color color) {
    return new RectangleImage(this.x, this.y, OutlineMode.SOLID, color);
  }

  // Draw this button dark
  WorldImage drawDark() {
    return this.drawButton(this.color.darker().darker());
  }

  // Draw this button lit
  WorldImage drawLit() {
    return this.drawButton(this.color.brighter().brighter());
  }

  // returns true if this button is the same as the given button
  boolean sameButton(Button button) {
    return this.color.equals(button.color)
        && this.x == button.x
        && this.y == button.y;
  }

  // returns true if this button is the same color as the given color
  boolean sameColor(Color color) {
    return this.color.equals(color);
  }
}

// to represent examples and tests for simon says
class ExamplesSimon {
  ExamplesSimon() {}

  // examples of buttons
  Button greenButton = new Button(Color.green, 250, 250);
  Button redButton = new Button(Color.red, 250, 250);
  Button yellowButton = new Button(Color.yellow, 250, 250);
  Button blueButton = new Button(Color.blue, 250, 250);
  Button grayButton = new Button(Color.gray, 250, 250);

  // examples of lists of buttons
  ILoButton empty = new MtLoButton();
  ILoButton sequence1 = new ConsLoButton(this.redButton, this.empty);
  ILoButton sequence2 = new ConsLoButton(this.redButton, 
      new ConsLoButton(this.blueButton, this.empty));
  ILoButton sequence2Diff = new ConsLoButton(this.redButton, 
      new ConsLoButton(this.greenButton, this.empty));
  ILoButton sequence3 = new ConsLoButton(this.redButton, 
      new ConsLoButton(this.blueButton, new ConsLoButton(this.greenButton, this.empty)));
  ILoButton sequence4 = new ConsLoButton(this.redButton, new ConsLoButton(this.blueButton, 
      new ConsLoButton(this.greenButton, new ConsLoButton(this.yellowButton, this.empty))));
  ILoButton sequence4Minus1 = new ConsLoButton(this.blueButton, 
      new ConsLoButton(this.greenButton, new ConsLoButton(this.yellowButton, this.empty)));
  ILoButton diffSequence = new ConsLoButton(this.blueButton, new ConsLoButton(this.greenButton, 
      new ConsLoButton(this.greenButton, new ConsLoButton(this.yellowButton, 
          new ConsLoButton(this.redButton, new ConsLoButton(this.yellowButton,
                  new ConsLoButton(this.blueButton, this.empty)))))));
  ILoButton sequence5 = new ConsLoButton(this.greenButton, new ConsLoButton(this.blueButton, 
      new ConsLoButton(this.redButton, new ConsLoButton(this.yellowButton, 
          new ConsLoButton(this.greenButton, this.empty)))));
  ILoButton sequence6 = new ConsLoButton(this.yellowButton, new ConsLoButton(this.blueButton, 
      new ConsLoButton(this.redButton, new ConsLoButton(this.yellowButton, 
          new ConsLoButton(this.greenButton, new ConsLoButton(this.redButton, this.empty))))));

  // random seed for testing
  Random seed = new Random(4);

  // examples of worlds
  SimonWorld emptyWorld = new SimonWorld();
  SimonWorld world1 = new SimonWorld(this.empty, this.sequence1, this.sequence1, "", this.seed);
  SimonWorld world2 = new SimonWorld(this.empty, this.sequence2, this.sequence2, "", this.seed);
  SimonWorld world3 = new SimonWorld(this.empty, this.sequence3, this.sequence3, "", this.seed);
  SimonWorld world4 = new SimonWorld(this.empty, this.sequence4, this.sequence4, "", this.seed);
  SimonWorld world5 = new SimonWorld(this.empty, 
      this.diffSequence, this.diffSequence, "", this.seed);
  SimonWorld greenWorldClick = 
      new SimonWorld(this.empty, this.sequence4, this.sequence4, "Green", this.seed);
  SimonWorld incorrectWorld1 = 
      new SimonWorld(this.sequence2, this.sequence1, this.sequence1, "", this.seed);
  SimonWorld randomWorld = new SimonWorld(this.empty, this.sequence1, this.sequence1, "");
  
  // examples of posns
  Posn greenPos = new Posn(100, 100);
  Posn redPos = new Posn(400, 100);
  Posn bluePos = new Posn(400, 400);
  Posn yellowPos = new Posn(100, 400);
  
  // examples of world scenes and world images
  WorldScene background = new WorldScene(500, 500);
  WorldImage topSquares = new BesideImage(this.greenButton.drawDark(), 
      this.redButton.drawDark());
  WorldImage bottomSquares = new BesideImage(this.yellowButton.drawDark(),
      this.blueButton.drawDark());
  WorldImage combineSquares = new AboveImage(this.topSquares, this.bottomSquares);
  WorldScene shapesToBackground = this.background.placeImageXY(this.combineSquares, 250, 250);

  // test the method makeScene
  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.world1.makeScene(), new WorldScene(500, 500).placeImageXY(
        new AboveImage(new BesideImage(this.greenButton.drawDark(), 
            this.redButton.drawDark()), 
            new BesideImage(this.yellowButton.drawDark(),
                this.blueButton.drawDark())), 250, 250)
        .placeImageXY(this.redButton.drawLit(), 375, 125))
        && t.checkExpect(this.greenWorldClick.makeScene(), new WorldScene(500, 500).placeImageXY(
            new AboveImage(new BesideImage(this.greenButton.drawDark(), 
                this.redButton.drawDark()), 
                new BesideImage(this.yellowButton.drawDark(),
                    this.blueButton.drawDark())), 250, 250)
            .placeImageXY(this.redButton.drawLit(), 375, 125)
            .placeImageXY(this.greenButton.drawLit(), 125, 125));
  }
  
  // test the method onTick
  boolean testOnTick(Tester t) {
    return t.checkExpect(this.world1.onTick(), 
        new SimonWorld(this.empty, this.sequence1, this.empty, "", this.seed))
        && t.checkExpect(this.greenWorldClick.onTick(), 
            new SimonWorld(this.empty, this.sequence4, this.sequence4Minus1, "Green", this.seed))
        && t.checkExpect(this.incorrectWorld1.onTick(), 
            this.incorrectWorld1.endOfWorld("You Lose :( Try again!"));
  }
  
  // test the method lastScene
  boolean testLastScene(Tester t) {
    return t.checkExpect(this.world1.lastScene("You lose :( Try again!"),
    new WorldScene(500, 500).placeImageXY(new TextImage(1
        + " Buttons", 40, Color.black), 250, 350).placeImageXY(
            new TextImage("You lose :( Try again!", 40, Color.black), 250, 250));
  }
  
  // test the method onMousePressed
  boolean testOnMousePressed(Tester t) {
    return t.checkExpect(this.world1.onMousePressed(this.greenPos), 
        new SimonWorld(this.empty, this.sequence1, 
        this.sequence1, "Green", this.seed))
        && t.checkExpect(this.world1.onMousePressed(this.redPos), 
            new SimonWorld(this.empty, this.sequence1, 
                this.sequence1, "Red", this.seed))
        && t.checkExpect(this.world1.onMousePressed(this.bluePos), 
            new SimonWorld(this.empty, this.sequence1, 
                this.sequence1, "Blue", this.seed))
        && t.checkExpect(this.world1.onMousePressed(this.yellowPos), 
            new SimonWorld(this.empty, this.sequence1, 
                this.sequence1, "Yellow", this.seed));
  }
  
  // test the method onMouseReleased
  boolean testOnMouseReleased(Tester t) {
    return t.checkExpect(this.world1.onMouseReleased(this.greenPos), 
        new SimonWorld(new ConsLoButton(this.greenButton, this.empty), this.sequence1, 
        this.sequence1, "", this.seed))
        && t.checkExpect(this.world1.onMouseReleased(this.redPos), 
            new SimonWorld(new ConsLoButton(this.redButton, this.empty), this.sequence1, 
                this.sequence1, "", this.seed))
        && t.checkExpect(this.world1.onMouseReleased(this.bluePos), 
            new SimonWorld(new ConsLoButton(this.blueButton, this.empty), this.sequence1, 
                this.sequence1, "", this.seed))
        && t.checkExpect(this.world1.onMouseReleased(this.yellowPos), 
            new SimonWorld(new ConsLoButton(this.yellowButton, this.empty), this.sequence1, 
                this.sequence1, "", this.seed));
  }
  
  // test the method randomButton
  boolean testRandomButton(Tester t) {
    return t.checkExpect(this.world1.randomButton(), new Button(Color.yellow, 250, 250))
        && t.checkExpect(this.greenWorldClick.randomButton(), new Button(Color.blue, 250, 250))
        && t.checkExpect(this.incorrectWorld1.randomButton(), new Button(Color.blue, 250, 250))
        && t.checkExpect(this.world2.randomButton(), new Button(Color.blue, 250, 250))
        && t.checkExpect(this.world3.randomButton(), new Button(Color.blue, 250, 250))
        && t.checkExpect(this.world4.randomButton(), new Button(Color.green, 250, 250))
        && t.checkExpect(this.world5.randomButton(), new Button(Color.yellow, 250, 250));
  }
  
  // test the method insert
  boolean testInsert(Tester t) {
    return t.checkExpect(this.empty.insert(this.greenButton), 
        new ConsLoButton(this.greenButton, this.empty))
        && t.checkExpect(this.sequence1.insert(this.greenButton), 
            new ConsLoButton(this.redButton, new ConsLoButton(this.greenButton, this.empty)))
        && t.checkExpect(this.sequence2.insert(this.blueButton), new ConsLoButton(this.redButton, 
            new ConsLoButton(this.blueButton, new ConsLoButton(this.blueButton, this.empty))))
        && t.checkExpect(this.sequence2Diff.insert(this.redButton), 
            new ConsLoButton(this.redButton, 
                new ConsLoButton(this.greenButton, new ConsLoButton(this.redButton, this.empty))))
        && t.checkExpect(this.sequence3.insert(this.greenButton), new ConsLoButton(this.redButton, 
            new ConsLoButton(this.blueButton, new ConsLoButton(this.greenButton, 
                new ConsLoButton(this.greenButton, this.empty)))))
        && t.checkExpect(this.sequence4.insert(this.redButton), 
            new ConsLoButton(this.redButton, new ConsLoButton(this.blueButton, 
                new ConsLoButton(this.greenButton, new ConsLoButton(this.yellowButton, 
                    new ConsLoButton(this.redButton, this.empty))))))
        && t.checkExpect(this.sequence4Minus1.insert(this.yellowButton), 
            new ConsLoButton(this.blueButton, 
                new ConsLoButton(this.greenButton, 
                    new ConsLoButton(this.yellowButton, 
                        new ConsLoButton(this.yellowButton, this.empty)))));
  }

  // test the method length
  boolean testLength(Tester t) {
    return t.checkExpect(this.sequence1.length(), 1)
        && t.checkExpect(this.sequence2.length(), 2)
        && t.checkExpect(this.sequence2Diff.length(), 2)
        && t.checkExpect(this.sequence3.length(), 3)
        && t.checkExpect(this.sequence4.length(), 4)
        && t.checkExpect(this.sequence4Minus1.length(), 3);
  }

  // test the method remove
  boolean testRemove(Tester t) {
    return t.checkExpect(this.sequence4.remove(), new ConsLoButton(this.blueButton, 
        new ConsLoButton(this.greenButton, new ConsLoButton(this.yellowButton, this.empty))))
        && t.checkExpect(this.sequence3.remove(), 
            new ConsLoButton(this.blueButton, new ConsLoButton(this.greenButton, this.empty)))
        && t.checkExpect(this.sequence2.remove(), new ConsLoButton(this.blueButton, this.empty))
        && t.checkExpect(this.sequence1.remove(), this.empty)
        && t.checkExpect(this.empty.remove(), this.empty);
  }

  // test the method buttonIndex
  boolean testButtonIndex(Tester t) {
    return t.checkExpect(this.sequence1.buttonIndex(1, 1), this.redButton)
        && t.checkExpect(this.sequence4.buttonIndex(1, 1), this.redButton)
        && t.checkExpect(this.sequence4.buttonIndex(this.sequence4.length(), 1), 
            this.yellowButton)
        && t.checkExpect(this.sequence4.buttonIndex(2, 1), this.blueButton)
        && t.checkExpect(this.sequence4.buttonIndex(3, 1), this.greenButton)
        && t.checkExpect(this.sequence4.buttonIndex(4, 1), this.yellowButton)
        && t.checkExpect(this.sequence4.buttonIndex(5, 1), this.grayButton)
        && t.checkExpect(this.sequence4.buttonIndex(6, 1), this.grayButton)
        && t.checkExpect(this.empty.buttonIndex(1, 1), this.grayButton)
        && t.checkExpect(this.empty.buttonIndex(10, 1), this.grayButton);
  }

  // test the method startsWith
  boolean testStartsWith(Tester t) {
    return t.checkExpect(this.sequence4.startsWith(this.sequence1), true)
        && t.checkExpect(this.sequence2.startsWith(this.sequence1), true)
        && t.checkExpect(this.sequence4.startsWith(this.sequence2), true)
        && t.checkExpect(this.sequence2.startsWith(this.sequence4), false)
        && t.checkExpect(this.sequence4.startsWith(this.sequence4), true)
        && t.checkExpect(this.sequence3.startsWith(this.sequence3), true)
        && t.checkExpect(this.sequence2.startsWith(this.sequence2Diff), false)
        && t.checkExpect(this.empty.startsWith(this.sequence2Diff), false)
        && t.checkExpect(this.empty.startsWith(this.empty), true);
  }
  
  // test the method flashSequence
  boolean testFlashSequence(Tester t) {
    return t.checkExpect(this.sequence1.flashSequence(this.shapesToBackground), 
        this.shapesToBackground.placeImageXY(this.redButton.drawLit(), 375, 125))
        && t.checkExpect(this.diffSequence.flashSequence(this.shapesToBackground), 
            this.shapesToBackground.placeImageXY(this.blueButton.drawLit(), 375, 375))
        && t.checkExpect(this.sequence4.flashSequence(this.shapesToBackground), 
            this.shapesToBackground.placeImageXY(this.redButton.drawLit(), 375, 125))
        && t.checkExpect(this.sequence4Minus1.flashSequence(this.shapesToBackground), 
            this.shapesToBackground.placeImageXY(this.blueButton.drawLit(), 375, 375))
        && t.checkExpect(this.sequence5.flashSequence(this.shapesToBackground), 
            this.shapesToBackground.placeImageXY(this.greenButton.drawLit(), 125, 125))
        && t.checkExpect(this.sequence6.flashSequence(this.shapesToBackground), 
            this.shapesToBackground.placeImageXY(this.yellowButton.drawLit(), 125, 375));
  }

  // test the method unFlash
  boolean testUnflash(Tester t) {
    return t.checkExpect(this.sequence1.unflash(), new ConsLoButton(this.grayButton, 
        new ConsLoButton(this.redButton, this.empty)))
        && t.checkExpect(this.sequence2.unflash(), new ConsLoButton(this.grayButton, 
        new ConsLoButton(this.redButton, new ConsLoButton(this.grayButton, 
            new ConsLoButton(this.blueButton, this.empty)))))
        && t.checkExpect(this.sequence3.unflash(), new ConsLoButton(this.grayButton, 
            new ConsLoButton(this.redButton, new ConsLoButton(this.grayButton,
            new ConsLoButton(this.blueButton, new ConsLoButton(this.grayButton, 
                new ConsLoButton(this.greenButton, this.empty)))))))
        && t.checkExpect(this.sequence4.unflash(), new ConsLoButton(this.grayButton, 
            new ConsLoButton(this.redButton, new ConsLoButton(this.grayButton, 
                new ConsLoButton(this.blueButton, new ConsLoButton(this.grayButton,
            new ConsLoButton(this.greenButton, new ConsLoButton(this.grayButton, 
                new ConsLoButton(this.yellowButton, this.empty)))))))));
  }

  // test the method drawButton
  boolean testDrawButton(Tester t) {
    return t.checkExpect(this.redButton.drawButton(Color.red), 
        new RectangleImage(250, 250, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.greenButton.drawButton(Color.green), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.green))
        && t.checkExpect(this.yellowButton.drawButton(Color.yellow), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.yellow))
        && t.checkExpect(this.blueButton.drawButton(Color.blue), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.blue))
        && t.checkExpect(this.blueButton.drawButton(Color.yellow), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.yellow));
  }

  // test the method drawDark
  boolean testDrawDark(Tester t) {
    return t.checkExpect(this.redButton.drawDark(), 
        new RectangleImage(250, 250, OutlineMode.SOLID, Color.red.darker().darker()))
        && t.checkExpect(this.greenButton.drawDark(), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.green.darker().darker()))
        && t.checkExpect(this.yellowButton.drawDark(), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.yellow.darker().darker()))
        && t.checkExpect(this.blueButton.drawDark(), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.blue.darker().darker()));
  }

  // test the method drawLit
  boolean testDrawLit(Tester t) {
    return t.checkExpect(this.redButton.drawLit(), 
        new RectangleImage(250, 250, OutlineMode.SOLID, Color.red.brighter().brighter()))
        && t.checkExpect(this.greenButton.drawLit(), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.green.brighter().brighter()))
        && t.checkExpect(this.yellowButton.drawLit(), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.yellow.brighter().brighter()))
        && t.checkExpect(this.blueButton.drawLit(), 
            new RectangleImage(250, 250, OutlineMode.SOLID, Color.blue.brighter().brighter()));
  }
  
  // test the method sameButton
  boolean testSameButton(Tester t) {
    return t.checkExpect(this.greenButton.sameButton(this.blueButton), false)
        && t.checkExpect(this.greenButton.sameButton(this.greenButton), true)
        && t.checkExpect(this.redButton.sameButton(this.greenButton), false)
        && t.checkExpect(this.redButton.sameButton(new Button(Color.red, 250, 250)), true)
        && t.checkExpect(this.blueButton.sameButton(this.blueButton), true)
        && t.checkExpect(this.yellowButton.sameButton(this.yellowButton), true)
        && t.checkExpect(this.greenButton.sameButton(new Button(Color.green, 250, 100)), false)
        && t.checkExpect(this.greenButton.sameButton(new Button(Color.green, 100, 250)), false);
  }
  
  // test the method sameColor
  boolean testSameColor(Tester t) {
    return t.checkExpect(this.greenButton.sameColor(Color.green), true)
        && t.checkExpect(this.redButton.sameColor(Color.red), true)
        && t.checkExpect(this.yellowButton.sameColor(Color.yellow), true)
        && t.checkExpect(this.blueButton.sameColor(Color.blue), true)
        && t.checkExpect(this.greenButton.sameColor(Color.red), false)
        && t.checkExpect(this.blueButton.sameColor(Color.yellow), false)
        && t.checkExpect(this.redButton.sameColor(Color.green), false);
  }

  // runs the game by creating a world and calling bigBang
  boolean testSimonSays(Tester t) {
    SimonWorld starterWorld = new SimonWorld();
    int sceneSize = 500;
    return starterWorld.bigBang(sceneSize, sceneSize, 0.8);
  }
}