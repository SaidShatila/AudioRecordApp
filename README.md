# AudioRecordApp
## This sample was created to showcase my skills and the latest learnings in the Android Framework.
### Android Frameworks Used:
#### Used MVVM
#### Used ViewModels, Hilt, Preference DataStore.
#### Used Media Record to record the audio of the user.
#### Used Media Player to play the saved voice notes.
#### Kotlin-coroutines were used in order to fetch the saved Data.

## Architecture:
I am using the MVVM architecture and some state machine concept on top of it. Every screen has a view, a model, and a ViewModel. 
The ViewModel contains a state that represents the properties of the View.

The ViewModel state is represented using a simple kotlin data class with different fields.

## Helper Package or Utils Package:
I created a package named Utils, in order to add some functionalities in order to use them in my Main Activity 
such as Timer for the audio record time and Animation used for the button.

## Future Enhancments:
I will add an API in order to upload the saved files locally to a cloud or online storage, moreover try implement
Audio Recorder framework instead of Media Recorder, because it can provide me with some spooky customization's for 
a better quality sound.

## Video
Here is a small video demontstrating my mini app.

https://user-images.githubusercontent.com/57522137/153200997-3e2b2c14-e25d-47dd-a262-8bf33107c631.mp4



