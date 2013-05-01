# Fountain for Java

A Java parser for [fountain](http://fountain.io/syntax) based off (plagiarizing perhaps?) of [nyousefi/Fountain](https://github.com/nyousefi/Fountain).

# Fountain

[Fountain](http://fountain.io) is a simple plain text formatting syntax for screenplays.  The syntax is simple and human readable.  If you want to write in Notepad or [SublimeText](http://sublimetext.com) you can!  Whatever floats your boat.

The [syntax](http://fountain.io/syntax) is very similar to [markdown](http://daringfireball.net/projects/markdown).  Fountain removes a lot of the complexity of markdown, and keeps things simple.  If you ever tried writing a screenplay using plaintext before, more than likely a large majority of it winds up being valid fountain syntax.

## Fountain for Java

Fountain for java is a port of the [Objective-C implementation](https://github.com/nyousefi/Fountain) to Java.  Why Java?  Well... we'll have to answer that later...

### Components

#### FNTypes

An enum that contains valid types based off of the syntax of [Fountain](http://fountain.io/syntax).  Used by FNElement to identify the type of element.

#### FNElement

The data model for storing script elements.

#### FNParser

A line-by-line parser for Fountain strings and files.  Stores the script in a List of FNElements and the title page as a Map.

### To Do

* **FNScriptToHTML** - Will convert a Fountain string or file to an HTML file.
* **FNWriter** - Will output a List of FNElements and a Map representing the Title Page to a valid Fountain file.
* **FNScript** - Object that holds the script content.  Makes it easy to read and write fountain files (instead of having to interact with everything).

## Installation and Usage

Download our jar or source (someday...) and add it to your Java project path.  Import com.thp.fountain.*;  Be happy.

# Credits

* Code is stolen and converted to Java from [nyousefi/Fountain](https://github.com/nyousefi/Fountain)
* Fountain is the brain child of [John August](http://johnaugust.com/about), [Stu Maschwitz](http://prolost.com/about/), [Nima Yousefi](http://nimayousefi.com/about/), as well as [a bunch of other people](http://fountain.io)

# Links

Want to discuss and contribute to the syntax of fountain?  Join the [Glassboard](http://www.candlerblog.com/2012/12/06/fountain-glassboard/).

Other useful tools for fountain?  Check out the [apps](http://fountain.io/apps).

Need to convert from Fountain to Final Draft?  Checkout [screenplain](http://www.screenplain.com).
