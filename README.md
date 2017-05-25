# BigIdeaInventory
Inventory app for the Big Idea Bookstore

To get this to run:

1 - Clone this project

2 - Create a symlink named jniLibs in app/src/main that points to [location of this repo]/OpenCV-android-sdk/sdk/native/libs

3 - Make a copy of default.CMakeLists.txt and rename it CMakeLists.txt

4 - Change line 9 in CMakeLists.txt so that [yourPathToOpenCV] is the directory where you cloned the repo

5 - Open this project in Android Studio

6 - Install any dependencies that are missing

7 - A warning about missing/unused dependencies might pop-up make sure to pick the option to keep them

8 - Profit?
