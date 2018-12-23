DConsole Android library
========================

<h2>Purpose</h2>

DConsole is an Android library to emulate a console interface allowing to seamlessly print text to a TextView.

<h2>Usage</h2>


1.	The application layout must define a TextView within a ScrollView
2.	The activity using the console must instantiate a DConsole object on onCreate
3.	The activity using the console must call DConsole.onResume and DConsole.onPause within its own onResume and onPause
4.	Printing is done with DConsole.print; the text is only displayed after a DConsole.display or DConsole.update call.
    DConsole.display renders the text immediately. DConsole.update delays the rendering and is faster if multiple calls to
    DConsole.print are made in rapid succession.

<h3>Key methods</h3>
* **onResume**. Must be called from the activity onResume
* **onPause**. Must be called from the activity onPause
*  **clear**. Clears the console
* **setMaxLines**. Sets the maximum number of lines in the console buffer (or unlimited)
* **print**. Prints a string (display is done in a second step with **display** or **update**  
* **display**. Display immediately the console content
* **update**. Delayed update of the console to speed-up rendering when multiple calls to print/update are made in rapid succession.

<h2>Example</h2>

This repository comprises a simple example.
