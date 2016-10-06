# AddressBook-Android
Android based Address Book

# Deployment Instructions:
- Clone repository from Github
- Locate repository and open AddressBook project file
- Run the project with your Android device plugged in and allow Address Book to access yout contacts

#Summary:
After reading the project, I had a pretty good idea of what I was starting to build. Once the Repo and Project were set up, I needed to investigate RandomUser and how Android returns Phone Contacts. From there I found that I would use to use Volley to handle the Network call to RandomUser once the phone was done collecting the contacts actaully stored on it. 
I chose to make this in Java for Android because currently I'm more interested with working in android, however that's not to say I couldn't have done this also in swift or Objective-C for iOS.
This app loads the Users Contact list from their phone, it also then calls out and gets an additional 50 contacts from RandomUser. Once the contacts are all loaded, their name's are displayed in a RecyclerView within MainActivity. You can tell which contacts are real and which aren't by their background colour. When the User taps on a contact, some a new Fragment appears filled with some more contact info, and if the contact isn't fake, there is a button at the bottom which takes you to their Contact card, to allow for editing.

Given more time I would have completed adding the users images. I decided against adding this due to time restraints when I noticed that some of the images I was getting back from the Contact info, even though they provided a URI, ended up displaying a blank image due to the way the contact info is saved. I assume finding a solution to this would have only taken, at max, and including the compatability layer required to support the fake users, an extra 2 hours. With this feature I would have also added the images to the RecyclerView ListItems.
To make this more robust I can imagine having a button in the menu possibly to add a new contact, and have it actually write to your phone( 2-4 hrs ). Including more information from the contacts( dependant # of fields, roughly 20-40 per ) Also possibly the option to select how the list is sorted( 1hr ). Could also add the ability to edit a contact from inside the app (2-4hrs). Colour profiles( 2hrs ). 
