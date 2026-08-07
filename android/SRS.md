Customer Mobile App
The customer app provides users with a smooth ordering, booking and tracking experience across multiple services.

Browse vendors, products and services
Order food, groceries and pharmacy items
Send parcel and courier requests
Book rides and services
Upload prescription images
Track orders, deliveries and rides
Manage profile and delivery addresses
Wallet, referral and order history support
Dark mode and RTL language support
Login, register and forgot password flows
Vendor, product and food search
Cart and checkout workflow

//////////////////////////////////////

Driver Mobile App
The driver app helps delivery riders and drivers receive jobs, manage trips and complete assigned requests.

Receive delivery and ride requests
Accept or reject assigned jobs
Navigate using maps
Update delivery and trip status
Manage online and offline availability
Track earnings and wallet balance
Support for driver wallet system

//////////////////////////////////////

Vendor Mobile App
Vendors can manage products, orders and business activities directly from the vendor application.

Manage products, menus and services
Receive and process customer orders
Update order status
Manage inventory and availability
View sales performance
Access vendor reports

////////////////////////////////////////////////

Act as a Senior Android Native architect.

Build a production-ready Android application for Barisal City Service.

Technology

- Kotlin
- Jetpack Compose
- Hilt
- Retrofit
- Room
- Firebase
- React Hook Form
- Firebase
- Google Maps
- Socket.IO
- Native Push Notifications
- Camera
- Image Picker
- Biometric Login

Screens

Splash

Onboarding

Login

Register

OTP Verification

Home

Categories

Nearby Providers

Map View

Search

Booking

Payment

Live Tracking

Wallet

Order History

Booking History

Chat

Notifications

Profile

Support

Settings

Emergency SOS

Features

- GPS Tracking
- Real-time provider location
- Voice Search
- QR Payment
- Wallet
- Referral
- Coupon
- Rating
- Review
- Offline support
- Deep Linking

Use Material Design 3.

Architecture

Feature-based

Repository Pattern

Reusable Components

State Management

Clean Code

Build scalable code for millions of users.


Backend Hosting Plan:

### 1. Possibility & Feasibility
- **Yes, it is 100% possible and viable** to use **Firebase Database** for database & real-time synchronization, and a **Google Drive Dedicated Folder** for media and document storage.
- **Firebase Database (Firestore / Realtime DB)** handles fast, real-time structured data (orders, live GPS locations, chat messages, user profiles, bookings, push notification tokens).
- **Google Drive Storage** serves as the central binary asset store (prescriptions, product/menu images, KYC verification documents, profile photos, chat attachments) via Google Drive API v3.

---

### 2. Architecture & Implementation Plan

#### A. Database Layer — Firebase (Firestore / Realtime DB)
- **Data Collections / Nodes**:
  - `users`: Customer, Driver, Vendor profiles & addresses.
  - `orders` & `bookings`: Status lifecycle, payment logs, item breakdown.
  - `live_location`: High-frequency driver GPS coordinates stored in Firebase Realtime DB for low latency.
  - `chats`: Real-time messages with references to media attachments.
- **Security Rules**: Enforces strict Role-Based Access Control (RBAC) using Firebase Authentication Custom Claims (`customer`, `driver`, `vendor`, `admin`).

#### B. Storage Layer — Google Drive Dedicated Folder via Storage Gateway (Firebase Cloud Functions)
Directly embedding Google Drive OAuth or API credentials in the mobile app is insecure and causes permission issues across apps. The recommended production pattern is a **Serverless Storage Gateway**:

```
[Android App (Compose)] 
       │ (1. Upload File + Auth Token)
       ▼
[Firebase Cloud Function / API Gateway] 
       │ (2. Authenticate & Verify Permissions)
       │ (3. Google Drive API v3 - Service Account)
       ▼
[Google Drive Dedicated Folder] 
       ├── prescriptions/{user_id}/
       ├── vendor_assets/{vendor_id}/
       ├── driver_kyc/{driver_id}/
       └── chat_media/{chat_id}/
       │
       │ (4. Returns File ID & Direct Media URL)
       ▼
[Firebase Firestore / Realtime DB] 
       (Stores metadata: driveFileId, fileUrl, timestamp)
```

1. **Dedicated Drive Structure**:
   - Central Root Folder in Google Drive / Google Workspace Shared Drive: `ServeNear_Production_Storage/`
   - Automated subfolder routing based on entity type (`prescriptions`, `vendor_assets`, `driver_kyc`, `avatars`).

2. **Upload & Viewing Workflow**:
   - **Upload**: Mobile App compresses media locally using Kotlin `Bitmap` / file utility, then posts to `/upload` API endpoint protected by Firebase Auth. Cloud Function streams the file to Google Drive using a Google Cloud Service Account.
   - **Viewing**: Store the Drive file URL (`https://lh3.googleusercontent.com/d/{FILE_ID}`) or backend proxy URL in Firebase Database. In Jetpack Compose, load images seamlessly with `Coil` image loader.

---

### 3. Key Benefits & Considerations
- **Cost & Space Optimization**: Leverages company Google Workspace Drive storage without separate S3/Cloud Storage billing overhead.
- **Security & Granularity**: End-users do not get access to raw Drive tokens; Cloud Functions validate user roles before performing any file creation or deletion in Google Drive.
- **Scalability**: High-throughput file uploads handled asynchronously with Google Drive API v3 Resumable Upload protocol.

---

### 4. Step-by-Step Implementation Guide: Google Drive API v3 + Firebase Cloud Functions

