<p align="center">
<img src="art/github_preview.png" width="100%" />
</p> 

[![Build Status](https://app.bitrise.io/app/5d3a8885ec39a72b/status.svg?token=H8Sqp47-k-Ed-tkOkBFSSA&branch=development)](https://app.bitrise.io/app/5d3a8885ec39a72b)

**DeckBox** is an unofficial deck building app for creating and managing your Pokémon TCG decklists. Whether you are building and tweaking the top decks in the meta or just experimenting with some spicy rogue decks, search through a large collection of Pokémon cards that span from the latest expansion all the way back to the base set

Seamlessly integrate with your Pokémon TCG Online game by importing and exporting decklists in a compatible format.

Sign in with our Google account to build and edit your decks across all of your devices, or just continue without one and link it later.

### [Roadmap](https://github.com/r0adkll/DeckBox/projects/1) 
View the progress of upcoming and currently worked on features for this project.

<p align="center">
<a href="https://play.google.com/store/apps/details?id=com.r0adkll.deckbuilder"><img src="https://cdn.rawgit.com/steverichey/google-play-badge-svg/master/img/en_get.svg" width="300px" align="center"/>
</a>
</p>

## API
[![pokemontcg-developers on discord](https://img.shields.io/badge/discord-pokemontcg--developers-738bd7.svg)](https://discord.gg/dpsTCvg)  
DeckBox is powered by [pokemontcg.io](https://pokemontcg.io) 

**API:** [PokemonTCG/pokemon-tcg-api](https://github.com/PokemonTCG/pokemon-tcg-api)  
**SDK:** [PokemonTCG/pokemon-tcg-sdk-kotlin](https://github.com/PokemonTCG/pokemon-tcg-sdk-kotlin)  

To add/update card data to the API you must fork and make a pull request to this repository: [PokemonTCG/pokemon-tcg-data](https://github.com/PokemonTCG/pokemon-tcg-data)

## Architecture
DeckBox is **100%** written in [Kotlin](https://kotlinlang.org) and follows an **MVI** pattern for managing the UI and a repository pattern for managing the data/business layer. It is built on this stack:

* Dagger 2
* RxJava 2
* Firebase Firestore

## Design
<img src="art/sketch_icon.png" width="64px" />  

Check out the designs using [Sketch](https://www.sketchapp.com/)  

**Sketch Cloud**  
https://sketch.cloud/s/vgnyn

## Build-it Yourself
To build/run DeckBox your self you will need to configure and setup the following items:

#### [Firebase](https://firebase.google.com/)
Configure an Android application on your firebase project and download the `google-services.json` file into the `/app` folder.

Then run:
  
```groovy
./gradlew installDebug
```

## Contributing

Please follow the guidelines set forth in the [CONTRIBUTING](CONTRIBUTING.md) document.

<a href='https://ko-fi.com/D1D5KEED' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

## License

GNU General Public License v3.0

See [LICENSE](LICENSE) to see the full text.
