# Team8-FlipGame Android Project

## Table of Contents
- [Background](#background)
- [Platform](#platform)
- [Application Structure](#structure)
- [Contributors](#contributors)
- [License](#license)


## Background
This is an Android card flipping memory game application,finished in 4 days by SA50 Team 8 after 6-day course.

## Platform
We use Android studio and test via emulator API 28.

## Contributors

<a href="https://github.com/Martindreamz/T8LAPS/graphs/contributors">
  <img src="https://contributors-img.web.app/image?repo=Martindreamz/T8LAPS" />
</a>

Made with [contributors-img](https://contributors-img.web.app).


## Structure
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
  - gifDrawable
  
## License


