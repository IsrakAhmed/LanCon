# LanCon

## Software Requirements Specification (SRS)

LanCon - Local Area Network Communication Application

### Table of Contents
1. Introduction
    1. Purpose
    2. Scope
    3. Definitions, Acronyms, and Abbreviations
    4. References
    5. Overview
2. Overall Description
    1. Product Perspective
    2. Product Functions
    3. User Classes and Characteristics
    4. Operating Environment
    5. Design and Implementation Constraints
    6. Assumptions and Dependencies
3. Specific Requirements
    1. Functional Requirements
    2. Non-functional Requirements
    3. External Interface Requirements
    4. System Features
4. Other Requirements
5. Developer Details

---

## 1. Introduction

### 1.1 Purpose

The purpose of this Software Requirements Specification (SRS) document is to provide a comprehensive description of the requirements for the LanCon application. It will detail the application's purpose, features, and specifications.

### 1.2 Scope

LanCon is an Android application designed for communication over a Local Area Network (LAN) without the need for internet access. The app includes features such as messaging, audio and video calling, file sharing, and device discovery. It supports 1-to-1 and group interactions.

### 1.3 Definitions, Acronyms, and Abbreviations

- **LAN**: Local Area Network
- **UI**: User Interface
- **SRS**: Software Requirements Specification
- **SQLite**: Structured Query Language, a C-language library that implements a small, fast, self-contained, high-reliability, full-featured, SQL database engine.

### 1.4 References

1. Android Developer Documentation
2. SQLite Documentation
3. WebRTC Documentation
4. Network Service Discovery (NSD) Documentation

### 1.5 Overview

This SRS document provides an overview of the LanCon application, its functionalities, requirements, and constraints. It is intended for developers, testers, and stakeholders involved in the project.

---

## 2. Overall Description

### 2.1 Product Perspective

LanCon is a standalone mobile application for Android devices. It leverages local network connectivity to provide communication services without requiring internet access.

### 2.2 Product Functions

1. **Messaging**: Send and receive text messages.
2. **Audio and Video Calling**: Conduct voice and video calls.
3. **File Sharing**: Share files within the local network.
4. **Device Discovery**: Discover other devices on the same network.
5. **1-to-1 and Group Interactions**: Support for individual and group communications.

### 2.3 User Classes and Characteristics

The primary users of LanCon are individuals connected to the same local network who require efficient communication and file sharing. Users should have basic knowledge of using mobile applications.

### 2.4 Operating Environment

The application will run on Android devices with version 5.0 (Lollipop) and above. It requires access to local network services and permissions for camera, microphone, and storage.

### 2.5 Design and Implementation Constraints

1. The application must function without internet access.
2. All communications and file transfers should be limited to the local network.
3. The UI must adhere to Material Design guidelines.

### 2.6 Assumptions and Dependencies

1. Users will be connected to the same local network.
2. Devices will have the necessary hardware capabilities (camera, microphone).
3. The application relies on WebRTC for audio and video calling functionalities.

---

## 3. Specific Requirements

### 3.1 Functional Requirements

1. The app shall allow users to send and receive text messages.
2. The app shall support voice calls over the local network.
3. The app shall support video calls over the local network.
4. The app shall allow users to share files within the local network.
5. The app shall allow users to discover other devices on the same network.
6. The app shall support 1-to-1 and group interactions for messaging, calling, and file sharing.

### 3.2 Non-functional Requirements

1. The app shall have a user-friendly and intuitive interface.
2. The app shall provide smooth and responsive performance.
3. The app shall store messages and files locally on the device.
4. The app shall adhere to security best practices to protect user data.
5. The app shall be compatible with Android 5.0 (Lollipop) and above.

### 3.3 External Interface Requirements

1. The app shall use the device's local network interface for communication.
2. The app shall require permissions for camera, microphone, and storage access.

### 3.4 System Features

1. **Messaging**: Real-time text messaging over the local network.
2. **Audio Calling**: High-quality voice calls over the local network.
3. **Video Calling**: High-quality video calls over the local network.
4. **File Sharing**: Efficient file transfer within the local network.
5. **Device Discovery**: Automatic discovery of devices on the same network.

---

## 4. Other Requirements

1. The app shall provide detailed error messages for troubleshooting.
2. The app shall include documentation for users and developers.
3. The app shall support future enhancements and updates.

---

## 5. Developer Details

**Name**: Israk Ahmed  
**E-Mail**: israkahmed7@gmail.com  
**Github**: [Israk Ahmed](https://github.com/IsrakAhmed)  
**LinkedIn**: [Israk Ahmed](https://www.linkedin.com/in/israkahmed)

---

This document covers the essential requirements and specifications for the LanCon application. It serves as a guideline for the development and testing phases of the project.

