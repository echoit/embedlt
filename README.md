Embeddable Apertium
===================

coding challenge for [apertium-j](http://wiki.apertium.org/wiki/Ideas_for_Google_Summer_of_Code/Make_lttoolbox-java_embeddable)

How to test on Android
----------------------

- now only support eo-en
- Install the apk in any android device (2.3.3+)
- Put the language pair into /sdcard/forapertium/ (e.g., for simulator [$ adb push apertium-eo-en/ /sdcard/forapertium/] or directly copy to your real SD-card in android phone)
- Then enjoy it ;)

![Android Apertium](http://farm8.staticflickr.com/7080/7017242597_57a664d6a1.jpg "android apertium")

HOWTO on PC
-----

- Make all of the files in src/org/apertium/embed/ integrated into the lttoolbox-java project
- Modify Apertium's DATA\_DRECTORY to adapt your pair's path
- Run class EmbeddableApertium with main method
- You can change something in the eotest.input

