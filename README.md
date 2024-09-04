# Pokédex App - Explore and Learn About Pokémon

<p align="center">
  <img src="https://github.com/anubhav-auth/PokeDex/blob/master/assets/IMG_20240904_191727.jpg" alt="Pokédex App Logo" style="width: 50%; max-width: 200px; height: auto;">
</p>

## Overview

Welcome to the **Pokédex App**! This application allows users to explore and learn about various Pokémon, displaying detailed stats and images fetched from the PokéAPI. Whether you're a Pokémon enthusiast or just curious about the world of Pokémon, this app provides an engaging and informative experience.

## Features

### 🗃️ Fetching Data from PokéAPI
- Retrieves Pokémon names, stats, and images directly from the PokéAPI.
- Always stay updated with the latest Pokémon data.

### 📊 Displaying Pokémon Stats
- Shows various stats such as HP, Attack, Defense, Special Attack, Special Defense, Speed, and Base Experience.
- Provides a comprehensive view of each Pokémon's strengths and weaknesses.

### 🚀 Using Retrofit for API Calls
- Makes efficient API calls using Retrofit.
- Ensures smooth and fast data retrieval.

### 🖌️ Jetpack Compose for UI
- Utilizes Jetpack Compose for a modern, responsive UI.
- Enjoy a seamless and visually appealing user experience.

### 🖼️ Coil-Compose for Image Loading
- Loads and displays Pokémon images efficiently.
- Delivers high-quality images with minimal load times.

### 📐 Dynamic Layouts
- Adjusts layouts dynamically based on fetched data.
- Provides a customized viewing experience for each Pokémon.

### 🎉 Animated Progress Bars
- Progress bars for stats animate from 0 to their actual value.
- Visualize Pokémon stats in a fun and interactive way.

### 🔢 Stat Max Values
- Normalizes progress bars using known maximum values for stats.
- Ensures accurate representation of each stat's significance.

### 🎨 Dominant Color Background
- Displays the dominant color of each Pokémon as the background.
- Enhances the visual connection with each Pokémon's design.

## Getting Started

### 1. Install the App
- [Download the latest release](https://github.com/anubhav-auth/PokeDex/releases) from the releases section.

### 2. Browse Pokémon
- Browse through the list of Pokémon.
- Tap on a Pokémon to view detailed stats and image.
- The background color dynamically changes to the dominant color of the selected Pokémon.

## Built With

* [![Kotlin][Kotlin]][Kotlin-url]
* [![Jetpack Compose][JetpackCompose]][JetpackCompose-url]
* [![Retrofit][Retrofit]][Retrofit-url]
* [![Coil][Coil]][Coil-url]
* [![PokéAPI][PokeAPI]][PokeAPI-url]

## Demo

Check out a video demo of the app in action:

https://github.com/user-attachments/assets/fb066b4d-7370-44e8-9c1e-4b8a4f5aa63f


## Screenshots

| Pokémon List                                                           | Pokémon Stats                                                        | Pokemon Search                                            |
|------------------------------------------------------------------------|----------------------------------------------------------------------|------------------------------------------------------------------------|
| ![Pokémon List][Pokemon-List-Image]                                    | ![Pokémon Stats][Pokemon-Stats-Image]                                 | ![Pokemon Search][Pokemon-Search-Image]         |

## Contributing

We welcome contributions! Please fork the repository and submit a pull request.

## License

Pokédex App is licensed under the MIT License - see the [LICENSE](https://github.com/anubhav-auth/PokeDex/blob/master/LICENSE.txt) file for details.

## Acknowledgements

- Thanks to the [PokéAPI](https://pokeapi.co/) for providing the Pokémon data.
- Special thanks to the Android development community for their tutorials and open-source projects.

## Contact

For any inquiries or feedback, please reach out to [email](mailto:anubahvauth2002@gmail.com).

<!-- MARKDOWN LINKS & IMAGES -->
[Kotlin]: https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[JetpackCompose]: https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white
[JetpackCompose-url]: https://developer.android.com/jetpack/compose
[Retrofit]: https://img.shields.io/badge/Retrofit-3DDC84?style=for-the-badge&logo=retrofit&logoColor=white
[Retrofit-url]: https://square.github.io/retrofit/
[Coil]: https://img.shields.io/badge/Coil-5A67D8?style=for-the-badge&logo=coil&logoColor=white
[Coil-url]: https://coil-kt.github.io/coil/
[PokeAPI]: https://img.shields.io/badge/PokeAPI-FFCB05?style=for-the-badge&logo=pokemon&logoColor=white
[PokeAPI-url]: https://pokeapi.co/

[Pokemon-List-Image]: https://github.com/anubhav-auth/PokeDex/blob/master/assets/Screenshot_2024-09-04-18-47-17-724_com.example.pokedex.jpg
[Pokemon-Stats-Image]: https://github.com/anubhav-auth/PokeDex/blob/master/assets/Screenshot_2024-09-04-18-47-23-215_com.example.pokedex.jpg
[Pokemon-Search-Image]: https://github.com/anubhav-auth/PokeDex/blob/master/assets/Screenshot_2024-09-04-18-47-44-447_com.example.pokedex.jpg
