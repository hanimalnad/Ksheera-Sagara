# Ksheera-Sagara 🐄

### Dairy Profit/Loss Calculator for Small Dairy Farmers

Ksheera-Sagara is an **offline-first Android application** designed to help dairy farmers track milk income, expenses, and real monthly profitability.

The app helps farmers understand:

* How much they earn from milk sales
* Where money is being spent
* Which cows are profitable
* Whether the farm is running in profit or loss

Built specifically for small dairy farmers in Karnataka with a simple and farmer-friendly interface.

Based on the product requirements document: 

---

# ✨ Features

## 🥛 Milk Income Tracking

* Daily milk entry logging
* Fat % and SNF % tracking
* Automatic income calculation
* Monthly filtering
* Edit/Delete entries

## 💸 Expense Management

Track expenses across categories:

* Fodder
* Medical
* Labor
* Electricity
* Other

Includes:

* Expense history
* Monthly summaries
* Category-wise analytics

## 🐄 Cow Management

* Add/Edit/Delete cows
* Track breed and age
* Lifetime production statistics
* Monthly cow performance

## 📊 Financial Dashboard

* Monthly Profit/Loss overview
* Income vs Expense comparison
* Profit per liter calculation
* Cost reduction suggestions

## 📈 Analytics

* Expense pie charts
* Cow profitability rankings
* Interactive visual summaries

## 📄 PDF Report Generation

Generate monthly reports including:

* Farmer profile
* Milk income summary
* Expense breakdown
* Cow-wise performance
* Profit/Loss report

## 🔐 Offline & Secure

* 100% offline functionality
* PIN-based login
* No cloud/server dependency
* No data tracking



---

# 📱 APK Installation

## Install APK

1. Download the APK file
2. Open it on your Android device
3. Allow **Install from Unknown Sources**
4. Tap **Install**
5. Open Ksheera-Sagara

### Android Requirements

* Android 7.0+ (API 24+)

---

# 🖼️ Screenshots

# 📸 Screenshots

| Dashboard | Milk Log |
|---|---|
| ![](screenshots/ks1.jfif) | ![](screenshots/ks2.jfif) |

| Expenses | Cows |
|---|---|
| ![](screenshots/ks3.jfif) | ![](screenshots/ks4.jfif) |

| Analytics |
|---|
| ![](screenshots/ks5.jfif) |


---

# 🚀 Getting Started

## First-Time Setup

1. Register farmer details
2. Create a 4-digit PIN
3. Add cows
4. Start entering milk records and expenses

---

# 📋 Daily Workflow

## Daily Milk Entry

1. Open app
2. Go to **Milk**
3. Tap `+`
4. Enter:

   * Date
   * Cow Name
   * Liters
   * Fat %
   * SNF %
   * Rate per liter
5. Save entry

## Expense Entry

1. Open **Expenses**
2. Tap `+`
3. Select category
4. Enter amount and description
5. Save expense

## Monthly Review

1. Open Dashboard
2. Select month
3. Review:

   * Profit/Loss
   * Expenses
   * Cow rankings
4. Generate PDF report if needed



---

# 🛠️ Tech Stack

| Technology             | Purpose                   |
| ---------------------- | ------------------------- |
| Kotlin                 | Main programming language |
| Jetpack Compose        | Modern Android UI         |
| Room Database          | Offline local storage     |
| StateFlow + Coroutines | State management          |
| MPAndroidChart         | Charts & analytics        |
| iText7                 | PDF generation            |



---

# 🏗️ Architecture

```text
UI Layer (Compose)
        ↓
ViewModel (StateFlow)
        ↓
Repository
        ↓
DAO (Room)
        ↓
SQLite Database
```

The app follows a clean layered architecture for maintainability and scalability.

---

# 🎨 UI/UX Design Principles

Designed specifically for farmers with basic smartphone experience:

* Large readable fonts
* Simple navigation
* High-contrast colors
* Minimal typing
* Offline-first design
* Large touch targets

### Color System

* 🟢 Green → Profit / Income
* 🔴 Red → Loss / Expense
* 🟠 Amber → Warnings
* 🔵 Blue → Information



---

# 📊 Core Metrics

The app calculates:

## Net Profit

\text{Net Profit} = \text{Total Income} - \text{Total Expenses}

## Profit Per Liter

\text{Profit Per Liter} = \frac{\text{Net Profit}}{\text{Total Liters Produced}}

## Cow Profit Estimate

\text{Cow Profit} = \text{Cow Income} - \left(\frac{\text{Cow Income}}{\text{Total Income}} \times \text{Total Expenses}\right)



---

# 🔒 Privacy & Data Safety

Ksheera-Sagara is built with farmer privacy in mind.

* No internet required
* No external servers
* No analytics tracking
* All data stored locally on device
* PIN-protected access



---

# 📈 Future Enhancements

Planned future features:

* Kannada language support
* Voice input
* OCR milk slip scanning
* Google Drive backup
* Feed optimization calculator
* Vet reminders
* Dairy cooperative integrations



---

# 🎯 Project Goals

The goal of Ksheera-Sagara is to help dairy farmers:

* Understand true profitability
* Reduce unnecessary expenses
* Make data-driven decisions
* Improve financial stability

Target impact:

* 1000 active farmers in 6 months
* 90% profitability awareness
* Faster financial decision-making



---

# 📂 Suggested Project Structure

```text
app/
├── data/
│   ├── dao/
│   ├── database/
│   ├── models/
│   └── repository/
│
├── ui/
│   ├── screens/
│   ├── components/
│   ├── navigation/
│   └── theme/
│
├── viewmodel/
│
├── reports/
│
└── utils/
```



# 📜 License

```text
MIT License

Copyright (c) 2025 Ksheera-Sagara
```

---

# 👨‍🌾 Built For Farmers

Ksheera-Sagara aims to make dairy farming financially transparent, sustainable, and data-driven for small farmers across India.
