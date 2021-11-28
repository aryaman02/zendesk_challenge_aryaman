Ticket Viewer Application

A Java application to view Zendesk tickets on the CLI

Installation And Usage Instructions:

Requirements:
        1. Java version: 1.8.0
        2. A command-line interface, like ITerm on a Mac

How to run the application on a Mac using a Command-line window (like Terminal or ITerm):
        1. Clone the repository into your local machine. As a reminder, the name of the repository is "zendesk_challenge_aryaman".
        2. By now, you should be able to see the sub-directory, which is titled "ticket_viewer". Note that this represents a Java Maven Project.

Using the Command-Line window, build the Maven project using the "mvn clean install" command or something equivalent. Note that you should do this
in the ticket_viewer directory.

After the build is successful, then you should see a "target" directory getting automatically created in the Maven project. On the command-line
window, go to the target directory.

After that, issue the following command to run the application:

    *** java -cp ticket_viewer-1.0-SNAPSHOT-jar-with-dependencies.jar net.aryaman.challenge.TicketViewerApp ***

Note that you should use the name of the jar file that was automatically created in the target directory (name should end with "with-dependencies")

