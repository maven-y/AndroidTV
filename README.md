# Android TV Application

## Overview
A modern Android TV application built with Kotlin and Jetpack Compose, featuring video playback and movie browsing capabilities.



## Demo Video

https://github.com/maven-y/AndroidTV/blob/main/Screen_recording_20250406_215706.mp4




## Screenshots



![Details Screen](https://github.com/maven-y/AndroidTV/blob/main/1.png)

![Details Screen](https://github.com/maven-y/AndroidTV/blob/main/2.png)



## Features
- Video playback with ExoPlayer
- TV-optimized UI with focus management
- Movie card grid layout
- Smooth animations and transitions
- Error handling and fallbacks

## Tech Stack
- Language: Kotlin
- UI Framework: Jetpack Compose
- Video Player: ExoPlayer
- Image Loading: Coil
- Architecture: MVVM with Compose

## Project Structure

![Details Screen](https://i.imgur.com/RkRkVLh.png)



## Key directories:

- ui/activity/ - Contains main activity classes (ExoPlayerActivity)
- ui/components/ - Contains reusable UI components (MovieCard)
- data/ - Contains data models (Movie)
- res/ - Contains resources (drawables, layouts, strings)


This structure follows Android best practices with clear separation of concerns:

- UI components in their own package
- Data models in a separate package
- Activities in a dedicated package
- Resources in the standard Android resource directory

## Requirements
- Android SDK 33 or higher
- Android TV emulator or device
- Kotlin 1.8+
- Gradle 7.0+


## Setup Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on Android TV emulator or device
