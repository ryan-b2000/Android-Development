# ECE 558 Project #2 -- Multiple choice quiz app
## <b>This assignment is worth 100 points.  Demos on Mon, 29-Oct and Tue 30-Oct.  Deliverables due to GitHub on Wed, 31-Oct by 10:00 PM.  </b>

### <i> We will be using GitHub classroom for this assignment.  You will do your development using the integrated support for Git and GitHub in Android Studio.  You should submit your assignment before the deadline by pushing your final Android Studio project and other deliverables to your GitHub repository for the assignment.</i>

## After completing this assignment students should have:
- Gained experience in developing apps with multiple activities
- Gained experience with file I/O and JSON parsing
- Practiced object oriented design and  programming
- Made successful use of the Git/GitHub support integrated into Android studio to develop their project under version control


## Links to Git and GitHub tutorials:

#### Tutorials on using the Android Studio integration w/ Git and GitHub:
- 	(11 min) https://www.youtube.com/watch?v=3p_fgJEbsp8
- Will W. Part 1 - Setup (5 min):  https://www.youtube.com/watch?v=AJune0EMP94
- Will W. Part 2 – Import and Push (15 min): https://www.youtube.com/watch?v=tjYQPsBba7s
-	Will W. Part 2.1 – Pull and Clone (8 min): https://www.youtube.com/watch?v=u84uKjY5S0g
- Will W. Part 3 – Create, delete, and switch branches (7 min): https://www.youtube.com/watch?v=OJoW0eaPpgM  
Will Willis also posted a video on merging using Android Studio and GitHub that you may find useful when you work on team projects.

#### Tutorials on using git and GitHub:
- Edureka (1hr, 45 min): https://www.youtube.com/watch?v=xuB1Id2Wxak
- TraversyMedia (32 min): https://www.youtube.com/watch?v=SWYqp7iY_Tc&t=96s


### Introduction

For the second programming project in ECE 558 you will create a multiple activity application using the Android SDK.  This project builds on what you learned by completing and enhancing the GeoQuiz application (see, I told you that it would be worth your effort to complete BNRG: Ch 1-5).  The app for this project is more complex than GeoQuiz but that’s to be expected, and it is significantly more difficult than Project #1. Android apps (like most GUI-based apps) trade programmer time for ease of use for the user.  

The deliverables for this project will be a demo for the T/A or instructor, a design report for the project, your Android Studio project and a signed executable (.apk file) of your final app.

We will be using GitHub classroom for this assignment.  This means that you will do your development in a local Git repository that will be linked to a private repository on GitHub that can also be accessed by the instructor and T/A for the course.  Fortunately, Android Studio and IntelliJ have excellent integration with version control systems such as Git.  Maybe not quite as fortunate, you need to have Git installed on the PC you are using for your coursework.

You will submit your work via a private repository using GitHub classroom.  Our plan is to review your work and provide feedback using GitHub classroom but this will be a new experience for us so we'll see how it goes.

NOTES:
-  If you decide to leverage (borrow) existing code in your app  than please acknowledge the source. We know how to use Google, too, so copying existing code from the web without acknowledging the source will lead to serious consequences if you are caught cheating.  Borrowing a few lines of code without acknowledgement is OK, but if you find yourself borrowing whole classes, etc. the source should be acknowledged.

- You may collaborate with your classmates in the design of the program but your are all expected to submit original work.  There is a difference between collaboration and copying - you don't want to be caught doing the latter since that could affect both your final grade and the grade of the student you copied from.

- Although there are 3rd party libraries that do the bulk of the work in parsing JSON and saving an entire object we do not want you to use them in this project.  Knowing how to parse and process JSON files is one of learning objectives for the project.

### Using GitHub classroom
We are planning to manage this assignment using GitHub Classroom.  GitHub classroom makes it straightforward for instructors to create and distribute assignments and provide feedback on your work.

