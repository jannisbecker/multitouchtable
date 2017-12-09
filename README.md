# Multitouch Table
An interactive homemade implementation of multitouch hardware,
including softwareside image processing and calibration in OpenCV/jogl, 
and a simple pool game in OpenGL.

## Introduction
This project was done with 4 people, Daniel Bannert, Thomas Skowronek, Raffael Balthasar and me, Jannis Becker
 in the 3rd semester of "Visual Computing and Design" at the Hochschule Hamm-Lippstadt in Germany.
The main task was to combine computer vision with computer graphics, thus any project that uses image processing prior
to show something to the user with 2D/3D computer graphics algorithms.

## Required tech
For this code to work, you need the following soft- and hardware assets:
* A modified PS3 eye camera with the IR filter removed. As OpenCV does work with most webcams as well, you might get it to work with other cameras. A PS3 eye camera is recommended though, due to very high possible framerates.
* A TFT monitor of any size, where it is possible to seperate the display unit and the backlight. Look at the documentation for more info about our own hardware implementation.
* An acrylic "EndlightenT" plate, which is able to spread side induced light evenly to the top and bottom surfaces.
* Infrared LED strips, enough to cover all 4 sides of the acrylic plate.
* OpenCV 2.4.11, JoGL 2.3.2 and Vecmath 1.5.2 are required libraries. You can find an prebuilt fat-jar included in this repo, which already includes these libraries. OpenCV has to be installed nonetheless.

## Folder structure

* **source**
  * **src:** Complete java source code including both image processing and computer graphics parts of the 
project
  * **bin:** A prebuilt x86 jarfile of the source, which includes the required libraries for the most part (see 
above)
  * **doc:** Java doc of the source
* **media**
  * **productvideo.mp4:** A short video demo of the finished table, the available touch interactions and a bit of gameplay
* **docs**
  * **documentation.pdf:** The complete project documentation, with lots of images, code snippets and plans/designs of the project
  * **presentation.pdf:** Our short final presentation slide
