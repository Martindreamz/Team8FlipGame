# Team8FlipGame
Team 8 card flipping memory game

This is an Android game application.

## Application Structure
### Activities:
  - VideoSplash (launcher)
  - Home (menu)
  - imagePicking
  - game
  - leaderboard
  - credits
  
### Service 
  - DBService for score (SQLite)
  - BGMusicService (impl. binder & connected with all the actvities around)
  
### Models
  - image
  - score
  
### Adaptors
  - imageAdaptor
  - scoreAdaptor
  
### Media & Animation
  - color filter for imagePicking
  - filpview for game and music setting icon
  - video play as welcoming page
  - gif image in menu 

### Lifecylces & intents
  - email implicit intent
  - intent with flags for Game activity
  - finish() OnCreate OnResume OnPause OnStop etc.
  
### Other techiques
  - Notification
  - Toast Messages
  - Thread & UniThread
  
### External Libraries
  - EasyFlipView
