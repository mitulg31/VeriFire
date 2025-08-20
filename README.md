Project Summary: VeriFire

Project Goal:
VeriFire is an AI-powered, offline SMS spam detection application for Android. Its primary purpose is to provide real-time protection against unwanted and malicious text messages without needing an internet connection, ensuring user privacy by processing all data directly on the device.

Target Audience:
The app is specifically aimed at digitally vulnerable individuals, with a strong focus on elderly and non-tech-savvy users who require a simple and trustworthy solution.

Core Features:
Offline Spam Detection: Uses an onboard TensorFlow Lite model and a local vocabulary file to categorize incoming texts without an internet connection.

Real-Time Alerts: Instantly notifies the user when a spam message is detected.

Spam Folder: Quarantines suspicious messages in a secure area within the app instead of deleting them immediately.

User-Controlled Lists: Allows users to maintain a local blocklist and allowlist for full control over their messaging environment.

Manual Reporting: Includes a feature for users to manually report messages, which can be used for future model updates.

Accessible UI: The user interface is designed with high-contrast visuals and large text to be user-friendly, especially for elderly users.

Technical Implementation:

AI Model: The spam detection model is trained in Python using Scikit-learn with a Naive Bayes or SVM algorithm and TF-IDF for text vectorization. The model is then converted to the 
.tflite format for use in the Android app.

Android App: The application is built with Kotlin using the MVVM (Model-View-ViewModel) architecture. It uses a 

BroadcastReceiver to intercept incoming SMS messages and a Room Database to manage message history and user preferences locally.
