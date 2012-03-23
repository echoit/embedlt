Embeddable Apertium
---

coding challenge for [apertium-j](http://wiki.apertium.org/wiki/Ideas_for_Google_Summer_of_Code/Make_lttoolbox-java_embeddable)

HOWTO
---

- Make all of the files in src/org/apertium/embed/ integrated into the lttoolbox-java project
- Modify Apertium's DATA\_DRECTORY to adapt your pair's path
- Run class EmbeddableApertium with main method
- You can change something in the eotest.input


What does it do?
---

This challenge is your chance to understand the project better and for the GSoC mentors to get an impression of you.

- Download and compile a language pair which only uses core Apertium blocks.

I.e., no HFST or Contraint grammar. Choose for example the eo-en or es-en language pair.
You will need to do a minimal installation from SVN of the C++ version to compile the language pair. This is most easily done on Ubunut Linux.
If you can't do this step for some reason you can download a compiled pair [here](http://javabog.dk/filer/apertium-eo-en.tar.gz) (at this case, you should manually modify some paths in both eo-en.mode and en-eo.mode then copy them to modes/ directory)

- Get to know the command line workings of the C++ version. Try for example:

echo "Saluton, mi estas Jacob" | apertium -d apertium-eo-en/ eo-en

- Compile Lttoolbox-java and install it and try to use the Java port to translate some sentences.

echo "Saluton, mi estas Jacob" | apertium-j -d apertium-eo-en/ eo-en
Notice that C++ version is apertium. Java version is apertium-j.
Look at apertium-j. It is a Unix shell script. Try to invoke the JAR file directly without the shell script:
echo "Saluton, mi estas Jacob" | java -jar /usr/local/share/apertium/lttoolbox.jar apertium-j -d apertium-eo-en/ eo-en

- Have a look at CommandLineInterface.java and make your own class with a main() method that translates just your pair.

Also make a method that just exercises the system, ie translates something predefined (ie not from standard input) and show the result

- Throw it into an Android project and see if you can get your method that translates something predefined to work

- Publish your work for further examination. For example to Subversion or just as a plain ZIP file somewhere.

Try if you can avoid modifying lttoolbox-java. In that case you can just show your own files.

