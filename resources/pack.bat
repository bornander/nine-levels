rem @echo off
cd %~dp0
java -jar runnable-texturepacker.jar raw\graphics\sprites graphics\sheets sprites
java -jar runnable-texturepacker.jar raw\graphics\controller graphics\sheets controller
java -jar runnable-texturepacker.jar raw\graphics\menu graphics\sheets menu
xcopy /S /Y /D graphics ..\android\assets\graphics
xcopy /S /Y /D audio ..\android\assets\audio
xcopy /S /Y /D levels ..\android\assets\levels
pause