<b>Note: These instructions worked for me (twice...I wanted to make sure) but all GitHub accounts are not created equally (different options, etc).  If you have a different experience please post the steps that worked for you on the Project #2 discussion forum on D2L </b>

To manage your project under GitHub classroom:
1. Install the Git tools (can be downloaded from https://git-scm.com/) on the PC your are using for the course and create a GitHub account if you do not already have one.
2. Log into your GitHub account, open a new window in your browser, and copy/paste the link for the assignment (link will be posted to D2L).  You will have to associate your GitHub account with GitHub classroom for the course (first time only) and accept the assignment.
3. Once you accept the assignment a private repository will be created for you and the contents of the assignment imported.  A message will pop up on your screen saying your assignment has been created and giving a link to our GitHub repository for the assignment.  You may also get an email with the same information.
4. Open Android studio and close all open projects.  This should bring up the "Welcome to Android Studio"
5. Connect Android Studio to your git installation and your GitHub account by selecting Configure/Settings/Version Control/Git and Settings/Version Control/GitHub. Click on OK.  There are several options to make the connection (SSH, Token, Password, etc.).  Pick the method that works best for you and test the connection. The videos should help with this.
6. Select Check out project from Version Control/GitHub.   For Git Repository URL select the URL that looks like this (only personalized for you) https://github.com/ECE558-fall2018/ece558proj2-profroyk.git.  Browse for the Parent directory and directory where you want to put the project.  Click on Clone.  This will clone the assignment on your local PC.  Once the clone is created click on No when Android Studio asks if you want to create an Android Studio project.  This will leave you at the "Welcome to Android Studio" screen.
7. Click on Start a New Android project and create the Android Studio project for your assignment.  You've done this before.  One suggestion (and it's only a suggestion) is to place your project into a subdirectory of your repository.  It's a bit cleaner because the assignment has other directories and files (docs, quizzes, README.md, ...).
8. Enable Version Control Integration by selecting VCS/Enable Version Control Integration/Git.
9. Select the Version Control tab at the bottom of the Android Studio screen and click on the Commit icon.  This will bring up a dialog saying that you have a bunch of non-versioned file (how true...you have not brought them under version control yet).  Select all of the files and click on Commit. Make your commit message "initial commit".  You will likely get a few code analysis warnings.  You can skip those for this round since you haven't added any code so select Commit.  You may also want to push your local repository to GitHub.  To do so select VCS/Git/Push.
10. Develop your code, making commits and pushing  your private repository to GitHub as appropriate. When you have finished the project, write your design report and include it in your repository.
11. Do a final push of your deliverables and Android Studio project to your GitHub repository for the project.

## Deliverables
Push your deliverables to your private GitHub repository for the assignment.  The repository should include:
- Your design report (.pdf preferred).
- JavaDoc output for your app, particularly for the key methods you created.  You do not need to document the Android callbacks or the standard Android “stuff”.
- A final version of your Android project. We will use this to grade your effort.  “Neatness counts” for this project - we will grade the quality of your code.  You code should be well structured, indented consistently (Android Studio helps with that) and you should include comments describing what long sections of your code do.    Comments should be descriptive rather than explain the obvious (ex:  //set a to b when the actual code says a = b; does not provide any value-added).
- A signed .apk (Android Application Package) file that can be downloaded and installed on an Android device.    You can generate an .apk file from Android Studio by selecting Build/Generate Signed APK.  It should be noted that using Android Studio to generate an .apk file will require that you “sign” your app with a keystore.  The Generate Signed APK wizard will help you through the process.

## List of files:
- docs/project2.pdf - The write-up for the project.
- docs/JSON Tutorial.pdf - A good JSON tutorial.  Read it unless you are already competent in creating and parsing JSON
- quizzes/quiz_template.json - An editable template that you can use to create quizzes
- quizzes/sample_quiz.json - A sample quiz in JSON format.
- README.md - This file
