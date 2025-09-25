
# 🦷 dentTechEdu: Revolutionizing Dental Prosthesis Training

> Empowering dental professionals with cutting-edge tools for learning and practicing prosthodontics.
<p align="center">
  <img src="https://img.shields.io/badge/React%20Native-0.70.8-blue?logo=react" alt="React Native"/>
  <img src="https://img.shields.io/badge/Thymeleaf-3.0-red?logo=thymeleaf" alt="Thymeleaf"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.0-green?logo=spring" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Docker-24-blue?logo=docker" alt="Docker"/>
  <img src="https://img.shields.io/badge/Java-17-orange?logo=java" alt="Java"/>
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License"/>
</p>

## 📑 Table of Contents
- [Features](#-features)
- [Requirements](#-requirements)
- [Tech Stack](#-tech-stack)
- [Installation](#-installation)
- [Demo Video](#-demo-video)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features
- 📊 Dashboards & analytics: Personalized views and progress curves for professors and students.
- 👩‍🏫 Professors: Manage groups, students, teeth, preparations, and validate practical works.
- 🎓 Students: Submit, track, and review prosthesis practical works with real-time progress tracking.
- 🦷 Dental simulations: Capture and process dental images with OpenCV for taper angles and line detection.
- 👤 Role-based access: Secure login for administrators, professors, and students.
- 🛡️ Admin: Manage professor accounts with full CRUD operations.
- 🌐 Cross-platform: Web and Mobile access.

## 📋 Requirements

- **Git** (to clone the repository)  
- **Node.js >= 16** and **npm / yarn** (for the mobile app)  
- **Expo Go app** (to run the mobile app on a physical device)  
- **Java JDK 11+** (for the backend)  
- **Docker & Docker Compose** (to run the backend + MySQL containerized)



## 🏗️ Tech Stack
- **Mobile**: React Native, JavaScript  
- **Web**: Thymeleaf, Spring Boot, Spring Security  
- **Database**: MySQL  
- **Others**: HTML, Docker  

## 🚀 Installation

### 📱 Mobile Setup
1. Clone the repo and go to `mobile` folder  
2. Install dependencies:
   ```bash
   npm install
3. Start app on Android:
   ```bash
   npx react-native start --reset-cache
4. Update `config.js` with your IP address
5. Run emulator from Android Studio


### 💻 Web Setup
1. Pull backend image:
   ```bash
   docker pull mohjamoutawadii1164/pfa2024:backend
2. Create network:
   ```bash
   docker network create mohja
3. Start MySQL & phpMyAdmin:
   ```bash
   docker run --name mysql-container --network mohja -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=dents -d mysql:latest
   docker run --name phpmyadmin-container --network mohja -d --link mysql-container:db -p 8084:80 phpmyadmin
4. Start backend:
   ```bash
   docker run --name backend-container --net mohja -p 5050:5050 --link mysql-container:mysql \
   -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dents \
   -e SPRING_DATASOURCE_USERNAME=root \
   -e SPRING_DATASOURCE_PASSWORD=root \
   mohjamoutawadii1164/pfa2024:backend

5. Access app at http://localhost:5050/login
- Username: admin
- Password: 1234

## 🎬 Demo Video


https://github.com/user-attachments/assets/a79ae343-3ca3-48ff-ad78-1ec2011c834e



## 🗺️ Roadmap
- AI-powered assessments
- 3D prosthesis modeling integration
- Multi-language support
- Certificates of completion

## 🤝 Contributing
Contributions are welcome! Please open an issue or submit a pull request. Ensure your code adheres to the project's coding style and includes comprehensive tests.

## ⚖️ License
MIT License © 2024 Salma191 & Mohjamoutawadii & AyoubMechkour2020
