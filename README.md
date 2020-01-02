FirstRate Currency (Demo App)
=========================
A currency conversion app for Android that allows you to choose from a list of currencies and compare the corresponding value to other countries. 

This is a demo application to demonstrate a design and an architectural approach, given a set of requirements.  

Introduction
------------
This is a one screen that shows a list of currencies, following a UI design that looks like this:

![UI Design Specification](screenshots/ui-design-specification.jpg "How the UI should look like")

The app downloads and updates currency rates every 1 second using an API.

All currencies from the endpoint are listed one per row. Each row has an input where you can enter any amount of money.
When you tap on currency row, it slides to the top of the list and its input takes focus.
When you’re changing the amount, the app simultaneously updates the corresponding value for other currencies.

Although a demo application, the solution is production ready.

Architectural Approach & Design
---------


Libraries Used
--------------


Non-Goals
---------
