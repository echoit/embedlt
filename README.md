Embeddable Apertium
===================

coding challenge for [apertium-j](http://wiki.apertium.org/wiki/Ideas_for_Google_Summer_of_Code/Make_lttoolbox-java_embeddable)

How to test on Android
----------------------

- Install the apk in any android device (2.3.3+)
- Put the language pair into /sdcard/forapertium/ (e.g., for simulator [$ adb push apertium-eo-en/ /sdcard/forapertium/] or directly copy to your real SD-card in android phone)
- Then enjoy it ;)
+![screenshot](http://flic.kr/p/bG6cKk)

HOWTO
---

- Make all of the files in src/org/apertium/embed/ integrated into the lttoolbox-java project
- Modify Apertium's DATA\_DRECTORY to adapt your pair's path
- Run class EmbeddableApertium with main method
- You can change something in the eotest.input

