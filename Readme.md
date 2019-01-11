# Fratcher #

Fratcher (Friend Matcher) is a demo application of a dating portal that is based on the Tinder principle. The technologies used include React for the frontend and Java Spring Boot for the backend. 

## Struture ##
![Class Diagramm](/doc/fratcher_class_diagramm.png)

## Manual ##

### Test Users ###
This project has 200 test users. The usernames are numbered from user1 to user200.  
The password for the demo user is always: "password + the ID from the username + 2".

Example: user5 | password7

The test users have already randomly rated each other (like/dislike). The best way to provoke a Match is to press "Like" all the time on all users and to switch between test users sometimes.  

### Login as Random User ###
If you click on "Forgot Password" on the login page, the access data for a random test user will be entered.

## ToDo ##
- [ ] Make API more secure
    * The API still has a few vulnerabilities where users can access information that they are not allowed to see.  
- [ ] Add automatic reload for more candidates
- [ ] Implement working picture upload
    * For the moment all UserAvatars are static
- [ ] Create possibility to edit interest tag cloud
    * For the moment all interest tags are static
- [ ] Create possibility to change user language in edit profile page
    * For the moment you cannot change the language of the user on frontend
    * The react script for internationalization is integrated and works fine
    * Some strings in the frontend are already dynamically loaded from the locales files
- [ ] Add timestamp for chat messages
    * Timestamp is transmitted but not displayed
- [ ] Cleanup frontend code and CSS styles
    * But I'm not a frontend developer 

    
## Known Issues ##
* On the registration page you have to go out (remove focus) of the last input field to enable the register button.
    * I know what's wrong with it by now, but had no time to fix it. Javascript validation sucks.

## Credits ##

* Demo data
    * About Me Descriptions
        * Dating Ipsum by Lauren Hallden
            * http://laurenhallden.com/datingipsum/#
        * User Images
            * https://randomuser.me/photos
        * First and last name
            * http://migano.de/testdaten.php?preview=1            
* Chat Layout
    * https://bootsnipp.com/snippets/featured/simple-chat
* Login Page
    * https://codepen.io/OldManJava/pen/bwupj
* Registration Page
    * https://bootsnipp.com/snippets/featured/register-page
* Tag-Cloud Component
    * https://github.com/madox2/react-tagcloud
 * User Avatars
    * React Component
        *  https://www.npmjs.com/package/react-avatar