#### Step 1: Enable Google Drive API v3 & Service Account Setup
1. **Enable Google Drive API v3**:
   - Open [Google Cloud Console](https://console.cloud.google.com/).
   - Select the GCP project linked to your Firebase project.
   - Go to **APIs & Services > Library**.
   - Search for **Google Drive API** and click **Enable**.

2. **Create Service Account**:
   - Go to **APIs & Services > Credentials**.
   - Click **Create Credentials** -> Select **Service Account**.
   - Name it (e.g., `sheba-drive-storage-sa`).
   - Click **Create and Continue** (no global project role is strictly required if sharing by folder).

3. **Generate & Download JSON Credentials Key**:
   - Select your newly created Service Account.
   - Go to the **Keys** tab -> Click **Add Key** -> **Create new key** -> Choose **JSON**.
   - Save the downloaded JSON file securely (e.g., `service-account-key.json`).

4. **Share Folder with Service Account**:
   - Open your Google Drive in browser and locate the `Sheba_dei_Storage` folder.
   - Extract the `FOLDER_ID` from the browser URL: `https://drive.google.com/drive/folders/{YOUR_FOLDER_ID}`.
   - Right-click `Sheba_dei_Storage` -> **Share**.
   - Paste the Service Account email address (e.g. `sheba-drive-storage-sa@YOUR_PROJECT.iam.gserviceaccount.com`).
   - Set permission to **Editor** and click **Send**.

---

#### Step 2: Seed 13 App Categories to Firebase Firestore & Google Drive

Create a Node.js seed script `seed-categories.js` in your backend or functions directory:

```javascript
const admin = require('firebase-admin');
const serviceAccount = require('./service-account-key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Google Drive Target Folder Configuration
const DRIVE_FOLDER_ID = "1OzKqnb1pfW1-nbt2HQQSO6hlnj2oUy3B"; // City_Service Dedicated Folder

// Core 13 categories with Google Drive icon URL placeholders
const categories = [
  { id: 'health_services', nameEn: 'Health Services', nameBn: 'স্বাস্থ্য সেবা', route: 'health', order: 1, driveFileId: 'GOOGLE_DRIVE_FILE_ID_HEALTH' },
  { id: 'transport_services', nameEn: 'Transport Services', nameBn: 'যাতায়াত সেবা', route: 'transport', order: 2, driveFileId: 'GOOGLE_DRIVE_FILE_ID_TRANSPORT' },
  { id: 'house_rent', nameEn: 'House Rent', nameBn: 'বাসা ভাড়া', route: 'houserent', order: 3, driveFileId: 'GOOGLE_DRIVE_FILE_ID_HOUSERENT' },
  { id: 'shopping', nameEn: 'Shopping', nameBn: 'বেচা-কেনা', route: 'shopping', order: 4, driveFileId: 'GOOGLE_DRIVE_FILE_ID_SHOPPING' },
  { id: 'matrimony', nameEn: 'Matrimony', nameBn: 'পাত্র-পাত্রী', route: 'matrimony', order: 5, driveFileId: 'GOOGLE_DRIVE_FILE_ID_MATRIMONY' },
  { id: 'event_service', nameEn: 'Event Service', nameBn: 'ইভেন্ট সার্ভিস', route: 'event', order: 6, driveFileId: 'GOOGLE_DRIVE_FILE_ID_EVENT' },
  { id: 'ride', nameEn: 'Ride', nameBn: 'রাইড', route: 'ride', order: 7, driveFileId: 'GOOGLE_DRIVE_FILE_ID_RIDE' },
  { id: 'courier', nameEn: 'Courier', nameBn: 'কুরিয়ার', route: 'courier', order: 8, driveFileId: 'GOOGLE_DRIVE_FILE_ID_COURIER' },
  { id: 'mistri', nameEn: 'Mistri', nameBn: 'মিস্ত্রি', route: 'mistri', order: 9, driveFileId: 'GOOGLE_DRIVE_FILE_ID_MISTRI' },
  { id: 'emergency_service', nameEn: 'Emergency Service', nameBn: 'জরুরী সেবা', route: 'emergency', order: 10, driveFileId: 'GOOGLE_DRIVE_FILE_ID_EMERGENCY' },
  { id: 'tutor', nameEn: 'Tutor', nameBn: 'টিউটর', route: 'tutor', order: 11, driveFileId: 'GOOGLE_DRIVE_FILE_ID_TUTOR' },
  { id: 'flat_land', nameEn: 'Flat and Land', nameBn: 'ফ্ল্যাট ও জমি', route: 'flatland', order: 12, driveFileId: 'GOOGLE_DRIVE_FILE_ID_FLATLAND' },
  { id: 'location_service', nameEn: 'Location Based Services', nameBn: 'লোকেশন ভিত্তিক সেবা', route: 'categorymap', order: 13, driveFileId: 'GOOGLE_DRIVE_FILE_ID_LOCATION' }
];

async function seedCategories() {
  console.log('Seeding categories to Firestore...');
  const batch = db.batch();

  categories.forEach(cat => {
    const docRef = db.collection('categories').doc(cat.id);
    batch.set(docRef, {
      ...cat,
      iconUrl: cat.driveFileId ? `https://lh3.googleusercontent.com/d/${cat.driveFileId}` : null,
      isActive: true,
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
  });

  await batch.commit();
  console.log('Successfully seeded 13 categories!');
}

seedCategories().catch(console.error);
```
