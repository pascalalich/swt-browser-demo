SWT Browser Demo
================
Intro
-----
This project is an Eclipse RCP sample application created for the *Eclipse DemoCamp Nov. 14th 2012*. 
It demonstrates the SWT Browser that allows integrating web browsers to a rich client application.
See http://wiki.eclipse.org/Eclipse_DemoCamps_November_2012/Hamburg for details.

Demos
-----
* Browser demo view that integrates the above mentioned wiki page
  * Restriction to browsing the wiki only
  * Additional link to start the demo, introduced via JavaScript
* Twitter widget view that integrates the author's Twitter timeline
* Integration with the HTML5, canvas-based drawing tool zwibbler.com
  * New file wizard
  * Files are stored in workspace
  * PNG can be downloaded

Let it run!
-----------
1. Install Eclipse 3.8 (Juno) for RCP Developers (I have not tested it with Eclipse 4)
1. Download this project *swt-browser-demo* to an arbitrary location
1. Import all projects into a new workspace
  1. File / Import / Plug-ins and Fragments
  2. Import from directory *swt-browser-demo*
  3. On next page of the import wizard, select all plugins found
  4. Press *Finish*
1. Make sure a Java 1.6 Runtime Environment is configured
1. Create a new launch configuration with the product 'org.eclipse.platform.ide' and all plug-ins
1. Run the launch configuration

Credits
-------
Pascal Alich, Software Engineer
